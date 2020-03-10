/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.AL11;

import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.exception.SoundException;

/**
 * Generic OpenAL object type.
 * Since this is managed internally by OpenAL, the handles to these
 * objects are "names" (aka integer IDs) rather than memory addresses.
 * @author Matthew Tropiano
 */
public abstract class OALObject
{
	/** This object's owning context. */
	private OALContext context;
	/** This object's ALId. */
	private int alId;
	/** Was this object allocated? */
	private boolean allocated;

	/**
	 * Allocates a new OpenAL object. Calls allocate().
	 * @param context the managing context.
	 */
	protected OALObject(OALContext context)
	{
		this.context = context;
		clearError();
		this.alId = allocate(); 
		context.registerObject(this);
		this.allocated = true; 
	}
	
	/**
	 * @return this OALObject's OpenAL object id.
	 */
	public final int getName()
	{
		return alId;
	}

	@Override
	public int hashCode() 
	{
		return Integer.hashCode(alId);
	}
	
	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof OALObject)
			return equals((OALObject)obj);
		return super.equals(obj);
	}
	
	/**
	 * Tests if this OpenAL object equals the provided one.
	 * @param obj the object to test.
	 * @return true if so, false if not.
	 */
	public boolean equals(OALObject obj) 
	{
		return getClass().equals(obj.getClass()) && alId == obj.alId;
	}

	/**
	 * Destroys this object.
	 * @throws SoundException if an error occurred destroying the object.
	 */
	public void destroy() 
	{
		if (allocated)
		{
			free();
			alId = 0;
			context.unregisterObject(this);
			context = null;
		}
		allocated = false;
	}

	/**	 
	 * Allocates a new type of this object in OpenAL.
	 * Called by OALObject constructor.
	 * @return the ALId of this new object.
	 * @throws SoundException if the allocation cannot happen.
	 */
	protected abstract int allocate();
	
	/**
	 * Destroys this object (deallocates it on OpenAL).
	 * This is called by destroy().
	 * @throws SoundException if the deallocation cannot happen.
	 */
	protected abstract void free();
	
	/**
	 * Sets a new context as current, in order hold a context as current for an AL call.
	 * @return a context lock for notifying releasing control of the thread.
	 */
	protected ContextLock requestContext()
	{
		return context.setCurrentContext();
	}
	
	/**
	 * Convenience method for clearing the OpenAL error state.
	 */
	protected void clearError()
	{
		int err;
		do {
			err = AL11.alGetError();
		} while (err != AL11.AL_NO_ERROR);
	}

	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	protected final void errorCheck()
	{
		int error = AL11.alGetError();
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("Object " + getClass().getSimpleName() + ": AL returned \"" + AL11.alGetString(error) + "\"");
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getName();
	}

}
