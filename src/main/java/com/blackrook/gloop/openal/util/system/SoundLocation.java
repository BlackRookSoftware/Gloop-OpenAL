package com.blackrook.gloop.openal.util.system;

/**
 * A sound location source.
 * @author Matthew Tropiano
 */
public interface SoundLocation
{
	/**
	 * @return the sound position of this object, X-coordinate, in world units.
	 */
	float getSoundPositionX();
	
	/**
	 * @return the sound position of this object, Y-coordinate, in world units.
	 */
	float getSoundPositionY();

	/**
	 * @return the sound position of this object, Z-coordinate, in world units.
	 */
	float getSoundPositionZ();

	/**
	 * @return the angle of the sound, in degrees.
	 */
	float getSoundAngle();
}
