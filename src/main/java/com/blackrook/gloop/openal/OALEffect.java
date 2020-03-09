/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.nio.IntBuffer;

import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

/**
 * Effect object for OpenAL sources.
 * TODO: Context locking (plus all effects).
 * @author Matthew Tropiano
 */
public abstract class OALEffect extends OALObject
{
	protected OALEffect(OALContext context, int alEffectType)
	{
		super(context);
		EXTEfx.alEffecti(getName(), EXTEfx.AL_EFFECT_TYPE, alEffectType);
	}
	
	@Override
	protected final int allocate()
	{
		int out;
		clearError();
		try (MemoryStack stack = MemoryStack.stackPush())
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
		clearError();
		EXTEfx.alDeleteEffects(getName());
		errorCheck();
	}
	
}
