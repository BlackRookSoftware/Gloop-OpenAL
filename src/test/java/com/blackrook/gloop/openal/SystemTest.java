package com.blackrook.gloop.openal;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.gloop.openal.struct.ThreadUtils;
import com.blackrook.gloop.openal.util.system.SoundData;
import com.blackrook.gloop.openal.util.system.SoundReverbType;
import com.blackrook.gloop.openal.util.system.SoundSystem;
import com.blackrook.gloop.openal.util.system.SoundSystem.SoundGroup;
import com.blackrook.gloop.openal.util.system.SoundSystem.Voice;

public final class SystemTest
{
	public static void main(String[] args) throws Exception
	{
		SoundSystem system = new SoundSystem(32);
		
		system.addListener(new SoundSystem.Listener()
		{
			@Override
			public void onVoiceStreamStarted(Voice voice)
			{
				System.out.println("Started VOICE: " + voice);
			}
			
			@Override
			public void onVoiceStopped(Voice voice)
			{
				System.out.println("Stopped VOICE: " + voice);
			}
			
			@Override
			public void onVoiceRejected()
			{
				System.out.println("VOICE Rejected");
			}
			
			@Override
			public void onVoicePrepared(Voice voice)
			{
				System.out.println("VOICE Prepped: " + voice);
			}
			
			@Override
			public void onVoiceAllocated(Voice voice)
			{
				System.out.println("VOICE Allocated: " + voice);
			}

			@Override
			public void onVoiceDeallocated(Voice voice)
			{
				System.out.println("VOICE Deallocated: " + voice);
			}
			@Override
			public void onVoicePlayed(Voice voice)
			{
				System.out.println("VOICE Played: " + voice);
			}
			
			@Override
			public void onStreamThreadStarted()
			{
				System.out.println("Stream thread started.");
			}
			
			@Override
			public void onStreamStep(Voice voice)
			{
				System.out.println("Stream step: " + voice);
			}

			@Override
			public void onStreamThreadEnded()
			{
				System.out.println("Stream thread ended.");
			}
			
			@Override
			public void onSoundCached(SoundData data)
			{
				System.out.println("Sound cached: " + data.getPath());
			}

			@Override
			public void onSoundUnsupportedError(SoundData data, UnsupportedAudioFileException e)
			{
				e.printStackTrace(System.err);
			}
			
			@Override
			public void onSoundIOError(SoundData data, IOException e)
			{
				e.printStackTrace(System.err);
			}

		});
		
		SoundGroup group = SoundSystem.group(false, true, false, 0);
		group.setEffectGain(0.0f);
		SoundData data = SoundSystem.fileData(new File(args[0]), true, 0);
		system.setSoundScape(SoundSystem.soundScape(null, SoundReverbType.FACTORY_LARGEROOM, null));
		system.play(data, group);
		
		ThreadUtils.sleep(1000);
		system.shutDown();
	}
}
