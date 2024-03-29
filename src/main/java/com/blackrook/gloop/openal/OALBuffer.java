/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.openal.JSPISoundHandle.Decoder;
import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.exception.SoundException;
import com.blackrook.gloop.openal.struct.IOUtils;

/**
 * Sound sample buffer class.
 * @author Matthew Tropiano
 */
public final class OALBuffer extends OALObject
{
	private static final ThreadLocal<byte[]> BYTEBUFFER = ThreadLocal.withInitial(()->new byte[16384]);
	
	public static final int SAMPLING_RATE_8KHZ =  8000;
	public static final int SAMPLING_RATE_11KHZ = 11025;
	public static final int SAMPLING_RATE_16KHZ = 16000;
	public static final int SAMPLING_RATE_22KHZ = 22050;
	public static final int SAMPLING_RATE_32KHZ = 32000;
	public static final int SAMPLING_RATE_44KHZ = 44100;
	public static final int SAMPLING_RATE_48KHZ = 48000;
	
	/**
	 * Sound format enumeration.
	 */
	public enum Format
	{
		MONO8(AL11.AL_FORMAT_MONO8, 8, 1),
		MONO16(AL11.AL_FORMAT_MONO16, 16, 1),
		STEREO8(AL11.AL_FORMAT_STEREO8, 8, 2),
		STEREO16(AL11.AL_FORMAT_STEREO16, 16, 2),
		;
		
		public final int alVal;
		/** Sample resolution. */
		public final int bits;
		/** Number of channels. */
		public final int channels;
		
		private Format(int val, int bits, int channels) 
		{alVal = val; this.bits = bits; this.channels = channels;}

		@Override
		public String toString()
		{
			return bits + "-bit, " + channels + " ch.";
		}
	}

	/** The sizes of each of the buffers. */
	protected int bufferSize;
	/** Sound format. */
	protected Format bufferFormat;
	/** Sound sampling rate. */
	protected int bufferRate;

	OALBuffer(OALContext context)
	{
		super(context);
		this.bufferSize = 0;
		this.bufferFormat = Format.MONO8;
		this.bufferRate = SAMPLING_RATE_11KHZ;
	}

	/**
	 * Constructs a new sound buffer with an entire buffer filled with data, decoded.
	 * @param handle the data to use.
	 * @throws IOException if a handle Decoder cannot be opened. 
	 */
	OALBuffer(OALContext context, JSPISoundHandle handle) throws IOException
	{
		this(context);
		Decoder decoder = handle.getDecoder();
		loadFromDecoder(decoder);
		IOUtils.close(decoder);
	}
	
	/**
	 * Constructs a new sound buffer with an entire
	 * buffer filled with a decoder's contents.
	 * @param decoder the decoder to use.
	 */
	OALBuffer(OALContext context, JSPISoundHandle.Decoder decoder) throws IOException
	{
		super(context);
		loadFromDecoder(decoder);
	}

	private void loadFromDecoder(JSPISoundHandle.Decoder decoder) throws IOException
	{
		setFrequencyAndFormat(decoder.getDecodedAudioFormat());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] by = BYTEBUFFER.get();
		int amt;
		while ((amt = decoder.readPCMBytes(by)) > 0)
			bos.write(by, 0, amt);
		setData(bos.toByteArray());
	}
	
	/**
	 * Sets the audio frequency and format of this buffer using an {@link AudioFormat} info object.
	 * @param format the JavaX sound format data to pull from.
	 */
	public void setFrequencyAndFormat(AudioFormat format)
	{
		setSamplingRate((int)format.getSampleRate());
		setFormatByChannelsAndBits(format.getChannels(), format.getSampleSizeInBits());
	}
	
	@Override
	protected final int allocate()
	{
		int out;
		try (ContextLock lock = requestContext()) 
		{
			clearError();
			out = AL11.alGenBuffers();
			errorCheck();
		}
		return out;
	}
	
	@Override
	protected final void free()
	{
		try (ContextLock lock = requestContext()) 
		{
			AL11.alDeleteBuffers(getName());
			errorCheck();
		}
	}

	/**
	 * Loads this buffer with sample data from an array of bytes.
	 * This is intended for pure convenience, and its use is discouraged if performance is desired.
	 * @param data the data to load into it.
	 */
	public synchronized void setData(byte[] data)
	{
		setData(data, 0, data.length);
	}
	
	/**
	 * Loads this buffer with sample data from an array of bytes.
	 * This is intended for pure convenience, and its use is discouraged if performance is desired.
	 * @param data the data to load into it.
	 * @param offset the offset into the array to use.
	 * @param length the amount of bytes to load.
	 * @throws ArrayIndexOutOfBoundsException if <code>offset + length</code> exceeds <code>data.length</code>.
	 */
	public synchronized void setData(byte[] data, int offset, int length)
	{
		ByteBuffer buf = null;
		try
		{
			buf = MemoryUtil.memAlloc(length);
			buf.put(data, offset, length);
			buf.flip();
			setData(buf);
		}
		finally 
		{
			MemoryUtil.memFree(buf);
		}
	}

	/**
	 * Loads this buffer with sample data.
	 * The data is loaded from the source byte buffer's current position to its current limit.
	 * @param data the data to load into it.
	 */
	public synchronized void setData(ByteBuffer data)
	{
		int len = data.remaining();
		
		// slightly more courteous error message for a specific condition.
		int width = (bufferFormat.bits >> 3) * bufferFormat.channels;
		if (len % width != 0)
			throw new SoundException("Input data is not aligned to sample size - len: " + len + " width: " + width);

		try (ContextLock lock = requestContext()) 
		{
			clearError();
			AL11.alBufferData(getName(), bufferFormat.alVal, data, bufferRate);
			errorCheck();
		}
		bufferSize = len;
	}

	/** 
	 * @return the buffer size in bytes. 
	 */
	public int getSize()
	{
		return bufferSize;
	}

	/**
	 * Sets this buffer's bitrate format.
	 * @param format This buffer's format.
	 * @throws IllegalArgumentException if this is not set using a valid constant.
	 */
	public void setFormat(Format format)
	{
		bufferFormat = format;
	}
	
	/**
	 * @return this buffer's format (AL constant).
	 */
	public Format getFormat()
	{
		return bufferFormat;
	}
	
	/**
	 * Sets this buffer's sampling rate.
	 * @param rate the rate in kHz.
	 */
	public void setSamplingRate(int rate)
	{
		bufferRate = rate;
	}
	
	/**
	 * @return this buffer's sampling rate.
	 */
	public int getSamplingRate()
	{
		return bufferRate;
	}
	
	/**
	 * Sets format by channels and bits.
	 * @param channels amount of channels.
	 * @param bits bit per sample.
	 * @throws IllegalArgumentException if the combination of channels and bits create an unsupported format.
	 */
	public void setFormatByChannelsAndBits(int channels, int bits)
	{
		switch(channels)
		{
			case 1:
				switch (bits)
				{
					case 8:
						setFormat(Format.MONO8);
						return;
					case 16:
						setFormat(Format.MONO16);
						return;
				}
				break;
				
			case 2:
				switch (bits)
				{
					case 8:
						setFormat(Format.STEREO8);
						return;
					case 16:
						setFormat(Format.STEREO16);
						return;
				}
				break;
		}
		throw new IllegalArgumentException("Unsupported set of channels and bytes. " + channels + " channels, " + bits + "-bits.");
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("Buffer ");
		sb.append(getName()+" ");
		switch (getFormat())
		{
			case MONO8:
				sb.append("Mono 8-bit");
				break;
			case MONO16:
				sb.append("Mono 16-bit");
				break;
			case STEREO8:
				sb.append("Stereo 8-bit");
				break;
			case STEREO16:
				sb.append("Stereo 16-bit");
				break;
		}
		sb.append(' ');
		sb.append(getSamplingRate()+"Hz ");
		sb.append(getSize()+" bytes");
		return sb.toString();
	}
	
}
