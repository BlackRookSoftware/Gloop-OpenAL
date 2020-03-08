/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.filter;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALFilter;
import com.blackrook.gloop.openal.OALSystem;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Low-pass filter object for low-pass filtering.
 * @author Matthew Tropiano
 */
public class LowPassFilter extends OALFilter
{
	/** Low-pass gain. */
	protected float gain;
	/** High-frequency low-pass gain. */
	protected float gainHF;
	
	public LowPassFilter(OALSystem system)
	{
		super(system, EXTEfx.AL_FILTER_LOWPASS);
		setGain(EXTEfx.AL_LOWPASS_DEFAULT_GAIN);
		setHFGain(EXTEfx.AL_LOWPASS_DEFAULT_GAINHF);
	}
	
	/**
	 * @return this filter's gain.
	 */
	public float getGain()
	{
		return gain;
	}

	/**
	 * Sets this filter's gain.
	 * @param gain	the gain value (0.0 to 1.0).
	 */
	public void setGain(float gain)
	{
		this.gain = gain;
		EXTEfx.alFilterf(getALId(), EXTEfx.AL_LOWPASS_GAIN, MathUtils.clampValue(gain, (float)EXTEfx.AL_LOWPASS_MIN_GAIN, EXTEfx.AL_LOWPASS_MAX_GAIN));
		errorCheck();
	}
	
	/**
	 * @return this filter's high-frequency gain.
	 */
	public float getHFGain()
	{
		return gainHF;
	}

	/**
	 * Sets this filter's high-frequency gain.
	 * @param gain	the gain value (0.0 to 1.0).
	 */
	public void setHFGain(float gain)
	{
		this.gainHF = gain;
		EXTEfx.alFilterf(getALId(), EXTEfx.AL_LOWPASS_GAINHF, MathUtils.clampValue(gain, (float)EXTEfx.AL_LOWPASS_MIN_GAINHF, EXTEfx.AL_LOWPASS_MAX_GAINHF));
		errorCheck();
	}
	
}
