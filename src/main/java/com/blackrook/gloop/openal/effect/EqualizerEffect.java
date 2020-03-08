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
 * Equalizer effect for sound sources.
 * @author Matthew Tropiano
 */
public class EqualizerEffect extends OALEffect
{
	/** Equalizer low gain. */
	protected float lowGain;
	/** Equalizer low cutoff in Hertz. */
	protected float lowCutoff;
	/** Equalizer first mid gain. */
	protected float mid1Gain;
	/** Equalizer first mid center in Hertz. */
	protected float mid1Center;
	/** Equalizer first mid width. */
	protected float mid1Width;
	/** Equalizer second mid gain. */
	protected float mid2Gain;
	/** Equalizer second mid center in Hertz. */
	protected float mid2Center;
	/** Equalizer second mid width. */
	protected float mid2Width;
	/** Equalizer high gain. */
	protected float highGain;
	/** Equalizer high cutoff in Hertz. */
	protected float highCutoff;
	
	public EqualizerEffect(OALSystem system)
	{
		super(system, EXTEfx.AL_EFFECT_EQUALIZER);
		setLowGain(EXTEfx.AL_EQUALIZER_DEFAULT_LOW_GAIN);
		setLowCutoff(EXTEfx.AL_EQUALIZER_DEFAULT_LOW_CUTOFF);
		
		setMid1Gain(EXTEfx.AL_EQUALIZER_DEFAULT_MID1_GAIN);
		setMid1Center(EXTEfx.AL_EQUALIZER_DEFAULT_MID1_CENTER);
		setMid1Width(EXTEfx.AL_EQUALIZER_DEFAULT_MID1_WIDTH);
		
		setMid2Gain(EXTEfx.AL_EQUALIZER_DEFAULT_MID2_GAIN);
		setMid2Center(EXTEfx.AL_EQUALIZER_DEFAULT_MID2_CENTER);
		setMid2Width(EXTEfx.AL_EQUALIZER_DEFAULT_MID2_WIDTH);
		
		setHighGain(EXTEfx.AL_EQUALIZER_DEFAULT_HIGH_GAIN);
		setHighCutoff(EXTEfx.AL_EQUALIZER_DEFAULT_HIGH_CUTOFF);
	}

	/** 
	 * @return the current equalizer high cutoff in Hertz. 
	 */
	public final float getHighCutoff() 
	{
		return highCutoff;
	}

	/** 
	 * Set equalizer high cutoff in Hertz (4000.0 to 16000.0). 
	 * @param highCutoff the new value.
	 */
	public final void setHighCutoff(float highCutoff) 
	{
		this.highCutoff = highCutoff;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_HIGH_CUTOFF, MathUtils.clampValue(highCutoff, EXTEfx.AL_EQUALIZER_MIN_HIGH_CUTOFF, EXTEfx.AL_EQUALIZER_MAX_HIGH_CUTOFF));
		errorCheck();
	}

	/** 
	 * @return the current equalizer high gain. 
	 */
	public final float getHighGain() 
	{
		return highGain;
	}

	/** 
	 * Set equalizer high gain (0.126 to 7.943). 
	 * @param highGain the new value.
	 */
	public final void setHighGain(float highGain)
	{
		this.highGain = highGain;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_HIGH_GAIN, MathUtils.clampValue(highGain, EXTEfx.AL_EQUALIZER_MIN_HIGH_GAIN, EXTEfx.AL_EQUALIZER_MAX_HIGH_GAIN));
		errorCheck();
	}

	/** 
	 * @return the current equalizer low cutoff in Hertz. 
	 */
	public final float getLowCutoff() 
	{
		return lowCutoff;
	}

	/** 
	 * Set equalizer low cutoff in Hertz (50.0 to 800.0). 
	 * @param lowCutoff the new value.
	 */
	public final void setLowCutoff(float lowCutoff) 
	{
		this.lowCutoff = lowCutoff;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_LOW_CUTOFF, MathUtils.clampValue(lowCutoff, EXTEfx.AL_EQUALIZER_MIN_LOW_CUTOFF, EXTEfx.AL_EQUALIZER_MAX_LOW_CUTOFF));
		errorCheck();
	}

	/** 
	 * @return the current equalizer low gain. 
	 */
	public final float getLowGain() 
	{
		return lowGain;
	}

	/** 
	 * Set equalizer low gain (0.126 to 7.943). 
	 * @param lowGain the new value.
	 */
	public final void setLowGain(float lowGain) 
	{
		this.lowGain = lowGain;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_LOW_GAIN, MathUtils.clampValue(lowGain, EXTEfx.AL_EQUALIZER_MIN_LOW_GAIN, EXTEfx.AL_EQUALIZER_MAX_LOW_GAIN));
		errorCheck();
	}

	/** 
	 * @return the current equalizer first mid center in Hertz. 
	 */
	public final float getMid1Center() 
	{
		return mid1Center;
	}

	/** 
	 * Set equalizer first mid center in Hertz (200.0 to 3000.0). 
	 * @param mid1Center the new value.
	 */
	public final void setMid1Center(float mid1Center) 
	{
		this.mid1Center = mid1Center;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID1_CENTER, MathUtils.clampValue(mid1Center, EXTEfx.AL_EQUALIZER_MIN_MID1_CENTER, EXTEfx.AL_EQUALIZER_MAX_MID1_CENTER));
		errorCheck();
	}

	/** 
	 * @return the current equalizer first mid gain. 
	 */
	public final float getMid1Gain() 
	{
		return mid1Gain;
	}

	/** 
	 * Set equalizer first mid gain (0.126 to 7.943). 
	 * @param mid1Gain the new value.
	 */
	public final void setMid1Gain(float mid1Gain) 
	{
		this.mid1Gain = mid1Gain;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID1_GAIN, MathUtils.clampValue(mid1Gain, EXTEfx.AL_EQUALIZER_MIN_MID1_GAIN, EXTEfx.AL_EQUALIZER_MAX_MID1_GAIN));
		errorCheck();
	}

	/** 
	 * @return the current equalizer first mid width. 
	 */
	public final float getMid1Width() 
	{
		return mid1Width;
	}

	/** 
	 * Set equalizer first mid width (0.01 to 1.0). 
	 * @param mid1Width the new value.
	 */
	public final void setMid1Width(float mid1Width) 
	{
		this.mid1Width = mid1Width;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID1_WIDTH, MathUtils.clampValue(mid1Width, EXTEfx.AL_EQUALIZER_MIN_MID1_WIDTH, EXTEfx.AL_EQUALIZER_MAX_MID1_WIDTH));
		errorCheck();
	}

	/** 
	 * @return the current equalizer second mid center in Hertz. 
	 */
	public final float getMid2Center() 
	{
		return mid2Center;
	}

	/** 
	 * Set equalizer second mid center in Hertz (1000.0 to 8000.0). 
	 * @param mid2Center the new value.
	 */
	public final void setMid2Center(float mid2Center) 
	{
		this.mid2Center = mid2Center;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID2_CENTER, MathUtils.clampValue(mid2Center, EXTEfx.AL_EQUALIZER_MIN_MID2_CENTER, EXTEfx.AL_EQUALIZER_MAX_MID2_CENTER));
		errorCheck();
	}

	/** 
	 * @return the current equalizer second mid gain. 
	 */
	public final float getMid2Gain() 
	{
		return mid2Gain;
	}

	/** 
	 * Set equalizer second mid gain (0.126 to 7.943). 
	 * @param mid2Gain the new value.
	 */
	public final void setMid2Gain(float mid2Gain) 
	{
		this.mid2Gain = mid2Gain;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID2_GAIN, MathUtils.clampValue(mid2Gain, EXTEfx.AL_EQUALIZER_MIN_MID2_GAIN, EXTEfx.AL_EQUALIZER_MAX_MID2_GAIN));
		errorCheck();
	}

	/** 
	 * @return the current equalizer second mid width. 
	 */
	public final float getMid2Width() 
	{
		return mid2Width;
	}

	/** 
	 * Set equalizer second mid width (0.01 to 1.0). 
	 * @param mid2Width the new value.
	 */
	public final void setMid2Width(float mid2Width) 
	{
		this.mid2Width = mid2Width;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_EQUALIZER_MID2_WIDTH, MathUtils.clampValue(mid2Width, EXTEfx.AL_EQUALIZER_MIN_MID2_WIDTH, EXTEfx.AL_EQUALIZER_MAX_MID2_WIDTH));
		errorCheck();
	}
	
}
