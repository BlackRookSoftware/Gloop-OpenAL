package com.blackrook.gloop.openal;

import java.io.File;

import com.blackrook.gloop.openal.system.SoundData;
import com.blackrook.gloop.openal.system.SoundSystem;
import com.blackrook.gloop.openal.system.SoundSystem.SoundGroup;

public final class SystemTest
{
	public static void main(String[] args) throws Exception
	{
		SoundSystem system = new SoundSystem(32);
		
		SoundGroup group = SoundSystem.group(false, true, false, 0);
		SoundData data = SoundSystem.fileData(new File(args[0]), 0);
		system.play(data, group);
	}
}
