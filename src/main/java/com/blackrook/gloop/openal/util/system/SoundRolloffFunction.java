package com.blackrook.gloop.openal.util.system;

/**
 * Sound rolloff type.
 * @author Matthew Tropiano
 */
public interface SoundRolloffFunction
{
	/**
	 * Calculates the attenuation scalar given a distance scalar.
	 * @param distanceScalar the distance scalar (0 = less than reference distance min, 1 = greater than reference distance max).
	 * @return an attenuation scalar value (0 = fully attenuated, 1 = no attenuation).
	 */
	float getAttenuationScalar(float distanceScalar);

	/**
	 * No attenuation.
	 */
	static SoundRolloffFunction NONE = (scalar) -> 1f;
	
	/**
	 * Linear attenuation.
	 */
	static SoundRolloffFunction LINEAR = (scalar) -> 1f - scalar;
	
	/**
	 * Cosine attenuation.
	 */
	static SoundRolloffFunction COSINE = (scalar) -> (float)((Math.cos(Math.PI * scalar) / 2.0) + 0.5);

	/**
	 * Squared attenuation.
	 */
	static SoundRolloffFunction SQUARED = (scalar) -> 1f - (scalar * scalar);
	
}
