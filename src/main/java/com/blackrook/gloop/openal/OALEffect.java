/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.nio.IntBuffer;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

/**
 * Effect object for OpenAL sources.
 * @author Matthew Tropiano
 */
public abstract class OALEffect extends OALObject
{
	protected OALEffect(OALSystem system, int alEffectType)
	{
		super(system);
		EXTEfx.alEffecti(getALId(), EXTEfx.AL_EFFECT_TYPE, alEffectType);
	}
	
	@Override
	protected final int allocate()
	{
		int out;
		AL11.alGetError();
		try (MemoryStack stack = MemoryStack.stackGet())
		{
			IntBuffer buf = stack.mallocInt(1);
			EXTEfx.alGenEffects(buf);
			errorCheck();
			out = buf.get(0);
		}
		return out;
	}
	
	@Override
	protected final void free()
	{
		AL11.alGetError();
		EXTEfx.alDeleteEffects(getALId());
		errorCheck();
	}
	
}
