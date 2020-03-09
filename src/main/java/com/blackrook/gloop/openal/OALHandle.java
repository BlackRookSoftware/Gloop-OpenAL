/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import com.blackrook.gloop.openal.exception.SoundException;

/**
 * Generic OpenAL handle type.
 * Essentially an object that is not an integer id - this wraps a memory address.
 * @author Matthew Tropiano
 */
public abstract class OALHandle
{
	/**
	 * Allocates a new OpenAL handle.
	 */
	protected OALHandle() {}
	
	/**
	 * @return this handle's OpenAL address handle.
	 */
	public abstract long getHandle();

	/**
	 * @return true if this handle was allocated, false if not.
	 */
	public abstract boolean isCreated(); 
	
	/**
	 * Destroys this handle. Does nothing if already destroyed.
	 * @throws SoundException if a problem occurs during free.
	 */
	public abstract void destroy() throws SoundException;

	@Override
	public int hashCode() 
	{
		return Long.hashCode(getHandle());
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
		return getClass().equals(handle.getClass()) && this.getHandle() == handle.getHandle();
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
		super.finalize();
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ' ' + getHandle();
	}

}
