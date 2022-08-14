package com.blackrook.gloop.openal.util.system;

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

	/**
	 * @return the occlusion factors to use, if any. Can be null.
	 */
	SoundOcclusionType getOcclusion();

	/**
	 * The minimum effect gain at distance 0. At the max effect distance, it becomes 1.0f,
	 * so this is the multiplicative amount applied at the closest distance. Rolloff is linear. 
	 * @return the effect gain at the closest distance.
	 * @see #getMaxEffectGainDistance()
	 */
	float getMinEffectGain();

	/**
	 * The distance at which the maximum amount of effect gain is applied (1.0f).
	 * Soundscapes can manipulate the amount that effects are applied by distance.
	 * @return the distance at which full reverb is applied, in world units.
	 * @see #getMinEffectGain()
	 */
	float getMaxEffectGainDistance();

}
