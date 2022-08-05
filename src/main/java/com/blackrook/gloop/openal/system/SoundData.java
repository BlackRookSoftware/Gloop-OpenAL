package com.blackrook.gloop.openal.system;

import java.io.InputStream;

/**
 * A singular sound source.
 * @author Matthew Tropiano
 */
public interface SoundData
{
	/**
	 * @return the path to the source.
	 */
	String getPath();
	
	/**
	 * @return an input stream for reading the sound data.
	 */
	InputStream getInputStream();
	
	/**
	 * Affects how the sound is loaded. 
	 * If not a stream, the data is loaded fully.
	 * @return true if this is a streaming source, false if not (is a clip).
	 */
	boolean isStream();
	
	/**
	 * @return true if this sound should replace an existing instance of its playback, false to not.
	 */
	boolean replacesOldSounds();

	/**
	 * @return true if this sound cannot be culled, or its play event must succeed.
	 */
	boolean isAlwaysPlayed();

	/**
	 * @return the amount of concurrent instances of this sound can be playing. 0 or less is infinite.
	 */
	int getLimit();

	/**
	 * @return the amount of pitch to vary each play. Scalar value.
	 */
	float getPitchVariance();

}
