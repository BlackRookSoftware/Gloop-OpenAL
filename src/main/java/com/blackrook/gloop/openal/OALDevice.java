/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCCapabilities;
import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.exception.SoundException;
import com.blackrook.gloop.openal.exception.SoundSystemException;

/**
 * A device handle in OpenAL. Represents the output sound device.
 * <p>Also creates its capabilities (see {@link #getCapabilities()}).
 * @author Matthew Tropiano
 */
public class OALDevice extends OALHandle
{
	private static final Object CONTEXT_CREATE_MUTEX = new Object();
	private static final AtomicInteger MULTICONTEXT_LATCH = new AtomicInteger(0);
	
	/** System. */
	private OALSystem system;
	/** The device name. */
	private String name;
	/** The device name. */
	private ALCCapabilities capabilities;
	
	/** This object's handle. */
	private long handle;
	/** Was this object allocated? */
	private boolean allocated;

	/** Map of created devices. */
	private Set<OALContext> openContexts;

	// Default device.
	OALDevice(OALSystem system)
	{
		this(system, null);
	}

	// Specific device.
	OALDevice(OALSystem system, String name)
	{
		this.system = system;
		this.name = name;
		this.openContexts = new HashSet<>(2, 1f);
		if ((handle = ALC11.alcOpenDevice(name)) == 0)
			throw new SoundException("Handle for OALDevice could not be allocated!");
		this.allocated = true;
		this.name = name == null ? "DEFAULT" : name;
		this.capabilities = ALC.createCapabilities(getHandle());
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
	public long getHandle()
	{
		return handle;
	}

	@Override
	public boolean isCreated()
	{
		return allocated;
	}

	@Override
	public void destroy() throws SoundException 
	{
		if (allocated)
		{
			if (!free())
				throw new SoundException("Handle for OALDevice could not be deleted!");				
			handle = 0L;
			allocated = false;
		}
	}

	protected boolean free() throws SoundException 
	{
		synchronized (openContexts)
		{
			// need to copy set contents - deleting these handles will affect the set as we iterate.
			OALHandle[] toDelete = new OALHandle[openContexts.size()];
			openContexts.toArray(toDelete);
			for (int i = 0; i < toDelete.length; i++)
			{
				toDelete[i].destroy();
				openContexts.remove(toDelete[i]);
			}
		}
		return ALC11.alcCloseDevice(getHandle());
	}

	/**
	 * Sets a new context as current.
	 * @param context the context to make current, or null for no context.
	 */
	ContextLock setCurrentContext(OALContext context)
	{
		return system.setCurrentContext(context);
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException if an error is raised.
	 * @throws SoundException if an error was found. 
	 */
	public void getContextError()
	{
		int error = ALC11.alcGetError(getHandle());
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("OpenAL returned \"" + ALC11.alcGetString(getHandle(), error) + "\".");
	}

	/**
	 * Creates a rendering context for a device.
	 * @param attributes the optional context attributes.
	 * @return the newly created context.
	 * @throws SoundSystemException if the context can't be created or there is no current context selected.
	 */
	public OALContext createContext(OALContext.AttributeValue ... attributes)
	{
		synchronized(CONTEXT_CREATE_MUTEX)
		{
			// if already one, upgrade to strict lock.
			if (MULTICONTEXT_LATCH.get() == 1)
				system.upgradeLock();
			MULTICONTEXT_LATCH.incrementAndGet();
			
			OALContext context = new OALContext(this, attributes);
			try (ContextLock lock = setCurrentContext(context))
			{
				context.setCapabilities(AL.createCapabilities(getCapabilities()));
				context.setVendorName(AL11.alGetString(AL11.AL_VENDOR));
				context.setVersionName(AL11.alGetString(AL11.AL_VERSION));
				context.setRendererName(AL11.alGetString(AL11.AL_RENDERER));
				context.setExtensions(AL11.alGetString(AL11.AL_EXTENSIONS).split("(\\s|\\n)+"));
				context.setMaxEffectSlots(ALC11.alcGetInteger(getHandle(), EXTEfx.ALC_MAX_AUXILIARY_SENDS));
				context.setListener(new OALListener(context));
			}
			openContexts.add(context);
			return context;
		}
	}
	
}
