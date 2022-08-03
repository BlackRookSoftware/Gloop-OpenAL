package com.blackrook.gloop.openal.system;

/**
 * A single rolloff type.
 * @author Matthew Tropiano
 */
public interface SoundRolloffType
{
	/**
	 * @return the minimum distance that attenuation starts occurring in world units.
	 */
	float getMinimumDistance();

	/**
	 * @return the maximum distance that attenuation stops occurring in world units.
	 */
	float getMaximumDistance();
	
	/**
	 * @return the rolloff function for this rolloff type.
	 */
	SoundRolloffFunction getRolloffFunction();
	
	/**
	 * Calculates the attenuation scalar by distance.
	 * @param distance the distance in world units.
	 * @return the calculated attenuation scalar given the distance.
	 */
	default float getAttenuationScalar(float distance)
	{
		return (distance < getMinimumDistance()) ? 1f 
			: (distance > getMaximumDistance())  ? 0f
			: (getMinimumDistance() == getMaximumDistance()) ? 1f
			: getRolloffFunction().getAttenuationScalar(
				(distance - getMinimumDistance()) / (getMaximumDistance() - getMinimumDistance())
			);
	}
	
}
