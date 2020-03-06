/*******************************************************************************
 * Copyright (c) 2014, 2015 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *
 * Contributors:
 *     Matt Tropiano - initial API and implementation
 *******************************************************************************/
package com.blackrook.gloop.openal;

import java.nio.IntBuffer;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;
import org.lwjgl.system.MemoryStack;

import com.blackrook.gloop.openal.exception.SoundException;

/**
 * Filter object for OpenAL sources.
 * @author Matthew Tropiano
 */
public abstract class OALFilter extends OALObject
{
	protected OALFilter(OALSystem system, int alFilterType)
	{
		super(system);
		EXTEfx.alFilteri(getALId(), EXTEfx.AL_FILTER_TYPE, alFilterType);
	}

	@Override
	protected int allocate() throws SoundException
	{
		int out;
		AL11.alGetError();
		try (MemoryStack stack = MemoryStack.stackGet())
		{
			IntBuffer buf = stack.mallocInt(1);
			EXTEfx.alGenFilters(buf);
			errorCheck();
			out = buf.get(0);
		}
		return out;
	}

	@Override
	protected void free() throws SoundException
	{
		AL11.alGetError();
		EXTEfx.alDeleteFilters(getALId());
		errorCheck();
	}

}