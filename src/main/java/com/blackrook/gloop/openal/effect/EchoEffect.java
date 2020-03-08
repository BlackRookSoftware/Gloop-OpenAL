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
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Echo effect for sound sources.
 * @author Matthew Tropiano
 */
public class EchoEffect extends OALEffect
{
	/** Echo delay in seconds. */
	protected float delay;
	/** Echo LR delay in seconds. */
	protected float lrDelay;
	/** Echo damping. */
	protected float damping;
	/** Echo feedback. */
	protected float feedback;
	/** Echo spread. */
	protected float spread;
	
	public EchoEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_ECHO);
		setDelay(EXTEfx.AL_ECHO_DEFAULT_DELAY);
		setLRDelay(EXTEfx.AL_ECHO_DEFAULT_LRDELAY);
		setDamping(EXTEfx.AL_ECHO_DEFAULT_DAMPING);
		setFeedback(EXTEfx.AL_ECHO_DEFAULT_FEEDBACK);
		setSpread(EXTEfx.AL_ECHO_DEFAULT_SPREAD);
	}
	
	/**
	 * @return this effect's delay in seconds.
	 */
	public final float getDelay()
	{
		return delay;
	}

	/**
	 * Sets this effect's delay in seconds (0.0 to 0.207).
	 * @param delay the new value.
	 */
	public final void setDelay(float delay)
	{
		this.delay = delay;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_ECHO_DELAY, MathUtils.clampValue(delay, EXTEfx.AL_ECHO_MIN_DELAY, EXTEfx.AL_ECHO_MAX_DELAY));
		errorCheck();
	}

	/**
	 * @return this effect's LR delay in seconds.
	 */
	public final float getLRDelay()
	{
		return lrDelay;
	}

	/**
	 * Sets this effect's LR delay in seconds (0.0 to 0.404).
	 * @param lrDelay the new value.
	 */
	public final void setLRDelay(float lrDelay)
	{
		this.lrDelay = lrDelay;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_ECHO_LRDELAY, MathUtils.clampValue(lrDelay, EXTEfx.AL_ECHO_MIN_LRDELAY, EXTEfx.AL_ECHO_MAX_LRDELAY));
		errorCheck();
	}

	/**
	 * @return this effect's feedback scalar.
	 */
	public final float getFeedback()
	{
		return feedback;
	}

	/**
	 * Sets this effect's feedback scalar (0.0 to 1.0).
	 * @param feedback the new value.
	 */
	public final void setFeedback(float feedback)
	{
		this.feedback = feedback;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_ECHO_FEEDBACK, MathUtils.clampValue(feedback, EXTEfx.AL_ECHO_MIN_FEEDBACK, EXTEfx.AL_ECHO_MAX_FEEDBACK));
		errorCheck();
	}

	/**
	 * @return this effect's damping scalar.
	 */
	public final float getDamping()
	{
		return damping;
	}

	/**
	 * Sets this effect's damping scalar (0.0 to 0.99).
	 * @param damping the new value.
	 */
	public final void setDamping(float damping)
	{
		this.damping = damping;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_ECHO_DAMPING, MathUtils.clampValue(damping, EXTEfx.AL_ECHO_MIN_DAMPING, EXTEfx.AL_ECHO_MAX_DAMPING));
		errorCheck();
	}

	/**
	 * @return this effect's spread.
	 */
	public final float getSpread()
	{
		return spread;
	}

	/**
	 * Sets this effect's spread (-1.0 to 1.0).
	 * @param spread the new value.
	 */
	public final void setSpread(float spread)
	{
		this.spread = spread;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_ECHO_SPREAD, MathUtils.clampValue(spread, EXTEfx.AL_ECHO_MIN_SPREAD, EXTEfx.AL_ECHO_MAX_SPREAD));
		errorCheck();
	}
	
}
