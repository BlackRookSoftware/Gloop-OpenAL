package com.blackrook.gloop.openal.system;

/**
 * Sound reverb types.
 * @author Matthew Tropiano
 */
public interface SoundEchoType
{
	/**
	 * @return this effect's delay in seconds (0.0 to 0.207).
	 */
	float getDelay();

	/**
	 * @return this effect's LR delay in seconds (0.0 to 0.404).
	 */
	float getLRDelay();

	/**
	 * @return this effect's feedback scalar (0.0 to 1.0).
	 */
	float getFeedback();

	/**
	 * @return this effect's damping scalar (0.0 to 0.99).
	 */
	float getDamping();

	/**
	 * @return this effect's spread (-1.0 to 1.0).
	 */
	float getSpread();
	
}
