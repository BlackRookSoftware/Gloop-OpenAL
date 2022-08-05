package com.blackrook.gloop.openal.system;

/**
 * An occlusion type.
 * @author Matthew Tropiano
 */
public interface SoundOcclusionType
{
	/**
	 * @return the maximum obstruction width in world units that maximum occlusion occurs at.
	 */
	float getMaximumWidth();

	/**
	 * @return the maximum occlusion attenuation.
	 */
	float getGain();
	
	/**
	 * @return the maximum occlusion attenuation for low pass.
	 */
	float getLowPassGain();

	/**
	 * @return the maximum occlusion attenuation for high pass.
	 */
	float getHighPassGain();
}
