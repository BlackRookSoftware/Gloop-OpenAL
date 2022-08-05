package com.blackrook.gloop.openal.system;

/**
 * An encapsulation of sound rolloff properties for played sounds.
 * @author Matthew Tropiano
 */
public interface SoundCategoryType
{
	/**
	 * @return the rolloff type to use, if any. Can be null.
	 */
	SoundRolloffType getRolloffType();

	/**
	 * @return the rolloff type to use for the low pass, if any. Can be null.
	 */
	SoundRolloffType getLowPassRolloffType();

	/**
	 * @return the rolloff type to use for the high pass, if any. Can be null.
	 */
	SoundRolloffType getHighPassRolloffType();

	/**
	 * @return the rolloff type to use for the sound cone, if any. Distances are in degrees. Can be null.
	 */
	SoundRolloffType getConicRolloffType();

}
