package com.blackrook.gloop.openal;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.gloop.openal.struct.ThreadUtils;
import com.blackrook.gloop.openal.system.SoundData;
import com.blackrook.gloop.openal.system.SoundSystem;
import com.blackrook.gloop.openal.system.SoundSystem.SoundGroup;
import com.blackrook.gloop.openal.system.SoundSystem.Voice;

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
			public void onStreamThreadEnded()
			{
				System.out.println("Stream thread ended.");
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
			
			@Override
			public void onSoundCached(SoundData data)
			{
				System.out.println("Sound cached: " + data.getPath());
			}

		});
		
		SoundGroup group = SoundSystem.group(false, true, false, 0);
		SoundData data = SoundSystem.fileData(new File(args[0]), true, 0);
		
		group.setEffectGain(0f);
		system.play(data, group);
	}
}
