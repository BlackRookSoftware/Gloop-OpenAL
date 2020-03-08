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

import com.blackrook.gloop.openal.exception.SoundException;

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
	
	/**
	 * Creates a new auxiliary effect slot.
	 */
	OALEffectSlot(OALSystem system)
	{
		super(system);
		setEffect(null);
		setAutoUpdating(true);
	}
	
	@Override
	protected int allocate() throws SoundException
	{
		int out;
		AL11.alGetError();
		try (MemoryStack stack = MemoryStack.stackPush())
		{
			IntBuffer buf = stack.mallocInt(1);
			EXTEfx.alGenAuxiliaryEffectSlots(buf);
			errorCheck();
			out = buf.get(0);
		}
		return out;
	}

	@Override
	protected final void free() throws SoundException
	{
		AL11.alGetError();
		EXTEfx.alDeleteAuxiliaryEffectSlots(getALId());
		errorCheck();
	}

	/**
	 * Sets an Effect in this slot. Can be null to remove the Effect.
	 * @param effect the effect to add.
	 */
	public void setEffect(OALEffect effect)
	{
		this.effect = effect;
		EXTEfx.alAuxiliaryEffectSloti(getALId(), EXTEfx.AL_EFFECTSLOT_EFFECT, effect == null ? EXTEfx.AL_EFFECT_NULL : effect.getALId());
		errorCheck();
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
		EXTEfx.alAuxiliaryEffectSlotf(getALId(), EXTEfx.AL_EFFECTSLOT_GAIN, gain);
		errorCheck();
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
		EXTEfx.alAuxiliaryEffectSloti(getALId(), EXTEfx.AL_EFFECTSLOT_AUXILIARY_SEND_AUTO, autoUpdate ? AL11.AL_TRUE : AL11.AL_FALSE);
		errorCheck();
	}

}
