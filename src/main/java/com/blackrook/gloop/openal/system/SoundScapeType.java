package com.blackrook.gloop.openal.system;

/**
 * A soundscape type that affects playing sounds in groups that use it.
 * Soundscapes can be swapped out independently of playing sounds.
 * @author Matthew Tropiano
 */
public interface SoundScapeType
{
	/**
	 * @return the echo to use, if any. Can be null.
	 */
	SoundEchoType getEcho();

	/**
	 * @return the reverb to use, if any. Can be null.
	 */
	SoundReverbType getReverb();

}