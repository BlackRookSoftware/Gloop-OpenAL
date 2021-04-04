/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALSystem.ContextLock;

/**
 * Filter object for OpenAL sources.
 * @author Matthew Tropiano
 */
public abstract class OALFilter extends OALObject
{
	protected OALFilter(OALContext context, int alFilterType)
	{
		super(context);
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alFilteri(getName(), EXTEfx.AL_FILTER_TYPE, alFilterType);
			errorCheck();
		}
	}

	@Override
	protected int allocate()
	{
		int out;
		try (ContextLock lock = requestContext()) 
		{
			clearError();
			out = EXTEfx.alGenFilters();
			errorCheck();
		}
		return out;
	}

	@Override
	protected void free()
	{
		try (ContextLock lock = requestContext()) 
		{
			clearError();
			EXTEfx.alDeleteFilters(getName());
			errorCheck();
		}
	}

}
