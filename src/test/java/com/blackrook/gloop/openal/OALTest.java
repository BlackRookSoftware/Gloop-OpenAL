package com.blackrook.gloop.openal;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.system.MemoryUtil;

import com.blackrook.gloop.openal.struct.ThreadUtils;

public final class OALTest 
{
	static Random random = new Random();
	
	public static void main(String[] args) throws IOException, UnsupportedAudioFileException
	{
		JSPISoundHandle handle = new JSPISoundHandle(new File(args[0]));
		JSPISoundHandle.Decoder decoder = handle.getDecoder();
		AudioFormat format = decoder.getDecodedAudioFormat();

		OALSystem system = new OALSystem();
		
		OALSource source = system.createSource();
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

		OALBuffer[] buffers = system.createBuffers(2);
		buffers[0].setFrequencyAndFormat(format);
		buffers[1].setFrequencyAndFormat(format);

		ByteBuffer buf = null;
		try {
			buf = MemoryUtil.memAlloc(decoder.getDecodedAudioFormat().getChannels() * (int)decoder.getDecodedAudioFormat().getSampleRate());
			if (loadBuffer(decoder, buffers[0], buf))
				source.enqueueBuffer(buffers[0]);
			if (loadBuffer(decoder, buffers[1], buf))
				source.enqueueBuffer(buffers[1]);

			source.play();
			
			while (source.isPlaying())
			{
				while (source.getProcessedBufferCount() > 0)
				{
					OALBuffer deq = source.dequeueBuffer();
					if (loadBuffer(decoder, deq, buf))
						source.enqueueBuffer(deq);
				}
				ThreadUtils.sleep(50);
			}
		} finally {
			MemoryUtil.memFree(buf);
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
