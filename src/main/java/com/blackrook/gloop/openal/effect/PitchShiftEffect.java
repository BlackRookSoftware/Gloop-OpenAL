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
 * Pitch shifter effect for sources.
 * @author Matthew Tropiano
 */
public class PitchShiftEffect extends OALEffect
{
	/** Pitch shifter coarse tuning in semitones. */
	protected int coarse;
	/** Pitch shifter fine tuning in cents. */
	protected int fine;
	
	public PitchShiftEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_PITCH_SHIFTER);
		setCoarseTuning(EXTEfx.AL_PITCH_SHIFTER_DEFAULT_COARSE_TUNE);
		setFineTuning(EXTEfx.AL_PITCH_SHIFTER_DEFAULT_FINE_TUNE);
	}

	/** 
	 * @return the current coarse tuning in semitones. 
	 v*/
	public final int getCoarseTuning()
	{
		return coarse;
	}

	/** 
	 * Sets the coarse tuning in semitones (-12 to 12). 
	 * @param coarse the new value.
	 */
	public final void setCoarseTuning(int coarse)
	{
		this.coarse = coarse;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(
				getName(), 
				EXTEfx.AL_PITCH_SHIFTER_COARSE_TUNE,
				MathUtils.clampValue(coarse, EXTEfx.AL_PITCH_SHIFTER_MIN_COARSE_TUNE, EXTEfx.AL_PITCH_SHIFTER_MAX_COARSE_TUNE)
			);
			errorCheck();
		}
	}

	/** 
	 * @return the current fine tuning in cents. 
	 */
	public final int getFineTuning()
	{
		return fine;
	}

	/** 
	 * Sets the fine tuning in cents (-50 to 50). 
	 * @param fine the new value.
	 */
	public final void setFineTuning(int fine)
	{
		this.fine = fine;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(
				getName(), 
				EXTEfx.AL_PITCH_SHIFTER_FINE_TUNE, 
				MathUtils.clampValue(fine, EXTEfx.AL_PITCH_SHIFTER_MIN_FINE_TUNE, EXTEfx.AL_PITCH_SHIFTER_MAX_FINE_TUNE)
			);
			errorCheck();
		}
	}
	
}
