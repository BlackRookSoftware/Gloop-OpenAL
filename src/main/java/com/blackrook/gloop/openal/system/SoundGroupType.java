package com.blackrook.gloop.openal.system;

/**
 * Sound group type.
 * Sound groups define the characteristics for sounds played in the group.
 * @author Matthew Tropiano
 */
public interface SoundGroupType
{
	/**
	 * @return the combined multiplicative gain for this group.
	 */
	float getGain();

	/**
	 * @return the combined multiplicative pitch for this group.
	 */
	float getPitch();

	/**
	 * @return the combined multiplicative dry low pass gain for this group.
	 */
	float getLowPassGain();

	/**
	 * @return the combined multiplicative dry high pass gain for this group.
	 */
	float getHighPassGain();

	/**
	 * @return the combined multiplicative wet effect gain for this group.
	 */
	float getEffectGain();

	/**
	 * @return if this group is occludable.
	 */
	boolean isOccludable();

	/**
	 * @return if the sounds in this group are supposed to be positioned on a two-dimensional plane in front of the center.
	 */
	boolean isTwoDimensional();

	/**
	 * @return if the sounds in this group are supposed to be played from the observer always.
	 */
	boolean isZeroPosition();
}
