/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;

import com.blackrook.gloop.openal.exception.SoundException;

/**
 * A device handle in OpenAL. Represents the output sound device.
 * <p>Also creates its capabilities (see {@link #getCapabilities()}).
 * @author Matthew Tropiano
 */
public class OALDevice extends OALHandle
{
	/** The device name. */
	private String name;
	/** The device name. */
	private ALCCapabilities capabilities;
	
	// Default device.
	OALDevice(OALSystem system)
	{
		this(system, null);
	}

	// Specific device.
	OALDevice(OALSystem system, String name)
	{
		super(system);
		this.name = name;
		create();
		this.name = name == null ? "DEFAULT" : name;
		capabilities = ALC.createCapabilities(getHandle());
	}

	/**
	 * @return this device's name, or "DEFAULT" if this represents the default device.
	 */
	public String getName() 
	{
		return name;
	}
	
	/**
	 * @return this device's OpenAL capabilities.
	 */
	public ALCCapabilities getCapabilities() 
	{
		return capabilities;
	}
	
	@Override
	protected long allocate() throws SoundException 
	{
		return ALC11.alcOpenDevice(name);
	}

	@Override
	protected void free() throws SoundException 
	{
		ALC11.alcCloseDevice(getHandle());
	}

}
