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

/**
 * Compressor effect for sound sources.
 * @author Matthew Tropiano
 */
public class CompressorEffect extends OALEffect
{
	/** Compressor state. */
	protected boolean enabled;
	
	public CompressorEffect(OALContext context)
	{
		super(context, EXTEfx.AL_EFFECT_COMPRESSOR);
		setEnabled(EXTEfx.AL_COMPRESSOR_DEFAULT_ONOFF != 0);
	}

	/** 
	 * @return true if the effect is enabled, false if not. 
	 */
	public final boolean isEnabled()
	{
		return enabled;
	}

	/** 
	 * Sets if the effect is enabled. 
	 * @param enabled true if so, false if not.
	 */
	public final void setEnabled(boolean enabled)
	{
		this.enabled = enabled;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alEffecti(getName(), EXTEfx.AL_COMPRESSOR_ONOFF, enabled ? 1 : 0);
			errorCheck();
		}
	}
	
}
