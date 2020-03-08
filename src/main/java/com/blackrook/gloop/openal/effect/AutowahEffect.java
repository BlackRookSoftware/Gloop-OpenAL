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
 * Autowah effect for sound sources.
 * @author Matthew Tropiano
 */
public class AutowahEffect extends OALEffect
{
	/** Autowah attack time in seconds. */
	protected float attackTime;
	/** Autowah release time in seconds. */
	protected float releaseTime;
	/** Autowah resonance factor. */
	protected float resonance;
	/** Autowah peak gain. */
	protected float peakGain;

	public AutowahEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_AUTOWAH);
		setAttackTime(EXTEfx.AL_AUTOWAH_DEFAULT_ATTACK_TIME);
		setReleaseTime(EXTEfx.AL_AUTOWAH_DEFAULT_RELEASE_TIME);
		setResonance(EXTEfx.AL_AUTOWAH_DEFAULT_RESONANCE);
		setPeakGain(EXTEfx.AL_AUTOWAH_DEFAULT_PEAK_GAIN);
	}

	/** 
	 * @return autowah attack time in seconds. 
	 */
	public final float getAttackTime()
	{
		return attackTime;
	}

	/** 
	 * Set autowah attack time in seconds (0.0001 to 1.0). 
	 * @param attackTime the new value.
	 */
	public final void setAttackTime(float attackTime)
	{
		this.attackTime = attackTime;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_AUTOWAH_ATTACK_TIME, MathUtils.clampValue(attackTime, EXTEfx.AL_AUTOWAH_MIN_ATTACK_TIME, EXTEfx.AL_AUTOWAH_MAX_ATTACK_TIME));
		errorCheck();
	}

	/** 
	 * @return Autowah peak gain. 
	 */
	public final float getPeakGain()
	{
		return peakGain;
	}

	/** 
	 * Set autowah peak gain (0.00003 to 31621.0).
	 * @param peakGain the new value.
	 */
	public final void setPeakGain(float peakGain)
	{
		this.peakGain = peakGain;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_AUTOWAH_PEAK_GAIN, MathUtils.clampValue(peakGain, EXTEfx.AL_AUTOWAH_MIN_PEAK_GAIN, EXTEfx.AL_AUTOWAH_MAX_PEAK_GAIN));
		errorCheck();
	}

	/** 
	 * @return autowah release time in seconds. 
	 */
	public final float getReleaseTime()
	{
		return releaseTime;
	}

	/** 
	 * Set autowah release time in seconds (0.0001 to 1.0). 
	 * @param releaseTime the new value.
	 */
	public final void setReleaseTime(float releaseTime)
	{
		this.releaseTime = releaseTime;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_AUTOWAH_RELEASE_TIME, MathUtils.clampValue(releaseTime, EXTEfx.AL_AUTOWAH_MIN_RELEASE_TIME, EXTEfx.AL_AUTOWAH_MAX_RELEASE_TIME));
		errorCheck();
	}

	/** 
	 * @return autowah resonance factor. 
	 */
	public final float getResonance()
	{
		return resonance;
	}

	/** 
	 * Set autowah resonance factor (2.0 to 1000.0). 
	 * @param resonance the new value.
	 */
	public final void setResonance(float resonance)
	{
		this.resonance = resonance;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_AUTOWAH_RESONANCE, MathUtils.clampValue(resonance, EXTEfx.AL_AUTOWAH_MIN_RESONANCE, EXTEfx.AL_AUTOWAH_MAX_RESONANCE));
		errorCheck();
	}
	
}
