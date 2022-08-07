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
	 * @return this effect's left-right delay in seconds (0.0 to 0.404).
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
	
	public final class DefaultType implements SoundEchoType
	{
		private float delay;
		private float lrDelay;
		private float feedback;
		private float damping;
		private float spread;
		
		@Override
		public float getDelay()
		{
			return delay;
		}

		public DefaultType setDelay(float delay)
		{
			this.delay = delay;
			return this;
		}

		@Override
		public float getLRDelay()
		{
			return lrDelay;
		}

		public DefaultType setLRDelay(float lrDelay)
		{
			this.lrDelay = lrDelay;
			return this;
		}

		@Override
		public float getFeedback()
		{
			return feedback;
		}

		public DefaultType setFeedback(float feedback)
		{
			this.feedback = feedback;
			return this;
		}

		@Override
		public float getDamping()
		{
			return damping;
		}

		public DefaultType setDamping(float damping)
		{
			this.damping = damping;
			return this;
		}

		@Override
		public float getSpread()
		{
			return spread;
		}

		public DefaultType setSpread(float spread)
		{
			this.spread = spread;
			return this;
		}
		
	}
	
	static SoundEchoType GENERIC = (new DefaultType())
		.setDelay(0.15f)
		.setDamping(0.1f)
		.setFeedback(0.25f)
		.setLRDelay(0)
		.setSpread(0)
	;

	
}
