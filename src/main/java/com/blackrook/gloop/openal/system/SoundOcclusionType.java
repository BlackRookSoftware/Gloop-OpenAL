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
	 * Calculates the occlusion scalar.
	 * @param width the width in world units. 
	 * @return the calculated occlusion scalar (0 = no occlusion, 1 = maximum occlusion).
	 */
	default float getOcclusionScalar(float width)
	{
		return getMaximumWidth() == 0.0f ? 0.0f : width / getMaximumWidth();
	}

	/**
	 * No occlusion.
	 */
	static SoundOcclusionType NONE = new SoundOcclusionType()
	{
		@Override
		public float getMaximumWidth()
		{
			return 0f;
		}
	};
	
}
