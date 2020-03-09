package com.blackrook.gloop.openal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.system.MemoryUtil;

public final class OALTest2 
{
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException
	{
		JSPISoundHandle handle = new JSPISoundHandle(new File(args[0]));
		JSPISoundHandle.Decoder decoder = handle.getDecoder();
		AudioFormat format = decoder.getDecodedAudioFormat();

		OALSystem system = new OALSystem();
		OALDevice device = system.createDevice();
		OALContext context = device.createContext();

		OALBuffer[] buffers = context.createBuffers(2);
		buffers[0].setFrequencyAndFormat(format);
		buffers[1].setFrequencyAndFormat(format);
		
		OALSource source = context.createSource();
		source.reset();
		source.addSourceListener(new OALSourceListener()
		{
			@Override
			public void sourceStopped(OALSource source)
			{
				System.out.println("STOP: " + source);
			}
			
			@Override
			public void sourceRewound(OALSource source) 
			{
				System.out.println("REWIND: " + source);
			}
			
			@Override
			public void sourcePlayed(OALSource source) 
			{
				System.out.println("PLAY: " + source);
			}
			
			@Override
			public void sourcePaused(OALSource source) 
			{
				System.out.println("PAUSE: " + source);
			}
			
			@Override
			public void sourceBufferEnqueued(OALSource source, OALBuffer buffer) 
			{
				System.out.println("ENQUEUE: " + source + ": " + buffer);
			}
			
			@Override
			public void sourceBufferDequeued(OALSource source, OALBuffer buffer) 
			{
				System.out.println("DEQUEUE: " + source + ": " + buffer);
			}
		});

		ByteBuffer buf = null;
		ByteBuffer buf2 = null;
		try
		{
			buf = MemoryUtil.memCalloc(44100);
			buf2 = MemoryUtil.memCalloc(22050);
			
			if (loadBuffer(decoder, buffers[0], buf))
				source.enqueueBuffer(buffers[0]);
			if (loadBuffer(decoder, buffers[1], buf2))
				source.enqueueBuffer(buffers[1]);
		} 
		finally 
		{
			MemoryUtil.memFree(buf);
			MemoryUtil.memFree(buf2);
		}
		
		system.shutDown();
	}

	private static boolean loadBuffer(JSPISoundHandle.Decoder decoder, OALBuffer buffer, ByteBuffer buf) throws IOException 
	{
		buf.clear();
		if (decoder.readPCMData(buf) < 0)
			return false;
		buf.flip();
		buffer.setData(buf);
		return true;
	}
}
