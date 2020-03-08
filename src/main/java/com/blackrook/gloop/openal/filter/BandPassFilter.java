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
 * Band-pass filter object for band-pass filtering.
 * @author Matthew Tropiano
 */
public class BandPassFilter extends OALFilter
{
	/** Band-pass gain. */
	protected float gain;
	/** Low-frequency band-pass gain. */
	protected float gainLF;
	/** High-frequency band-pass gain. */
	protected float gainHF;
	
	public BandPassFilter(OALSystem system)
	{
		super(system, EXTEfx.AL_FILTER_BANDPASS);
		setGain(EXTEfx.AL_BANDPASS_DEFAULT_GAIN);
		setLFGain(EXTEfx.AL_BANDPASS_DEFAULT_GAINLF);
		setHFGain(EXTEfx.AL_BANDPASS_DEFAULT_GAINHF);
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
	 * @param gain the gain value (0.0 to 1.0).
	 */
	public void setGain(float gain)
	{
		this.gain = gain;
		EXTEfx.alFilterf(getALId(), EXTEfx.AL_BANDPASS_GAIN, MathUtils.clampValue(gain, (float)EXTEfx.AL_BANDPASS_MIN_GAIN, EXTEfx.AL_BANDPASS_MAX_GAIN));
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
	 * @param gain the gain value (0.0 to 1.0).
	 */
	public void setLFGain(float gain)
	{
		this.gainLF = gain;
		EXTEfx.alFilterf(getALId(), EXTEfx.AL_BANDPASS_GAINLF, MathUtils.clampValue(gain, (float)EXTEfx.AL_BANDPASS_MIN_GAINLF, EXTEfx.AL_BANDPASS_MAX_GAINLF));
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
	 * @param gain the gain value (0.0 to 1.0).
	 */
	public void setHFGain(float gain)
	{
		this.gainHF = gain;
		EXTEfx.alFilterf(getALId(), EXTEfx.AL_BANDPASS_GAINHF, MathUtils.clampValue(gain, (float)EXTEfx.AL_BANDPASS_MIN_GAINHF, EXTEfx.AL_BANDPASS_MAX_GAINHF));
		errorCheck();
	}
	

}
