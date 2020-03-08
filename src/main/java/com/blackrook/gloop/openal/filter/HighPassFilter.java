/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.filter;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALContext;
import com.blackrook.gloop.openal.OALFilter;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * High-pass filter object for high-pass filtering.
 * @author Matthew Tropiano
 */
public class HighPassFilter extends OALFilter
{
	/** High-pass gain. */
	protected float gain;
	/** Low-frequency high-pass gain. */
	protected float gainLF;
	
	public HighPassFilter(OALContext context)
	{
		super(context, EXTEfx.AL_FILTER_HIGHPASS);
		setGain(EXTEfx.AL_HIGHPASS_DEFAULT_GAIN);
		setLFGain(EXTEfx.AL_HIGHPASS_DEFAULT_GAINLF);
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
		EXTEfx.alFilterf(getName(), EXTEfx.AL_HIGHPASS_GAIN, MathUtils.clampValue(gain, (float)EXTEfx.AL_HIGHPASS_MIN_GAIN, EXTEfx.AL_HIGHPASS_MAX_GAIN));
		errorCheck();
	}
	
	/**
	 * @return this filter's low-frequency gain.
	 */
	public float getLFGain()
	{
		return gainLF;
	}

	/**
	 * Sets this filter's low-frequency gain.
	 * @param gain	the gain value (0.0 to 1.0).
	 */
	public void setLFGain(float gain)
	{
		this.gainLF = gain;
		EXTEfx.alFilterf(getName(), EXTEfx.AL_HIGHPASS_GAINLF, MathUtils.clampValue(gain, (float)EXTEfx.AL_HIGHPASS_MIN_GAINLF, EXTEfx.AL_HIGHPASS_MAX_GAINLF));
		errorCheck();
	}
	
}
