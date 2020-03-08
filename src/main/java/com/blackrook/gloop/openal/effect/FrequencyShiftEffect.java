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
 * Frequency shift effects for sources.
 * @author Matthew Tropiano
 */
public class FrequencyShiftEffect extends OALEffect
{
	/** Shift direction enumeration. */
	public static enum Direction
	{
		DOWN(EXTEfx.AL_FREQUENCY_SHIFTER_DIRECTION_DOWN),
		UP(EXTEfx.AL_FREQUENCY_SHIFTER_DIRECTION_UP),
		OFF(EXTEfx.AL_FREQUENCY_SHIFTER_DIRECTION_OFF);
		
		final int alVal;
		private Direction(int alVal) {this.alVal = alVal;}
	}
	
	/** Frequency shifter frequency. */
	protected float frequency;
	/** Frequency shifter left direction. */
	protected Direction leftDir;
	/** Frequency shifter right direction. */
	protected Direction rightDir;
	
	public FrequencyShiftEffect(OALSystem system)
	{
		super(system, EXTEfx.AL_EFFECT_FREQUENCY_SHIFTER);
		setFrequency(EXTEfx.AL_FREQUENCY_SHIFTER_DEFAULT_FREQUENCY);
		setLeftDirection(Direction.DOWN);
		setRightDirection(Direction.DOWN);
	}

	/** 
	 * @return the current frequency shifter frequency. 
	 */
	public final float getFrequency()
	{
		return frequency;
	}

	/** 
	 * Set frequency shifter frequency (0.0 to 24000.0). 
	 * @param frequency the new value.
	 */
	public final void setFrequency(float frequency)
	{
		this.frequency = frequency;
		EXTEfx.alEffectf(getALId(), EXTEfx.AL_FREQUENCY_SHIFTER_FREQUENCY, MathUtils.clampValue(frequency, EXTEfx.AL_FREQUENCY_SHIFTER_MIN_FREQUENCY, EXTEfx.AL_FREQUENCY_SHIFTER_MAX_FREQUENCY));
		errorCheck();
	}

	/** 
	 * @return the current frequency shifter left direction. 
	 */
	public final Direction getLeftDirection()
	{
		return leftDir;
	}

	/** 
	 * Set frequency shifter left direction.
	 * @param leftDir the direction type. 
	 */
	public final void setLeftDirection(Direction leftDir)
	{
		this.leftDir = leftDir;
		EXTEfx.alEffecti(getALId(), EXTEfx.AL_FREQUENCY_SHIFTER_LEFT_DIRECTION, leftDir.alVal);
		errorCheck();
	}

	/** 
	 * @return the current frequency shifter right direction. 
	 */
	public final Direction getRightDirection()
	{
		return rightDir;
	}

	/** 
	 * Set frequency shifter right direction.
	 * @param rightDir the direction type. 
	 */
	public final void setRightDirection(Direction rightDir)
	{
		this.rightDir = rightDir;
		EXTEfx.alEffecti(getALId(), EXTEfx.AL_FREQUENCY_SHIFTER_RIGHT_DIRECTION, rightDir.alVal);
		errorCheck();
	}
	
}
