package com.blackrook.gloop.openal.util.system;

/**
 * Sound reverb types.
 * @author Matthew Tropiano
 */
public interface SoundReverbType
{
	/** 
	 * @return the current reverb density factor. 
	 */
	float getDensity();

	/** 
	 * @return the current reverb diffusion factor. 
	 */
	float getDiffusion();

	/**
	 * @return the current reverb gain. 
	 */
	float getGain();

	/** 
	 * @return the current reverb high-frequency gain. 
	 */
	float getHFGain();

	/** 
	 * @return the current reverb decay time in seconds. 
	 */
	float getDecayTime();

	/** 
	 * @return the current the current reverb high-frequency ratio. 
	 */
	float getDecayHFRatio();

	/** 
	 * @return true if the reverb decay high-frequency limit is set, false if not. 
	 */
	boolean isDecayHFLimit();

	/** 
	 * @return the current reverb reflection gain. 
	 */
	float getReflectionGain();

	/** 
	 * @return the current reverb reflection delay in seconds. 
	 */
	float getReflectionDelay();

	/** 
	 * @return the current late reverb gain. 
	 */
	float getLateGain();

	/** 
	 * @return the current late reverb delay. 
	 */
	float getLateDelay();

	/** 
	 * @return the current reverb high-frequency air absorption gain. 
	 */
	float getAirAbsorptionHFGain();

	/** 
	 * @return the current reverb room rolloff factor. 
	 */
	float getRoomRolloffFactor();

	public final class DefaultType implements SoundReverbType
	{
		/** Reverb density factor. */
		private float density;
		/** Reverb diffusion factor. */
		private float diffusion;
		/** Reverb gain. */
		private float gain;
		/** Reverb high-frequency gain. */
		private float hfGain;
		/** Reverb decay time in seconds. */
		private float decayTime;
		/** Reverb high-frequency ratio. */
		private float decayHFRatio;
		/** Reverb reflection gain. */
		private float reflectionGain;
		/** Reverb reflection delay in seconds. */
		private float reflectionDelay;
		/** Late reverb gain. */
		private float lateGain;
		/** Late reverb delay. */
		private float lateDelay;
		/** Reverb high-frequency air absorption gain. */
		private float airAbsorptionHFGain;
		/** Reverb room rolloff factor. */
		private float roomRolloffFactor;
		/** Reverb decay high-frequency limit? */
		private boolean decayHFLimit;
		
		public float getDensity()
		{
			return density;
		}
		
		public DefaultType setDensity(float density)
		{
			this.density = density;
			return this;
		}
		
		public float getDiffusion()
		{
			return diffusion;
		}
		public DefaultType setDiffusion(float diffusion)
		{
			this.diffusion = diffusion;
			return this;
		}
		
		public float getGain()
		{
			return gain;
		}
		
		public DefaultType setGain(float gain)
		{
			this.gain = gain;
			return this;
		}
		
		public float getHFGain()
		{
			return hfGain;
		}
		
		public DefaultType setHFGain(float gainHF)
		{
			this.hfGain = gainHF;
			return this;
		}
		
		public float getDecayTime()
		{
			return decayTime;
		}
		
		public DefaultType setDecayTime(float decayTime)
		{
			this.decayTime = decayTime;
			return this;
		}
		
		public float getDecayHFRatio()
		{
			return decayHFRatio;
		}
		
		public DefaultType setDecayHFRatio(float decayHFRatio)
		{
			this.decayHFRatio = decayHFRatio;
			return this;
		}
		
		public boolean isDecayHFLimit()
		{
			return decayHFLimit;
		}
		
		public DefaultType setDecayHFLimit(boolean hfLimit)
		{
			this.decayHFLimit = hfLimit;
			return this;
		}

		public float getReflectionGain()
		{
			return reflectionGain;
		}
		
		public DefaultType setReflectionGain(float reflectionGain)
		{
			this.reflectionGain = reflectionGain;
			return this;
		}
		
		public float getReflectionDelay()
		{
			return reflectionDelay;
		}
		
		public DefaultType setReflectionDelay(float reflectionDelay)
		{
			this.reflectionDelay = reflectionDelay;
			return this;
		}
		
		public float getLateGain()
		{
			return lateGain;
		}
		
		public DefaultType setLateGain(float lateGain)
		{
			this.lateGain = lateGain;
			return this;
		}
		
		public float getLateDelay()
		{
			return lateDelay;
		}
		
		public DefaultType setLateDelay(float lateDelay)
		{
			this.lateDelay = lateDelay;
			return this;
		}
		
		public float getAirAbsorptionHFGain()
		{
			return airAbsorptionHFGain;
		}
		
		public DefaultType setAirAbsorptionHFGain(float airAbsorptionGainHF)
		{
			this.airAbsorptionHFGain = airAbsorptionGainHF;
			return this;
		}
		
		public float getRoomRolloffFactor()
		{
			return roomRolloffFactor;
		}
		
		public DefaultType setRoomRolloffFactor(float roomRolloffFactor)
		{
			this.roomRolloffFactor = roomRolloffFactor;
			return this;
		}
		
	}
	
	static SoundReverbType GENERIC = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.8913f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.0500f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PADDEDCELL = (new DefaultType())
		.setDensity(0.1715f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0010f)
		.setDecayTime(0.1700f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.2500f)
		.setReflectionDelay(0.0010f)
		.setLateGain(1.2691f)
		.setLateDelay(0.0020f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ROOM = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.5929f)
		.setDecayTime(0.4000f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.1503f)
		.setReflectionDelay(0.0020f)
		.setLateGain(1.0629f)
		.setLateDelay(0.0030f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType BATHROOM = (new DefaultType())
		.setDensity(0.1715f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.2512f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.5400f)
		.setReflectionGain(0.6531f)
		.setReflectionDelay(0.0070f)
		.setLateGain(3.2734f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType LIVINGROOM = (new DefaultType())
		.setDensity(0.9766f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0010f)
		.setDecayTime(0.5000f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.2051f)
		.setReflectionDelay(0.0030f)
		.setLateGain(0.2805f)
		.setLateDelay(0.0040f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType STONEROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(2.3100f)
		.setDecayHFRatio(0.6400f)
		.setReflectionGain(0.4411f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.1003f)
		.setLateDelay(0.0170f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType AUDITORIUM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.5781f)
		.setDecayTime(4.3200f)
		.setDecayHFRatio(0.5900f)
		.setReflectionGain(0.4032f)
		.setReflectionDelay(0.0200f)
		.setLateGain(0.7170f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CONCERTHALL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(3.9200f)
		.setDecayHFRatio(0.7000f)
		.setReflectionGain(0.2427f)
		.setReflectionDelay(0.0200f)
		.setLateGain(0.9977f)
		.setLateDelay(0.0290f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CAVE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(1.0000f)
		.setDecayTime(2.9100f)
		.setDecayHFRatio(1.3000f)
		.setReflectionGain(0.5000f)
		.setReflectionDelay(0.0150f)
		.setLateGain(0.7063f)
		.setLateDelay(0.0220f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType ARENA = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.4477f)
		.setDecayTime(7.2400f)
		.setDecayHFRatio(0.3300f)
		.setReflectionGain(0.2612f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.0186f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType HANGAR = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(10.0500f)
		.setDecayHFRatio(0.2300f)
		.setReflectionGain(0.5000f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.2560f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CARPETEDHALLWAY = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0100f)
		.setDecayTime(0.3000f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.1215f)
		.setReflectionDelay(0.0020f)
		.setLateGain(0.1531f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType HALLWAY = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.5900f)
		.setReflectionGain(0.2458f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.6615f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType STONECORRIDOR = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.7612f)
		.setDecayTime(2.7000f)
		.setDecayHFRatio(0.7900f)
		.setReflectionGain(0.2472f)
		.setReflectionDelay(0.0130f)
		.setLateGain(1.5758f)
		.setLateDelay(0.0200f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ALLEY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.3000f)
		.setGain(0.3162f)
		.setHFGain(0.7328f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.8600f)
		.setReflectionGain(0.2500f)
		.setReflectionDelay(0.0070f)
		.setLateGain(0.9954f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FOREST = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.3000f)
		.setGain(0.3162f)
		.setHFGain(0.0224f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.5400f)
		.setReflectionGain(0.0525f)
		.setReflectionDelay(0.1620f)
		.setLateGain(0.7682f)
		.setLateDelay(0.0880f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CITY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.5000f)
		.setGain(0.3162f)
		.setHFGain(0.3981f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.6700f)
		.setReflectionGain(0.0730f)
		.setReflectionDelay(0.0070f)
		.setLateGain(0.1427f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType MOUNTAINS = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.2700f)
		.setGain(0.3162f)
		.setHFGain(0.0562f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.2100f)
		.setReflectionGain(0.0407f)
		.setReflectionDelay(0.3000f)
		.setLateGain(0.1919f)
		.setLateDelay(0.1000f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType QUARRY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.0000f)
		.setReflectionDelay(0.0610f)
		.setLateGain(1.7783f)
		.setLateDelay(0.0250f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PLAIN = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.2100f)
		.setGain(0.3162f)
		.setHFGain(0.1000f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.5000f)
		.setReflectionGain(0.0585f)
		.setReflectionDelay(0.1790f)
		.setLateGain(0.1089f)
		.setLateDelay(0.1000f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PARKINGLOT = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(1.0000f)
		.setDecayTime(1.6500f)
		.setDecayHFRatio(1.5000f)
		.setReflectionGain(0.2082f)
		.setReflectionDelay(0.0080f)
		.setLateGain(0.2652f)
		.setLateDelay(0.0120f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType SEWERPIPE = (new DefaultType())
		.setDensity(0.3071f)
		.setDiffusion(0.8000f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(2.8100f)
		.setDecayHFRatio(0.1400f)
		.setReflectionGain(1.6387f)
		.setReflectionDelay(0.0140f)
		.setLateGain(3.2471f)
		.setLateDelay(0.0210f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType UNDERWATER = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0100f)
		.setDecayTime(1.4900f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.5963f)
		.setReflectionDelay(0.0070f)
		.setLateGain(7.0795f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DRUGGED = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.5000f)
		.setGain(0.3162f)
		.setHFGain(1.0000f)
		.setDecayTime(8.3900f)
		.setDecayHFRatio(1.3900f)
		.setReflectionGain(0.8760f)
		.setReflectionDelay(0.0020f)
		.setLateGain(3.1081f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DIZZY = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.6000f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(17.2300f)
		.setDecayHFRatio(0.5600f)
		.setReflectionGain(0.1392f)
		.setReflectionDelay(0.0200f)
		.setLateGain(0.4937f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType PSYCHOTIC = (new DefaultType())
		.setDensity(0.0625f)
		.setDiffusion(0.5000f)
		.setGain(0.3162f)
		.setHFGain(0.8404f)
		.setDecayTime(7.5600f)
		.setDecayHFRatio(0.9100f)
		.setReflectionGain(0.4864f)
		.setReflectionDelay(0.0200f)
		.setLateGain(2.4378f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType CASTLE_SMALLROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8900f)
		.setGain(0.3162f)
		.setHFGain(0.3981f)
		.setDecayTime(1.2200f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0220f)
		.setLateGain(1.9953f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_SHORTPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8900f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(2.3200f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0230f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_MEDIUMROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.9300f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(2.0400f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.6310f)
		.setReflectionDelay(0.0220f)
		.setLateGain(1.5849f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_LARGEROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(2.5300f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0340f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0160f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_LONGPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8900f)
		.setGain(0.3162f)
		.setHFGain(0.3981f)
		.setDecayTime(3.4200f)
		.setDecayHFRatio(0.8300f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0230f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_HALL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8100f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(3.1400f)
		.setDecayHFRatio(0.7900f)
		.setReflectionGain(0.1778f)
		.setReflectionDelay(0.0560f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0240f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_CUPBOARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8900f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(0.6700f)
		.setDecayHFRatio(0.8700f)
		.setReflectionGain(1.4125f)
		.setReflectionDelay(0.0100f)
		.setLateGain(3.5481f)
		.setLateDelay(0.0070f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CASTLE_COURTYARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.4200f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(2.1300f)
		.setDecayHFRatio(0.6100f)
		.setReflectionGain(0.2239f)
		.setReflectionDelay(0.1600f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0360f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType CASTLE_ALCOVE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8900f)
		.setGain(0.3162f)
		.setHFGain(0.5012f)
		.setDecayTime(1.6400f)
		.setDecayHFRatio(0.8700f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0340f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_SMALLROOM = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(1.7200f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(0.7079f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.7783f)
		.setLateDelay(0.0240f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_SHORTPASSAGE = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.6400f)
		.setGain(0.2512f)
		.setHFGain(0.7943f)
		.setDecayTime(2.5300f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0380f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_MEDIUMROOM = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.8200f)
		.setGain(0.2512f)
		.setHFGain(0.7943f)
		.setDecayTime(2.7600f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(0.2818f)
		.setReflectionDelay(0.0220f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0230f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_LARGEROOM = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.7500f)
		.setGain(0.2512f)
		.setHFGain(0.7079f)
		.setDecayTime(4.2400f)
		.setDecayHFRatio(0.5100f)
		.setReflectionGain(0.1778f)
		.setReflectionDelay(0.0390f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0230f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_LONGPASSAGE = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.6400f)
		.setGain(0.2512f)
		.setHFGain(0.7943f)
		.setDecayTime(4.0600f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0370f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_HALL = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.7500f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(7.4300f)
		.setDecayHFRatio(0.5100f)
		.setReflectionGain(0.0631f)
		.setReflectionDelay(0.0730f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0270f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_CUPBOARD = (new DefaultType())
		.setDensity(0.3071f)
		.setDiffusion(0.6300f)
		.setGain(0.2512f)
		.setHFGain(0.7943f)
		.setDecayTime(0.4900f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(1.2589f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.9953f)
		.setLateDelay(0.0320f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_COURTYARD = (new DefaultType())
		.setDensity(0.3071f)
		.setDiffusion(0.5700f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(2.3200f)
		.setDecayHFRatio(0.2900f)
		.setReflectionGain(0.2239f)
		.setReflectionDelay(0.1400f)
		.setLateGain(0.3981f)
		.setLateDelay(0.0390f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType FACTORY_ALCOVE = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.5900f)
		.setGain(0.2512f)
		.setHFGain(0.7943f)
		.setDecayTime(3.1400f)
		.setDecayHFRatio(0.6500f)
		.setReflectionGain(1.4125f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.0000f)
		.setLateDelay(0.0380f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_SMALLROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8400f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(1.5100f)
		.setDecayHFRatio(1.5300f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_SHORTPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7500f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(1.7900f)
		.setDecayHFRatio(1.4600f)
		.setReflectionGain(0.5012f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0190f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_MEDIUMROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8700f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(2.2200f)
		.setDecayHFRatio(1.5300f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.0390f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0270f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_LARGEROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8100f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(3.1400f)
		.setDecayHFRatio(1.5300f)
		.setReflectionGain(0.2512f)
		.setReflectionDelay(0.0390f)
		.setLateGain(1.0000f)
		.setLateDelay(0.0270f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_LONGPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7700f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(3.0100f)
		.setDecayHFRatio(1.4600f)
		.setReflectionGain(0.7943f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0250f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_HALL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7600f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(5.4900f)
		.setDecayHFRatio(1.5300f)
		.setReflectionGain(0.1122f)
		.setReflectionDelay(0.0540f)
		.setLateGain(0.6310f)
		.setLateDelay(0.0520f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_CUPBOARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8300f)
		.setGain(0.3162f)
		.setHFGain(0.5012f)
		.setDecayTime(0.7600f)
		.setDecayHFRatio(1.5300f)
		.setReflectionGain(1.1220f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.9953f)
		.setLateDelay(0.0160f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_COURTYARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.5900f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(2.0400f)
		.setDecayHFRatio(1.2000f)
		.setReflectionGain(0.3162f)
		.setReflectionDelay(0.1730f)
		.setLateGain(0.3162f)
		.setLateDelay(0.0430f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType ICEPALACE_ALCOVE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8400f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(2.7600f)
		.setDecayHFRatio(1.4600f)
		.setReflectionGain(1.1220f)
		.setReflectionDelay(0.0100f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_SMALLROOM = (new DefaultType())
		.setDensity(0.2109f)
		.setDiffusion(0.7000f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(1.7200f)
		.setDecayHFRatio(0.8200f)
		.setReflectionGain(0.7943f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0130f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_SHORTPASSAGE = (new DefaultType())
		.setDensity(0.2109f)
		.setDiffusion(0.8700f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(3.5700f)
		.setDecayHFRatio(0.5000f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0160f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_MEDIUMROOM = (new DefaultType())
		.setDensity(0.2109f)
		.setDiffusion(0.7500f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(3.0100f)
		.setDecayHFRatio(0.5000f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.0340f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0350f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_LARGEROOM = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.8100f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(3.8900f)
		.setDecayHFRatio(0.3800f)
		.setReflectionGain(0.3162f)
		.setReflectionDelay(0.0560f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0350f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_LONGPASSAGE = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(4.6200f)
		.setDecayHFRatio(0.6200f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0310f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_HALL = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.8700f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(7.1100f)
		.setDecayHFRatio(0.3800f)
		.setReflectionGain(0.1778f)
		.setReflectionDelay(0.1000f)
		.setLateGain(0.6310f)
		.setLateDelay(0.0470f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_CUPBOARD = (new DefaultType())
		.setDensity(0.1715f)
		.setDiffusion(0.5600f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(0.7900f)
		.setDecayHFRatio(0.8100f)
		.setReflectionGain(1.4125f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.7783f)
		.setLateDelay(0.0180f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPACESTATION_ALCOVE = (new DefaultType())
		.setDensity(0.2109f)
		.setDiffusion(0.7800f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(1.1600f)
		.setDecayHFRatio(0.8100f)
		.setReflectionGain(1.4125f)
		.setReflectionDelay(0.0070f)
		.setLateGain(1.0000f)
		.setLateDelay(0.0180f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_SMALLROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1122f)
		.setDecayTime(0.7900f)
		.setDecayHFRatio(0.3200f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0320f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0290f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_SHORTPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1259f)
		.setDecayTime(1.7500f)
		.setDecayHFRatio(0.5000f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0120f)
		.setLateGain(0.6310f)
		.setLateDelay(0.0240f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_MEDIUMROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1000f)
		.setDecayTime(1.4700f)
		.setDecayHFRatio(0.4200f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0490f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0290f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_LARGEROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0891f)
		.setDecayTime(2.6500f)
		.setDecayHFRatio(0.3300f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0660f)
		.setLateGain(0.7943f)
		.setLateDelay(0.0490f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_LONGPASSAGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1000f)
		.setDecayTime(1.9900f)
		.setDecayHFRatio(0.4000f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0200f)
		.setLateGain(0.4467f)
		.setLateDelay(0.0360f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_HALL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0794f)
		.setDecayTime(3.4500f)
		.setDecayHFRatio(0.3000f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0880f)
		.setLateGain(0.7943f)
		.setLateDelay(0.0630f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_CUPBOARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1413f)
		.setDecayTime(0.5600f)
		.setDecayHFRatio(0.4600f)
		.setReflectionGain(1.1220f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0280f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_COURTYARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.6500f)
		.setGain(0.3162f)
		.setHFGain(0.0794f)
		.setDecayTime(1.7900f)
		.setDecayHFRatio(0.3500f)
		.setReflectionGain(0.5623f)
		.setReflectionDelay(0.1230f)
		.setLateGain(0.1000f)
		.setLateDelay(0.0320f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType WOODEN_ALCOVE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1259f)
		.setDecayTime(1.2200f)
		.setDecayHFRatio(0.6200f)
		.setReflectionGain(1.1220f)
		.setReflectionDelay(0.0120f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0240f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPORT_EMPTYSTADIUM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(6.2600f)
		.setDecayHFRatio(0.5100f)
		.setReflectionGain(0.0631f)
		.setReflectionDelay(0.1830f)
		.setLateGain(0.3981f)
		.setLateDelay(0.0380f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPORT_SQUASHCOURT = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7500f)
		.setGain(0.3162f)
		.setHFGain(0.3162f)
		.setDecayTime(2.2200f)
		.setDecayHFRatio(0.9100f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0070f)
		.setLateGain(0.7943f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPORT_SMALLSWIMMINGPOOL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7000f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(2.7600f)
		.setDecayHFRatio(1.2500f)
		.setReflectionGain(0.6310f)
		.setReflectionDelay(0.0200f)
		.setLateGain(0.7943f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType SPORT_LARGESWIMMINGPOOL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(5.4900f)
		.setDecayHFRatio(1.3100f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0390f)
		.setLateGain(0.5012f)
		.setLateDelay(0.0490f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType SPORT_GYMNASIUM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8100f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(3.1400f)
		.setDecayHFRatio(1.0600f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.0290f)
		.setLateGain(0.5623f)
		.setLateDelay(0.0450f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPORT_FULLSTADIUM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0708f)
		.setDecayTime(5.2500f)
		.setDecayHFRatio(0.1700f)
		.setReflectionGain(0.1000f)
		.setReflectionDelay(0.1880f)
		.setLateGain(0.2818f)
		.setLateDelay(0.0380f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SPORT_STADIUMTANNOY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7800f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(2.5300f)
		.setDecayHFRatio(0.8800f)
		.setReflectionGain(0.2818f)
		.setReflectionDelay(0.2300f)
		.setLateGain(0.5012f)
		.setLateDelay(0.0630f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PREFAB_WORKSHOP = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1413f)
		.setDecayTime(0.7600f)
		.setDecayHFRatio(1.0000f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0120f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType PREFAB_SCHOOLROOM = (new DefaultType())
		.setDensity(0.4022f)
		.setDiffusion(0.6900f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(0.9800f)
		.setDecayHFRatio(0.4500f)
		.setReflectionGain(1.4125f)
		.setReflectionDelay(0.0170f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0150f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PREFAB_PRACTISEROOM = (new DefaultType())
		.setDensity(0.4022f)
		.setDiffusion(0.8700f)
		.setGain(0.3162f)
		.setHFGain(0.3981f)
		.setDecayTime(1.1200f)
		.setDecayHFRatio(0.5600f)
		.setReflectionGain(1.2589f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0110f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PREFAB_OUTHOUSE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.1122f)
		.setDecayTime(1.3800f)
		.setDecayHFRatio(0.3800f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0240f)
		.setLateGain(0.6310f)
		.setLateDelay(0.0440f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType PREFAB_CARAVAN = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0891f)
		.setDecayTime(0.4300f)
		.setDecayHFRatio(1.5000f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0120f)
		.setLateGain(1.9953f)
		.setLateDelay(0.0120f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DOME_TOMB = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7900f)
		.setGain(0.3162f)
		.setHFGain(0.3548f)
		.setDecayTime(4.1800f)
		.setDecayHFRatio(0.2100f)
		.setReflectionGain(0.3868f)
		.setReflectionDelay(0.0300f)
		.setLateGain(1.6788f)
		.setLateDelay(0.0220f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType PIPE_SMALL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.3548f)
		.setDecayTime(5.0400f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.5012f)
		.setReflectionDelay(0.0320f)
		.setLateGain(2.5119f)
		.setLateDelay(0.0150f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DOME_SAINTPAULS = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8700f)
		.setGain(0.3162f)
		.setHFGain(0.3548f)
		.setDecayTime(10.4800f)
		.setDecayHFRatio(0.1900f)
		.setReflectionGain(0.1778f)
		.setReflectionDelay(0.0900f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0420f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PIPE_LONGTHIN = (new DefaultType())
		.setDensity(0.2560f)
		.setDiffusion(0.9100f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(9.2100f)
		.setDecayHFRatio(0.1800f)
		.setReflectionGain(0.7079f)
		.setReflectionDelay(0.0100f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0220f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType PIPE_LARGE = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.3548f)
		.setDecayTime(8.4500f)
		.setDecayHFRatio(0.1000f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.0460f)
		.setLateGain(1.5849f)
		.setLateDelay(0.0320f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType PIPE_RESONANT = (new DefaultType())
		.setDensity(0.1373f)
		.setDiffusion(0.9100f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(6.8100f)
		.setDecayHFRatio(0.1800f)
		.setReflectionGain(0.7079f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.0000f)
		.setLateDelay(0.0220f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType OUTDOORS_BACKYARD = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.4500f)
		.setGain(0.3162f)
		.setHFGain(0.2512f)
		.setDecayTime(1.1200f)
		.setDecayHFRatio(0.3400f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0690f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0230f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType OUTDOORS_ROLLINGPLAINS = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.0000f)
		.setGain(0.3162f)
		.setHFGain(0.0112f)
		.setDecayTime(2.1300f)
		.setDecayHFRatio(0.2100f)
		.setReflectionGain(0.1778f)
		.setReflectionDelay(0.3000f)
		.setLateGain(0.4467f)
		.setLateDelay(0.0190f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType OUTDOORS_DEEPCANYON = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7400f)
		.setGain(0.3162f)
		.setHFGain(0.1778f)
		.setDecayTime(3.8900f)
		.setDecayHFRatio(0.2100f)
		.setReflectionGain(0.3162f)
		.setReflectionDelay(0.2230f)
		.setLateGain(0.3548f)
		.setLateDelay(0.0190f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType OUTDOORS_CREEK = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.3500f)
		.setGain(0.3162f)
		.setHFGain(0.1778f)
		.setDecayTime(2.1300f)
		.setDecayHFRatio(0.2100f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.1150f)
		.setLateGain(0.1995f)
		.setLateDelay(0.0310f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType OUTDOORS_VALLEY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.2800f)
		.setGain(0.3162f)
		.setHFGain(0.0282f)
		.setDecayTime(2.8800f)
		.setDecayHFRatio(0.2600f)
		.setReflectionGain(0.1413f)
		.setReflectionDelay(0.2630f)
		.setLateGain(0.3981f)
		.setLateDelay(0.1000f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType MOOD_HEAVEN = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.9400f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(5.0400f)
		.setDecayHFRatio(1.1200f)
		.setReflectionGain(0.2427f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0290f)
		.setAirAbsorptionHFGain(0.9977f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType MOOD_HELL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.5700f)
		.setGain(0.3162f)
		.setHFGain(0.3548f)
		.setDecayTime(3.5700f)
		.setDecayHFRatio(0.4900f)
		.setReflectionGain(0.0000f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType MOOD_MEMORY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8500f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(4.0600f)
		.setDecayHFRatio(0.8200f)
		.setReflectionGain(0.0398f)
		.setReflectionDelay(0.0000f)
		.setLateGain(1.1220f)
		.setLateDelay(0.0000f)
		.setAirAbsorptionHFGain(0.9886f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DRIVING_COMMENTATOR = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.0000f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(2.4200f)
		.setDecayHFRatio(0.8800f)
		.setReflectionGain(0.1995f)
		.setReflectionDelay(0.0930f)
		.setLateGain(0.2512f)
		.setLateDelay(0.0170f)
		.setAirAbsorptionHFGain(0.9886f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DRIVING_PITGARAGE = (new DefaultType())
		.setDensity(0.4287f)
		.setDiffusion(0.5900f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(1.7200f)
		.setDecayHFRatio(0.9300f)
		.setReflectionGain(0.5623f)
		.setReflectionDelay(0.0000f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0160f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DRIVING_INCAR_RACER = (new DefaultType())
		.setDensity(0.0832f)
		.setDiffusion(0.8000f)
		.setGain(0.3162f)
		.setHFGain(1.0000f)
		.setDecayTime(0.1700f)
		.setDecayHFRatio(2.0000f)
		.setReflectionGain(1.7783f)
		.setReflectionDelay(0.0070f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0150f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DRIVING_INCAR_SPORTS = (new DefaultType())
		.setDensity(0.0832f)
		.setDiffusion(0.8000f)
		.setGain(0.3162f)
		.setHFGain(0.6310f)
		.setDecayTime(0.1700f)
		.setDecayHFRatio(0.7500f)
		.setReflectionGain(1.0000f)
		.setReflectionDelay(0.0100f)
		.setLateGain(0.5623f)
		.setLateDelay(0.0000f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DRIVING_INCAR_LUXURY = (new DefaultType())
		.setDensity(0.2560f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.1000f)
		.setDecayTime(0.1300f)
		.setDecayHFRatio(0.4100f)
		.setReflectionGain(0.7943f)
		.setReflectionDelay(0.0100f)
		.setLateGain(1.5849f)
		.setLateDelay(0.0100f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DRIVING_FULLGRANDSTAND = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(3.0100f)
		.setDecayHFRatio(1.3700f)
		.setReflectionGain(0.3548f)
		.setReflectionDelay(0.0900f)
		.setLateGain(0.1778f)
		.setLateDelay(0.0490f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DRIVING_EMPTYGRANDSTAND = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(1.0000f)
		.setGain(0.3162f)
		.setHFGain(1.0000f)
		.setDecayTime(4.6200f)
		.setDecayHFRatio(1.7500f)
		.setReflectionGain(0.2082f)
		.setReflectionDelay(0.0900f)
		.setLateGain(0.2512f)
		.setLateDelay(0.0490f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType DRIVING_TUNNEL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8100f)
		.setGain(0.3162f)
		.setHFGain(0.3981f)
		.setDecayTime(3.4200f)
		.setDecayHFRatio(0.9400f)
		.setReflectionGain(0.7079f)
		.setReflectionDelay(0.0510f)
		.setLateGain(0.7079f)
		.setLateDelay(0.0470f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CITY_STREETS = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7800f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(1.7900f)
		.setDecayHFRatio(1.1200f)
		.setReflectionGain(0.2818f)
		.setReflectionDelay(0.0460f)
		.setLateGain(0.1995f)
		.setLateDelay(0.0280f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CITY_SUBWAY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7400f)
		.setGain(0.3162f)
		.setHFGain(0.7079f)
		.setDecayTime(3.0100f)
		.setDecayHFRatio(1.2300f)
		.setReflectionGain(0.7079f)
		.setReflectionDelay(0.0460f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0280f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CITY_MUSEUM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.1778f)
		.setDecayTime(3.2800f)
		.setDecayHFRatio(1.4000f)
		.setReflectionGain(0.2512f)
		.setReflectionDelay(0.0390f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0340f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType CITY_LIBRARY = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.2818f)
		.setDecayTime(2.7600f)
		.setDecayHFRatio(0.8900f)
		.setReflectionGain(0.3548f)
		.setReflectionDelay(0.0290f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0200f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

	static SoundReverbType CITY_UNDERPASS = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8200f)
		.setGain(0.3162f)
		.setHFGain(0.4467f)
		.setDecayTime(3.5700f)
		.setDecayHFRatio(1.1200f)
		.setReflectionGain(0.3981f)
		.setReflectionDelay(0.0590f)
		.setLateGain(0.8913f)
		.setLateDelay(0.0370f)
		.setAirAbsorptionHFGain(0.9920f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CITY_ABANDONED = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.6900f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(3.2800f)
		.setDecayHFRatio(1.1700f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0440f)
		.setLateGain(0.2818f)
		.setLateDelay(0.0240f)
		.setAirAbsorptionHFGain(0.9966f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType DUSTYROOM = (new DefaultType())
		.setDensity(0.3645f)
		.setDiffusion(0.5600f)
		.setGain(0.3162f)
		.setHFGain(0.7943f)
		.setDecayTime(1.7900f)
		.setDecayHFRatio(0.3800f)
		.setReflectionGain(0.5012f)
		.setReflectionDelay(0.0020f)
		.setLateGain(1.2589f)
		.setLateDelay(0.0060f)
		.setAirAbsorptionHFGain(0.9886f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType CHAPEL = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.8400f)
		.setGain(0.3162f)
		.setHFGain(0.5623f)
		.setDecayTime(4.6200f)
		.setDecayHFRatio(0.6400f)
		.setReflectionGain(0.4467f)
		.setReflectionDelay(0.0320f)
		.setLateGain(0.7943f)
		.setLateDelay(0.0490f)
		.setAirAbsorptionHFGain(0.9943f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(true)
	;

	static SoundReverbType SMALLWATERROOM = (new DefaultType())
		.setDensity(1.0000f)
		.setDiffusion(0.7000f)
		.setGain(0.3162f)
		.setHFGain(0.4477f)
		.setDecayTime(1.5100f)
		.setDecayHFRatio(1.2500f)
		.setReflectionGain(0.8913f)
		.setReflectionDelay(0.0200f)
		.setLateGain(1.4125f)
		.setLateDelay(0.0300f)
		.setAirAbsorptionHFGain(0.9920f)
		.setRoomRolloffFactor(0.0000f)
		.setDecayHFLimit(false)
	;

}
