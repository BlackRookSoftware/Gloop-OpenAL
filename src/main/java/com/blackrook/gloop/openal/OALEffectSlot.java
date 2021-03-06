/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALSystem.ContextLock;

/**
 * Auxiliary Effect Slot for enforcing effect mixing rules.
 * @author Matthew Tropiano
 */
public class OALEffectSlot extends OALObject
{
	/** Effect bound to this slot. */
	protected OALEffect effect;
	
	/** Effect slot gain. */
	protected float slotGain;
	/** Does this auto update itself if the Effect changes? */
	protected boolean autoUpdating;
	
	OALEffectSlot(OALContext context)
	{
		super(context);
		setEffect(null);
		setAutoUpdating(true);
	}
	
	@Override
	protected int allocate()
	{
		int out;
		try (ContextLock lock = requestContext())
		{
			clearError();
			out = EXTEfx.alGenAuxiliaryEffectSlots();
			errorCheck();
		}
		return out;
	}

	@Override
	protected final void free()
	{
		try (ContextLock lock = requestContext())
		{
			clearError();
			EXTEfx.alDeleteAuxiliaryEffectSlots(getName());
			errorCheck();
		}
	}

	/**
	 * Sets an Effect in this slot. Can be null to remove the Effect.
	 * @param effect the effect to add.
	 */
	public void setEffect(OALEffect effect)
	{
		this.effect = effect;
		try (ContextLock lock = requestContext())
		{
			EXTEfx.alAuxiliaryEffectSloti(getName(), EXTEfx.AL_EFFECTSLOT_EFFECT, effect == null ? EXTEfx.AL_EFFECT_NULL : effect.getName());
			errorCheck();
		}
	}
	
	/**
	 * @return the reference of the Effect bound to this slot.
	 */
	public OALEffect getEffect()
	{
		return effect;
	}
	
	/**
	 * Removes the effect in this slot.
	 * @return the effect removed.
	 */
	public OALEffect removeEffect()
	{
		OALEffect e = effect;
		setEffect(null);
		return e;
	}
	
	/**
	 * @return the effect slot gain.
	 */
	public float getGain()
	{
		return slotGain;
	}

	/**
	 * Sets effect slot gain.
	 * @param gain the gain to use.
	 */
	public void setGain(float gain)
	{
		slotGain = gain;
		try (ContextLock lock = requestContext())
		{
			EXTEfx.alAuxiliaryEffectSlotf(getName(), EXTEfx.AL_EFFECTSLOT_GAIN, gain);
			errorCheck();
		}
	}

	/** 
	 * Does this auto update itself if the Effect changes? 
	 * @return true if so, false if not.
	 */
	public final boolean isAutoUpdating()
	{
		return autoUpdating;
	}

	/** 
	 * Sets if this auto update itself if the Effect changes. 
	 * @param autoUpdate true if so, false if not.
	 */
	public final void setAutoUpdating(boolean autoUpdate)
	{
		this.autoUpdating = autoUpdate;
		try (ContextLock lock = requestContext()) 
		{
			EXTEfx.alAuxiliaryEffectSloti(getName(), EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, autoUpdate ? AL11.AL_TRUE : AL11.AL_FALSE);
			errorCheck();
		}
	}

}
