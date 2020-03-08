/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.AL11;

import com.blackrook.gloop.openal.exception.SoundException;

/**
 * Generic OpenAL handle type.
 * Essentially an object that is not an integer id - this wraps a memory address.
 * <p>Creation paradigm around a handle may be more complex than just "create object" and set values. 
 * Handles are closer to the native layer, and may require additional parameters. 
 * <b>As such, {@link #create()} MUST be explicitly called by implementors of this class after the constructor!</b>
 * @author Matthew Tropiano
 */
public abstract class OALHandle
{
	/** This object's managing system. */
	private OALSystem system;
	/** This object's handle. */
	private long handle;
	/** Was this object allocated? */
	private boolean allocated;

	/**
	 * Allocates a new OpenAL handle.
	 * This just sets
	 * @param system the managing system.
	 */
	protected OALHandle(OALSystem system)
	{
		this.system = system;
		this.handle = 0L;
	}
	
	/**
	 * @return this OALHandle's OpenAL address handle.
	 */
	public final long getHandle()
	{
		return handle;
	}

	/**
	 * @return true if this handle was allocated, false if not.
	 */
	public boolean isCreated() 
	{
		return allocated;
	}
	
	/**
	 * Creates this handle. Does nothing if already created.
	 * @throws SoundException if a problem occurs during allocation.
	 * @see #allocate()
	 */
	protected final void create() throws SoundException 
	{
		if (!allocated)
		{
			if ((handle = allocate()) == 0)
				throw new SoundException("Handle for " + getClass().getSimpleName() + " could not be allocated!");
			system.registerHandle(this);
		}
		allocated = true;
	}

	/**
	 * Destroys this handle. Does nothing if already destroyed.
	 * @throws SoundException if a problem occurs during free.
	 * @see #free()
	 */
	public void destroy() throws SoundException 
	{
		if (allocated)
		{
			if (!free())
				throw new SoundException("Handle for " + getClass().getSimpleName() + " could not be deleted!");				
			handle = 0L;
			system.unregisterHandle(this);
			system = null;
		}
		allocated = false;
	}

	/**	 
	 * Allocates a new type of this handle in OpenAL.
	 * Called by {@link #create()}.
	 * @return the native address of this new handle.
	 * @throws SoundException if the allocation cannot happen.
	 */
	protected abstract long allocate() throws SoundException;
	
	/**
	 * Destroys this object (deallocates it on OpenAL).
	 * Called by {@link #destroy()}.
	 * @return true if deleted, false if not.
	 * @throws SoundException if the deallocation cannot happen.
	 */
	protected abstract boolean free() throws SoundException;
	
	/**
	 * Convenience method for clearing the OpenAL error state.
	 */
	protected void clearError()
	{
		while (AL11.alGetError() != AL11.AL_NO_ERROR) ;
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
	public int hashCode() 
	{
		return Long.hashCode(handle);
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (obj instanceof OALHandle)
			return equals((OALHandle)obj);
		return super.equals(obj);
	}

	/**
	 * Tests if this OpenAL handle equals the provided one.
	 * @param handle the handle to test.
	 * @return true if so, false if not.
	 */
	public boolean equals(OALHandle handle) 
	{
		return getClass().equals(handle.getClass()) && this.handle == handle.handle;
	}

	/**
	 * Frees this object from OpenAL.
	 * Safe, since OpenAL is thread-safe.
	 * AS ALWAYS, NEVER CALL DIRECTLY. 
	 */
	@Override
	public void finalize() throws Throwable
	{
		destroy();
		system.unregisterHandle(this);
		super.finalize();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getHandle();
	}

}
