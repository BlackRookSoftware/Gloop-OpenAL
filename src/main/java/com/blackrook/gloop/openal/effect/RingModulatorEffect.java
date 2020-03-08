/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.effect;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALEffect;
import com.blackrook.gloop.openal.OALSystem;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Ring modulator effect for sources.
 * @author Matthew Tropiano
 */
public class RingModulatorEffect extends OALEffect 
{
	/** WaveForm type enumeration. */
	public static enum WaveForm
	{
		SINUSOID(EXTEfx.AL_RING_MODULATOR_SINUSOID),
		SAWTOOTH(EXTEfx.AL_RING_MODULATOR_SAWTOOTH),
		SQUARE(EXTEfx.AL_RING_MODULATOR_SQUARE);
		
		final int alVal;
		private WaveForm(int alVal) {this.alVal = alVal;}
	}
	
	/** Ring modulator frequency. */
	protected float frequency;
	/** Ring modulator high-pass cutoff in Hertz. */
	protected float highPassCutoff;
	/** Ring modulator waveform. */
	protected WaveForm waveForm;

	public RingModulatorEffect(OALSystem system)
	{
		super(system, EXTEfx.AL_EFFECT_RING_MODULATOR);
		setWaveform(WaveForm.SINUSOID);
		setFrequency(EXTEfx.AL_RING_MODULATOR_DEFAULT_FREQUENCY);
		setHighPassCutoff(EXTEfx.AL_RING_MODULATOR_DEFAULT_HIGHPASS_CUTOFF);
	}
	
	/** 
	 * @return the current ring modulator waveform. 
	 */
	public final WaveForm getWaveform()
	{
		return waveForm;
	}

	/** 
	 * Set ring modulator waveform. 
	 * @param waveform the new value.
	 */
	public final void setWaveform(WaveForm waveform)
	{
		this.waveForm = waveform;
		EXTEfx.alEffecti(getALId(), EXTEfx.AL_RING_MODULATOR_WAVEFORM, waveform.alVal);
		errorCheck();
	}

	/** 
	 * @return the current ring modulator shifter frequency. 
	 */
	public final float getFrequency()
	{
		return frequency;
	}

	/** 
	 * Set frequency shifter frequency (0.0 to 8000.0). 
	 * @param frequency the new value.
	 */
	public final void setFrequency(float frequency)
	{
		this.frequency = frequency;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_RING_MODULATOR_FREQUENCY, MathUtils.clampValue(frequency, EXTEfx.AL_RING_MODULATOR_MIN_FREQUENCY, EXTEfx.AL_RING_MODULATOR_MAX_FREQUENCY));
		errorCheck();
	}

	/** 
	 * @return the current ring modulator high-pass cutoff in Hertz. 
	 */
	public final float getHighPassCutoff()
	{
		return highPassCutoff;
	}

	/** 
	 * Set ring modulator high-pass cutoff in Hertz (0.0 to 24000.0). 
	 * @param highPassCutoff the new value.
	 */
	public final void setHighPassCutoff(float highPassCutoff)
	{
		this.highPassCutoff = highPassCutoff;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_RING_MODULATOR_HIGHPASS_CUTOFF, MathUtils.clampValue(highPassCutoff, EXTEfx.AL_RING_MODULATOR_MIN_HIGHPASS_CUTOFF, EXTEfx.AL_RING_MODULATOR_MAX_HIGHPASS_CUTOFF));
		errorCheck();
	}

}
