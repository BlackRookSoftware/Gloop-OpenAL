/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.effect;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALContext;
import com.blackrook.gloop.openal.OALEffect;
import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Chorus effect for sound sources.
 * @author Matthew Tropiano
 */
public class ChorusEffect extends OALEffect
{
	/** WaveForm type enumeration. */
	public static enum WaveForm
	{
		SINUSOID(EXTEfx.AL_CHORUS_WAVEFORM_SINUSOID),
		TRIANGLE(EXTEfx.AL_CHORUS_WAVEFORM_TRIANGLE);
		
		final int alVal;
		private WaveForm(int alVal) {this.alVal = alVal;}
	}
	
	/** Chorus waveform type. */
	protected WaveForm waveForm;
	/** Chorus phase in degrees.  */
	protected int phase;
	/** Chorus rate in Hz. */
	protected float rate;
	/** Chorus depth. */
	protected float depth;
	/** Chorus feedback. */
	protected float feedback;
	/** Chorus delay in seconds. */
	protected float delay;
	
	public ChorusEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_CHORUS);
		setWaveForm(WaveForm.TRIANGLE);
		setPhase(EXTEfx.AL_CHORUS_DEFAULT_PHASE);
		setRate(EXTEfx.AL_CHORUS_DEFAULT_RATE);
		setDepth(EXTEfx.AL_CHORUS_DEFAULT_DEPTH);
		setFeedback(EXTEfx.AL_CHORUS_DEFAULT_FEEDBACK);
		setDelay(EXTEfx.AL_CHORUS_DEFAULT_DELAY);
	}

	/**
	 * @return this effect's delay in seconds.
	 */
	public final float getDelay()
	{
		return delay;
	}

	/**
	 * Sets this effect's delay in seconds (0.0 to 0.016).
	 * @param delay the new value.
	 */
	public final void setDelay(float delay)
	{
		this.delay = delay;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_CHORUS_DELAY, 
				MathUtils.clampValue(delay, EXTEfx.AL_CHORUS_MIN_DELAY, EXTEfx.AL_CHORUS_MAX_DELAY)
			);
			errorCheck();
		}
	}

	/**
	 * @return this effect's depth.
	 */
	public final float getDepth()
	{
		return depth;
	}

	/**
	 * Sets this effect's depth (0.0 to 1.0).
	 * @param depth the new value.
	 */
	public final void setDepth(float depth)
	{
		this.depth = depth;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_CHORUS_DEPTH, 
				MathUtils.clampValue(depth, EXTEfx.AL_CHORUS_MIN_DEPTH, EXTEfx.AL_CHORUS_MAX_DEPTH)
			);
			errorCheck();
		}
	}

	/**
	 * @return this effect's feedback.
	 */
	public final float getFeedback()
	{
		return feedback;
	}

	/**
	 * Sets this effect's feedback (-1.0 to 1.0).
	 * @param feedback the new value.
	 */
	public final void setFeedback(float feedback)
	{
		this.feedback = feedback;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_CHORUS_FEEDBACK, 
				MathUtils.clampValue(feedback, EXTEfx.AL_CHORUS_MIN_FEEDBACK, EXTEfx.AL_CHORUS_MAX_FEEDBACK)
			);
			errorCheck();
		}
	}

	/**
	 * @return this effect's phase in degrees.
	 */
	public final int getPhase()
	{
		return phase;
	}

	/**
	 * Sets this effect's phase in degrees (-180 to 180).
	 * @param phase the new value.
	 */
	public final void setPhase(int phase)
	{
		this.phase = phase;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(
				getName(), 
				EXTEfx.AL_CHORUS_PHASE,
				MathUtils.clampValue(phase, EXTEfx.AL_CHORUS_MIN_PHASE, EXTEfx.AL_CHORUS_MAX_PHASE)
			);
			errorCheck();
		}
	}

	/**
	 * @return this effect's rate in Hz.
	 */
	public final float getRate()
	{
		return rate;
	}

	/**
	 * Sets this effect's rate in Hz (0.0 to 10.0).
	 * @param rate the new value.
	 */
	public final void setRate(float rate)
	{
		this.rate = rate;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffectf(
				getName(), 
				EXTEfx.AL_CHORUS_RATE, 
				MathUtils.clampValue(rate, EXTEfx.AL_CHORUS_MIN_RATE, EXTEfx.AL_CHORUS_MAX_RATE)
			);
			errorCheck();
		}
	}

	/**
	 * @return this effect's waveform type.
	 */
	public final WaveForm getWaveForm()
	{
		return waveForm;
	}

	/**
	 * Sets this effect's waveform type.
	 * @param waveForm the waveform enumerant.
	 */
	public final void setWaveForm(WaveForm waveForm)
	{
		this.waveForm = waveForm;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(getName(), EXTEfx.AL_CHORUS_WAVEFORM, waveForm.alVal);
			errorCheck();
		}
	}
}
