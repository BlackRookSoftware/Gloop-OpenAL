/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.gloop.openal.struct.IOUtils;

/**
 * Sound resource abstraction.
 * Wraps Java Sound SPI structures for ease of creating handles to decodable sound.
 * @author Matthew Tropiano
 */
public class JSPISoundHandle
{
	/** Name of this data stream. */
	private String dataName;
	/** Audio file format. */
	private AudioFileFormat audioFileFormat;

	/** File resource. */
	private File dataFile;
	/** URL resource. */
	private URL dataURL;
	/** Byte resource. */
	private ByteArrayInputStream dataStream;
	
	protected JSPISoundHandle()
	{
		this.dataName = null;
		this.dataFile = null;
		this.dataURL = null;
		this.dataStream = null;
	}
	
	/**
	 * Opens a sound file for reading.
	 * @param filePath path to the file.
	 * @throws IOException if the resource can't be read.
	 * @throws UnsupportedAudioFileException if the audio format is not recognized.
	 */
	public JSPISoundHandle(String filePath) throws IOException, UnsupportedAudioFileException
	{
		dataFile = new File(filePath);
		dataName = dataFile.getPath();
		audioFileFormat = AudioSystem.getAudioFileFormat(dataFile);
	}

	/**
	 * Opens a sound file for reading.
	 * @param f the file.
	 * @throws IOException if the file can't be read.
	 * @throws UnsupportedAudioFileException if the audio format is not recognized.
	 */
	public JSPISoundHandle(File f) throws IOException, UnsupportedAudioFileException
	{
		dataName = f.getPath();
		dataFile = f;
		audioFileFormat = AudioSystem.getAudioFileFormat(dataFile);
	}
	
	/**
	 * Opens a URL for reading.
	 * @param url the URL.
	 * @throws IOException if the stream can't be read.
	 * @throws UnsupportedAudioFileException if the audio format is not recognized.
	 */
	public JSPISoundHandle(URL url) throws IOException, UnsupportedAudioFileException
	{
		dataName = url.toString();
		dataURL = url;
		audioFileFormat = AudioSystem.getAudioFileFormat(dataURL);
	}

	/**
	 * Creates a handle from an array of sound data.
	 * @param name the handle name.
	 * @param data the data.
	 * @throws IOException if the stream can't be read.
	 * @throws UnsupportedAudioFileException if the audio format is not recognized.
	 */
	public JSPISoundHandle(String name, byte[] data) throws IOException, UnsupportedAudioFileException
	{
		dataName = name;
		dataStream = new ByteArrayInputStream(data);
		audioFileFormat = AudioSystem.getAudioFileFormat(dataStream);
		dataStream.reset();
	}

	/**
	 * @return a {@link Decoder} that can decode this data into PCM data.
	 * @throws IOException if a decoder could not be opened.
	 */
	public Decoder getDecoder() throws IOException
	{
		try {
			return new Decoder();
		} catch (UnsupportedAudioFileException e) {
			throw new IOException("Could not decode audio (should have been prechecked).");
		}
	}
	
	/**
	 * @return the name of this handle.
	 */
	public String getName()	
	{
		return dataName;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(getName());
		sb.append(' ');
		sb.append(audioFileFormat.toString());
		return sb.toString();
	}

	/**
	 * @return the dataName
	 */
	public final String getDataName()
	{
		return dataName;
	}

	// Creates the stream for the decoder.
	private AudioInputStream startStream() throws IOException, UnsupportedAudioFileException
	{
		if (dataFile != null)
			return AudioSystem.getAudioInputStream(dataFile);
		else if (dataURL != null)
			return AudioSystem.getAudioInputStream(dataURL);
		else
		{
			dataStream.reset();
			return AudioSystem.getAudioInputStream(dataStream);
		}
	}
	
	/**
	 * Decoder class that decodes sound as PCM audio.
	 * @author Matthew Tropiano
	 */
	public class Decoder implements AutoCloseable
	{
		/** Audio format. */
		private AudioFormat audioFormat;
		/** Audio input stream. */
		private AudioInputStream audioStream;
		/** Audio format to decode to. */
		private AudioFormat decodedAudioFormat;
		/** Audio input stream to decode to. */
		private AudioInputStream decodedAudioStream;
		/** Bytes per sample of decoded data buffer. */
		private byte[] decodedBytesPerSample;
		
		private Decoder() throws IOException, UnsupportedAudioFileException
		{
			audioStream = startStream();
			audioFormat = audioStream.getFormat();
			decodedAudioFormat = new AudioFormat(
				audioFormat.getSampleSizeInBits() == 8 ? AudioFormat.Encoding.PCM_UNSIGNED : AudioFormat.Encoding.PCM_SIGNED, 
				audioFormat.getSampleRate(), 
				audioFormat.getSampleSizeInBits() != AudioSystem.NOT_SPECIFIED ? audioFormat.getSampleSizeInBits() : 16, 
				audioFormat.getChannels(),
				audioFormat.getChannels() * 2,
				audioFormat.getSampleRate(),
				ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN
			);
			decodedAudioStream = AudioSystem.getAudioInputStream(decodedAudioFormat, audioStream);
			decodedBytesPerSample = new byte[(decodedAudioFormat.getSampleSizeInBits() >> 3) * decodedAudioFormat.getChannels()];
		}
		
		/**
		 * Reads a bunch of decoded PCM data into the byte buffer.
		 * Bytes are loaded into the buffer until the end is reached or the end of the PCM data is reached.
		 * The data is aligned to the amount of decoded bytes per sample, as a sanitation measure.
		 * @param buffer the byte buffer.
		 * @return the amount of bytes written to the buffer, or -1 if the end of the stream was reached before data could be written.
		 * @throws IOException if the data can't be decompressed.
		 */
		public int readPCMData(ByteBuffer buffer) throws IOException
		{
			int b = 0;
			int out = 0;
			int s = 0;
			while (buffer.remaining() >= decodedBytesPerSample.length && (b = decodedAudioStream.read()) >= 0)
			{
				decodedBytesPerSample[s++] = (byte)b;
				if (s == decodedBytesPerSample.length)
				{
					buffer.put(decodedBytesPerSample);
					out += decodedBytesPerSample.length;
					s = 0;
				}
			}
			
			if (b < 0 && out == 0)
				return -1;

			return out;
		}

		/**
		 * Reads a bunch of decoded PCM data into the byte array.
		 * Bytes are loaded into the buffer until the end is reached or the end of the PCM data is reached.
		 * The data is aligned to the amount of decoded bytes per sample, as a sanitation measure.
		 * @param arr the byte array.
		 * @param offset the offset into the array.
		 * @return the amount of bytes written to the array, or -1 if the end of the stream was reached before data could be written.
		 * @throws IOException if the data can't be decompressed.
		 */
		public int readPCMData(byte[] arr, int offset) throws IOException
		{
			int b = 0;
			int out = 0;
			int s = 0;
			while (arr.length - (offset + out) >= decodedBytesPerSample.length && (b = decodedAudioStream.read()) >= 0)
			{
				decodedBytesPerSample[s++] = (byte)b;
				if (s == decodedBytesPerSample.length)
				{
					System.arraycopy(decodedBytesPerSample, 0, arr, offset + out, decodedBytesPerSample.length);
					out += decodedBytesPerSample.length;
					s = 0;
				}
				arr[offset + out] = (byte)b;
				out++;
			}

			return out;
		}

		/**
		 * @return the audio format specs for the source file.
		 * @see AudioFormat
		 */
		public final AudioFormat getFormat()
		{
			return audioFormat;
		}
		
		/**
		 * @return the audio file format specs for the source file.
		 * @see AudioFileFormat
		 */
		public final AudioFileFormat getFileFormat()
		{
			return audioFileFormat;
		}

		/**
		 * @return the audio format specs for what this decodes to.
		 */
		public final AudioFormat getDecodedAudioFormat()
		{
			return decodedAudioFormat;
		}
		
		/**
		 * @return the calculated decoded bytes per sample.
		 */
		public int getDecodedBytesPerSample() 
		{
			return decodedBytesPerSample.length;
		}
		
		/**
		 * @return the open audio stream for decoded data.
		 */
		public InputStream getDecodedStream()
		{
			return decodedAudioStream;
		}

		/**
		 * Closes the decoder.
		 * @throws IOException if an error occurred during close.
		 */
		public void close() throws IOException
		{
			IOUtils.close(audioStream);
			IOUtils.close(decodedAudioStream);
		}

	}
	
}