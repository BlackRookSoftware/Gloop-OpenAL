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
import java.nio.IntBuffer;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL11;
import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.openal.JSPISoundHandle.Decoder;
import com.blackrook.gloop.openal.enums.SoundFormat;
import com.blackrook.gloop.openal.struct.IOUtils;

/**
 * Sound sample buffer class.
 * @author Matthew Tropiano
 */
public final class OALBuffer extends OALObject
{
	public static final int FREQ_8KHZ =	8000;
	public static final int FREQ_11KHZ = 11025;
	public static final int FREQ_16KHZ = 16000;
	public static final int FREQ_22KHZ = 22050;
	public static final int FREQ_32KHZ = 32000;
	public static final int FREQ_44KHZ = 44100;
	public static final int FREQ_48KHZ = 48000;
	
	/** The sizes of each of the buffers. */
	protected int bufferSize;
	/** Sound format. */
	protected SoundFormat bufferFormat;
	/** Sound sampling rate. */
	protected int bufferRate;

	/**
	 * Constructs a new sound buffer.
	 * @throws SoundException if an OpenAL buffer cannot be allocated.
	 * @throws IOException if the SoundFile can't be opened.
	 */
	OALBuffer(OALSystem system)
	{
		super(system);
		this.bufferSize = 0;
		this.bufferFormat = SoundFormat.MONO8;
		this.bufferRate = FREQ_11KHZ;
	}

	/**
	 * Constructs a new sound buffer with an entire buffer filled with data, decoded.
	 * @param handle the data to use.
	 * @throws IOException if a handle Decoder cannot be opened. 
	 */
	OALBuffer(OALSystem system, JSPISoundHandle handle) throws IOException
	{
		this(system);
		Decoder decoder = handle.getDecoder();
		loadFromDecoder(decoder);
		IOUtils.close(decoder);
	}
	
	/**
	 * Constructs a new sound buffer with an entire
	 * buffer filled with a decoder's contents.
	 * @param decoder the decoder to use.
	 */
	OALBuffer(OALSystem system, JSPISoundHandle.Decoder decoder) throws IOException
	{
		super(system);
		loadFromDecoder(decoder);
	}

	private void loadFromDecoder(JSPISoundHandle.Decoder decoder) throws IOException
	{
		setFrequencyAndFormat(decoder.getDecodedAudioFormat());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] by = new byte[16384];
		int l = 0;
		do {
			l = decoder.readPCMBytes(by);
			bos.write(by, 0, l);
		} while (l == by.length);

		loadPCMData(ByteBuffer.wrap(bos.toByteArray()));
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
		clearError();
		try (MemoryStack stack = MemoryStack.stackGet())
		{
			IntBuffer buf = stack.mallocInt(1);
			AL11.alGenBuffers(buf);
			out = buf.get(0);
			errorCheck();
		}
		return out;
	}
	
	@Override
	protected final void free()
	{
		AL11.alDeleteBuffers(getALId());
		errorCheck();
	}

	/**
	 * Loads this buffer with PCM bytes.
	 * @param pcmData the PCM data to load into it.
	 * @param len amount of bytes to load.
	 */
	public synchronized void loadPCMData(ByteBuffer pcmData, int len)
	{
		bufferSize = len;
		clearError();
		AL11.alBufferData(getALId(), bufferFormat.alVal, pcmData, bufferRate);
		errorCheck();
	}

	/**
	 * Loads this buffer with PCM bytes.
	 * @param pcmData the array of PCM bytes to load into it.
	 */
	public synchronized void loadPCMData(ByteBuffer pcmData)
	{
		loadPCMData(pcmData, pcmData.capacity());
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
	public void setFormat(SoundFormat format)
	{
		bufferFormat = format;
	}
	
	/**
	 * @return this buffer's format (AL constant).
	 */
	public SoundFormat getFormat()
	{
		return bufferFormat;
	}
	
	/**
	 * Sets this buffer's frequency.
	 * @param freq the frequency in kHz.
	 */
	public void setSamplingRate(int freq)
	{
		bufferRate = freq;
	}
	
	/**
	 * @return this buffer's frequency.
	 */
	public int getFrequency()
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
						setFormat(SoundFormat.MONO8);
						return;
					case 16:
						setFormat(SoundFormat.MONO16);
						return;
				}
				break;
				
			case 2:
				switch (bits)
				{
					case 8:
						setFormat(SoundFormat.STEREO8);
						return;
					case 16:
						setFormat(SoundFormat.STEREO16);
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
		sb.append(getALId()+" ");
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
		sb.append(getFrequency()+"Hz ");
		sb.append(getSize()+" bytes");
		return sb.toString();
	}
	
}
