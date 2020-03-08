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
 * Distortion effect for sound sources.
 * @author Matthew Tropiano
 */
public class DistortionEffect extends OALEffect
{
	/** Distortion edge. */
	protected float edge;
	/** Distortion gain. */
	protected float gain;
	/** Distortion low-pass cutoff in Hertz. */
	protected float lowPassCutoff;
	/** Distortion equalizer centering in Hertz. */
	protected float eqCenter;
	/** Distortion equalizer bandwidth in Hertz. */
	protected float eqBandwidth;
	
	public DistortionEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_DISTORTION);
		setGain(EXTEfx.AL_DISTORTION_DEFAULT_GAIN);
		setEdge(EXTEfx.AL_DISTORTION_DEFAULT_EDGE);
		setLowPassCutoff(EXTEfx.AL_DISTORTION_DEFAULT_LOWPASS_CUTOFF);
		setEqualizerCenter(EXTEfx.AL_DISTORTION_DEFAULT_EQCENTER);
		setEqualizerBandwidth(EXTEfx.AL_DISTORTION_DEFAULT_EQBANDWIDTH);
	}
	
	/** 
	 * @return the current distortion gain. 
	 */
	public final float getGain()
	{
		return gain;
	}

	/** 
	 * Set distortion gain (0.01 to 1.0). 
	 * @param gain the new value.
	 */
	public final void setGain(float gain)
	{
		this.gain = gain;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_DISTORTION_GAIN, MathUtils.clampValue(gain, EXTEfx.AL_DISTORTION_MIN_GAIN, EXTEfx.AL_DISTORTION_MAX_GAIN));
		errorCheck();
	}

	/** 
	 * @return the current distortion edge. 
	 */
	public final float getEdge()
	{
		return edge;
	}

	/** 
	 * Set distortion edge (0.0 to 1.0). 
	 * @param edge the new value.
	 */
	public final void setEdge(float edge)
	{
		this.edge = edge;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_DISTORTION_EDGE, MathUtils.clampValue(edge, EXTEfx.AL_DISTORTION_MIN_EDGE, EXTEfx.AL_DISTORTION_MAX_EDGE));
		errorCheck();
	}

	/** 
	 * @return the current distortion equalizer bandwidth in Hertz. 
	 */
	public final float getEqualizerBandwidth()
	{
		return eqBandwidth;
	}

	/** 
	 * Set distortion equalizer bandwidth in Hertz (80.0 to 24000.0). 
	 * @param eqBandwidth the new value.
	 */
	public final void setEqualizerBandwidth(float eqBandwidth)
	{
		this.eqBandwidth = eqBandwidth;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_DISTORTION_EQBANDWIDTH, MathUtils.clampValue(eqBandwidth, EXTEfx.AL_DISTORTION_MIN_EQBANDWIDTH, EXTEfx.AL_DISTORTION_MAX_EQBANDWIDTH));
		errorCheck();
	}

	/** 
	 * @return the current  distortion equalizer centering in Hertz. 
	 */
	public final float getEqualizerCenter()
	{
		return eqCenter;
	}

	/** 
	 * Set distortion equalizer centering in Hertz (80.0 to 24000.0). 
	 * @param eqCenter the new value.
	 */
	public final void setEqualizerCenter(float eqCenter)
	{
		this.eqCenter = eqCenter;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_DISTORTION_EQCENTER, MathUtils.clampValue(eqCenter, EXTEfx.AL_DISTORTION_MIN_EQCENTER, EXTEfx.AL_DISTORTION_MAX_EQCENTER));
		errorCheck();
	}

	/** 
	 * @return the current  distortion low-pass cutoff in Hertz. 
	 */
	public final float getLowPassCutoff()
	{
		return lowPassCutoff;
	}

	/** 
	 * Set distortion low-pass cutoff in Hertz (80.0 to 24000.0). 
	 * @param lowPassCutoff the new value.
	 */
	public final void setLowPassCutoff(float lowPassCutoff)
	{
		this.lowPassCutoff = lowPassCutoff;
		EXTEfx.alEffectf(getName(), EXTEfx.AL_DISTORTION_LOWPASS_CUTOFF, MathUtils.clampValue(lowPassCutoff, EXTEfx.AL_DISTORTION_MIN_LOWPASS_CUTOFF, EXTEfx.AL_DISTORTION_MAX_LOWPASS_CUTOFF));
		errorCheck();
	}

}
