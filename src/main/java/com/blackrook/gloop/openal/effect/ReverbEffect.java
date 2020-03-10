/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.effect;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALContext;
import com.blackrook.gloop.openal.OALEffect;
import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Reverb effect for sound and stuff.
 * @author Matthew Tropiano
 */
public class ReverbEffect extends OALEffect
{
	/** Reverb density factor. */
	protected float density;
	/** Reverb diffusion factor. */
	protected float diffusion;
	/** Reverb gain. */
	protected float gain;
	/** Reverb high-frequency gain. */
	protected float gainHF;
	/** Reverb decay time in seconds. */
	protected float decayTime;
	/** Reverb high-frequency ratio. */
	protected float decayHFRatio;
	/** Reverb reflection gain. */
	protected float reflectionGain;
	/** Reverb reflection delay in seconds. */
	protected float reflectionDelay;
	/** Late reverb gain. */
	protected float lateGain;
	/** Late reverb delay. */
	protected float lateDelay;
	/** Reverb high-frequency air absorption gain. */
	protected float airAbsorptionGainHF;
	/** Reverb room rolloff factor. */
	protected float roomRolloffFactor;
	/** Reverb decay high-frequency limit? */
	protected boolean hfLimit;
	
	public ReverbEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_REVERB);
		setDensity(EXTEfx.AL_REVERB_DEFAULT_DENSITY);
		setDiffusion(EXTEfx.AL_REVERB_DEFAULT_DIFFUSION);
		setGain(EXTEfx.AL_REVERB_DEFAULT_GAIN);
		setHFGain(EXTEfx.AL_REVERB_DEFAULT_GAINHF);
		setDecayTime(EXTEfx.AL_REVERB_DEFAULT_DECAY_TIME);
		setDecayHFRatio(EXTEfx.AL_REVERB_DEFAULT_DECAY_HFRATIO);
		setReflectionGain(EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_GAIN);
		setReflectionDelay(EXTEfx.AL_REVERB_DEFAULT_REFLECTIONS_DELAY);
		setLateGain(EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_GAIN);
		setLateDelay(EXTEfx.AL_REVERB_DEFAULT_LATE_REVERB_DELAY);
		setAirAbsorptionGainHF(EXTEfx.AL_REVERB_DEFAULT_AIR_ABSORPTION_GAINHF);
		setRoomRolloffFactor(EXTEfx.AL_REVERB_DEFAULT_ROOM_ROLLOFF_FACTOR);
		setDecayHFLimit(true);
	}

	/** 
	 * @return the current reverb high-frequency air absorption gain. 
	 */
	public final float getAirAbsorptionGainHF()
	{
		return airAbsorptionGainHF;
	}

	/** 
	 * Set reverb high-frequency air absorption gain (0.892 to 1.0). 
	 * @param airAbsorptionGainHF the new value.
	 */
	public final void setAirAbsorptionGainHF(float airAbsorptionGainHF)
	{
		this.airAbsorptionGainHF = airAbsorptionGainHF;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_AIR_ABSORPTION_GAINHF, 
				MathUtils.clampValue(airAbsorptionGainHF, EXTEfx.AL_REVERB_MIN_AIR_ABSORPTION_GAINHF, EXTEfx.AL_REVERB_MAX_AIR_ABSORPTION_GAINHF)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current the current reverb high-frequency ratio. 
	 */
	public final float getDecayHFRatio()
	{
		return decayHFRatio;
	}

	/** 
	 * Sets the reverb high-frequency ratio (0.1 to 2.0). 
	 * @param decayHFRatio the new value.
	 */
	public final void setDecayHFRatio(float decayHFRatio)
	{
		this.decayHFRatio = decayHFRatio;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_DECAY_HFRATIO,
				MathUtils.clampValue(decayHFRatio, EXTEfx.AL_REVERB_MIN_DECAY_HFRATIO, EXTEfx.AL_REVERB_MAX_DECAY_HFRATIO)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb decay time in seconds. 
	 */
	public final float getDecayTime()
	{
		return decayTime;
	}

	/** 
	 * Set reverb decay time in seconds (0.1 to 20.0). 
	 * @param decayTime the new value.
	 */
	public final void setDecayTime(float decayTime)
	{
		this.decayTime = decayTime;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_DECAY_TIME, 
				MathUtils.clampValue(decayTime, EXTEfx.AL_REVERB_MIN_DECAY_TIME, EXTEfx.AL_REVERB_MAX_DECAY_TIME)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb density factor. 
	 */
	public final float getDensity()
	{
		return density;
	}

	/** 
	 * Set reverb density factor (0.0 to 1.0). 
	 * @param density the new value.
	 */
	public final void setDensity(float density)
	{
		this.density = density;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_DENSITY, 
				MathUtils.clampValue(density, EXTEfx.AL_REVERB_MIN_DENSITY, EXTEfx.AL_REVERB_MAX_DENSITY)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb diffusion factor. 
	 */
	public final float getDiffusion()
	{
		return diffusion;
	}

	/** 
	 * Set reverb diffusion factor (0.0 to 1.0). 
	 * @param diffusion the new value.
	 */
	public final void setDiffusion(float diffusion)
	{
		this.diffusion = diffusion;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_DIFFUSION, 
				MathUtils.clampValue(diffusion, EXTEfx.AL_REVERB_MIN_DIFFUSION, EXTEfx.AL_REVERB_MAX_DIFFUSION)
			);
			errorCheck();
		}
	}

	/**
	 * @return the current reverb gain. 
	 */
	public final float getGain()
	{
		return gain;
	}

	/**
	 * Set reverb gain (0.0 to 1.0). 
	 * @param gain the new value.
	 */
	public final void setGain(float gain)
	{
		this.gain = gain;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_GAIN, 
				MathUtils.clampValue(gain, EXTEfx.AL_REVERB_MIN_GAIN, EXTEfx.AL_REVERB_MAX_GAIN)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb high-frequency gain. 
	 */
	public final float getHFGain()
	{
		return gainHF;
	}

	/** 
	 * Set reverb high-frequency gain (0.0 to 1.0). 
	 * @param gainHF the new value.
	 */
	public final void setHFGain(float gainHF)
	{
		this.gainHF = gainHF;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_GAINHF, 
				MathUtils.clampValue(gainHF, EXTEfx.AL_REVERB_MIN_GAINHF, EXTEfx.AL_REVERB_MAX_GAINHF)
			);
			errorCheck();
		}
	}

	/** 
	 * @return true if the reverb decay high-frequency limit is set, false if not. 
	 */
	public final boolean isDecayHFLimit()
	{
		return hfLimit;
	}

	/** 
	 * Sets the reverb decay high-frequency limit. True = limit on, false = off. 
	 * @param limit the new value.
	 */
	public final void setDecayHFLimit(boolean limit)
	{
		hfLimit = limit;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(getName(), EXTEfx.AL_REVERB_DECAY_HFLIMIT, limit ? AL11.AL_TRUE : AL11.AL_FALSE);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb reflection delay in seconds. 
	 */
	public final float getReflectionDelay()
	{
		return reflectionDelay;
	}

	/** 
	 * Set reverb reflection delay in seconds (0.0 to 0.3). 
	 * @param reflectionDelay the new value.
	 */
	public final void setReflectionDelay(float reflectionDelay)
	{
		this.reflectionDelay = reflectionDelay;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_REFLECTIONS_DELAY,
				MathUtils.clampValue(reflectionDelay, EXTEfx.AL_REVERB_MIN_REFLECTIONS_DELAY, EXTEfx.AL_REVERB_MAX_REFLECTIONS_DELAY)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb reflection gain. 
	 */
	public final float getReflectionGain()
	{
		return reflectionGain;
	}

	/** 
	 * Set reverb reflection gain (0.0 to 3.16).
	 * @param reflectionGain the new value.
	 */
	public final void setReflectionGain(float reflectionGain)
	{
		this.reflectionGain = reflectionGain;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_REFLECTIONS_GAIN, 
				MathUtils.clampValue(reflectionGain, EXTEfx.AL_REVERB_MIN_REFLECTIONS_GAIN, EXTEfx.AL_REVERB_MAX_REFLECTIONS_GAIN)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current reverb room rolloff factor. 
	 */
	public final float getRoomRolloffFactor()
	{
		return roomRolloffFactor;
	}

	/** 
	 * Set reverb room rolloff factor (0.0 to 10.0). 
	 * @param roomRolloffFactor the new value.
	 */
	public final void setRoomRolloffFactor(float roomRolloffFactor)
	{
		this.roomRolloffFactor = roomRolloffFactor;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_ROOM_ROLLOFF_FACTOR, 
				MathUtils.clampValue(roomRolloffFactor, EXTEfx.AL_REVERB_MIN_ROOM_ROLLOFF_FACTOR, EXTEfx.AL_REVERB_MAX_ROOM_ROLLOFF_FACTOR)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current late reverb delay. 
	 */
	public final float getLateDelay()
	{
		return lateDelay;
	}

	/** 
	 * Set late reverb delay (0.0 to 0.1). 
	 * @param lateDelay the new value.
	 */
	public final void setLateDelay(float lateDelay)
	{
		this.lateDelay = lateDelay;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_LATE_REVERB_DELAY, 
				MathUtils.clampValue(lateDelay, EXTEfx.AL_REVERB_MIN_LATE_REVERB_DELAY, EXTEfx.AL_REVERB_MAX_LATE_REVERB_DELAY)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current late reverb gain. 
	 */
	public final float getLateGain()
	{
		return lateGain;
	}

	/** 
	 * Set late reverb gain (0.0 to 10.0).
	 * @param lateGain the new value.
	 */
	public final void setLateGain(float lateGain)
	{
		this.lateGain = lateGain;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_REVERB_LATE_REVERB_GAIN, 
				MathUtils.clampValue(lateGain, EXTEfx.AL_REVERB_MIN_LATE_REVERB_GAIN, EXTEfx.AL_REVERB_MAX_LATE_REVERB_GAIN)
			);
			errorCheck();
		}
	}

}
