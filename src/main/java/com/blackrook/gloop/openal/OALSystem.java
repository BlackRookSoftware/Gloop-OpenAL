/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.openal.AL;
import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALContext.DistanceModel;
import com.blackrook.gloop.openal.effect.AutowahEffect;
import com.blackrook.gloop.openal.effect.ChorusEffect;
import com.blackrook.gloop.openal.effect.CompressorEffect;
import com.blackrook.gloop.openal.effect.DistortionEffect;
import com.blackrook.gloop.openal.effect.EchoEffect;
import com.blackrook.gloop.openal.effect.EqualizerEffect;
import com.blackrook.gloop.openal.effect.FlangerEffect;
import com.blackrook.gloop.openal.effect.FrequencyShiftEffect;
import com.blackrook.gloop.openal.effect.PitchShiftEffect;
import com.blackrook.gloop.openal.effect.ReverbEffect;
import com.blackrook.gloop.openal.effect.RingModulatorEffect;
import com.blackrook.gloop.openal.effect.VocalMorpherEffect;
import com.blackrook.gloop.openal.exception.SoundException;
import com.blackrook.gloop.openal.exception.SoundSystemException;
import com.blackrook.gloop.openal.filter.BandPassFilter;
import com.blackrook.gloop.openal.filter.HighPassFilter;
import com.blackrook.gloop.openal.filter.LowPassFilter;

/**
 * This class is a central sound system class designed to manage an OpenAL instance and environment.
 * @author Matthew Tropiano
 */
public final class OALSystem
{
	/** The current device. */
	private OALDevice currentDevice;
	/** The current context instance. */
	private OALContext currentContext;

	/** Handle references. */
	private Set<OALHandle> createdHandles;

	/**
	 * Creates a new SoundSystem with the current device as a new sound device and 
	 * the current context as its first context, made current.
	 */
	public OALSystem()
	{
		this(null);
	}
	
	/**
	 * Creates a new SoundSystem.
	 * NOTE: Passing 'null' as the device name will create a new system with the default (current) device and context.
	 * @param deviceName the name of the device.
	 */
	public OALSystem(String deviceName)
	{
		createdHandles = new HashSet<>();
		String dname = deviceName != null ? "device \""+deviceName+"\"" : "default device";
		
		// create device.
		currentDevice = createDevice(deviceName);
		currentContext = createContext(currentDevice);
		if (!ALC11.alcMakeContextCurrent(currentContext.getHandle()))
			throw new SoundSystemException("The context for " + dname + " couldn't be made current.");
		
		AL.createCapabilities(currentDevice.getCapabilities());
		
		currentContext.setVendorName(AL11.alGetString(AL11.AL_VENDOR));
		currentContext.setVersionName(AL11.alGetString(AL11.AL_VERSION));
		currentContext.setRendererName(AL11.alGetString(AL11.AL_RENDERER));
		currentContext.setExtensions(AL11.alGetString(AL11.AL_EXTENSIONS).split("(\\s|\\n)+"));
		currentContext.setMaxEffectSlots(ALC11.alcGetInteger(currentDevice.getHandle(), EXTEfx.ALC_MAX_AUXILIARY_SENDS));
		currentContext.setListener(new OALListener(currentContext));
	}

	/**
	 * Adds a handle's reference as a managed object.
	 * @param handle the handle to add.
	 */
	void registerHandle(OALHandle handle)
	{
		synchronized (createdHandles)
		{
			createdHandles.add(handle);
		}
	}

	/**
	 * Removes a handle's reference as a managed object.
	 * @param handle the handle to add.
	 */
	void unregisterHandle(OALHandle handle)
	{
		synchronized (createdHandles)
		{
			createdHandles.remove(handle);
		}
	}

	/**
	 * Sets a new context as current.
	 * @param context the context to make current, or null for no context.
	 */
	public void setCurrentContext(OALContext context)
	{
		ALC11.alcMakeContextCurrent(context != null ? context.getHandle() : 0L);
		getContextError();
	}
	
	/**
	 * Suspends processing of the current context.
	 */
	public void suspendCurrentContext()
	{
		ALC11.alcSuspendContext(currentContext.getHandle());
		getContextError();
	}
	
	/**
	 * Resumes processing of the current context.
	 */
	public void processCurrentContext()
	{
		ALC11.alcProcessContext(currentContext.getHandle());
		getContextError();
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	public void getContextError()
	{
		int error = ALC11.alcGetError(currentDevice.getHandle());
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("OpenAL returned \""+ALC11.alcGetString(currentDevice.getHandle(), error)+"\".");
	}

	/**
	 * Allocates a new default device.
	 * @return the newly allocated device.
	 * @throws SoundSystemException if the device can't be created.
	 * @see #createDevice(String)
	 */
	public OALDevice createDevice()
	{
		return createDevice(null);
	}
	
	/**
	 * Creates a new device.
	 * @param name the name of the device (<code>null</code> for default).
	 * @return the newly allocated device.
	 * @throws SoundSystemException if the device can't be created.
	 */
	public OALDevice createDevice(String name)
	{
		return new OALDevice(this, name);
	}
	
	/**
	 * Creates a rendering context for a device.
	 * @param device the device to use.
	 * @param attributes the optional context attributes.
	 * @return the newly created context.
	 * @throws SoundSystemException if the context can't be created or there is no current context selected.
	 * @see #createDevice(String)
	 */
	public OALContext createContext(OALDevice device, OALContext.AttributeValue ... attributes)
	{
		return new OALContext(this, device, attributes);
	}
	
	/**
	 * @return the current device.
	 */
	public OALDevice getCurrentDevice() 
	{
		return currentDevice;
	}

	/**
	 * @return the current context.
	 */
	public OALContext getCurrentContext() 
	{
		return currentContext;
	}
	
	/**
	 * Runs all Shut Down hooks, destroys all contexts and closes all open devices.
	 */
	public void shutDown()
	{
		// suspend context before we delete. 
		suspendCurrentContext();
		getContextError();
		getCurrentContext().destroy();
		getContextError();
		setCurrentContext(null);
		getContextError();
		
		currentContext = null;
		currentDevice = null;

		// TODO: Gather and sort by object type - some handles need to be deleted before others.
		synchronized (createdHandles)
		{
			// need to copy set contents - deleting these handles will affect the set as we iterate.
			OALHandle[] toDelete = new OALHandle[createdHandles.size()];
			createdHandles.toArray(toDelete);
			createdHandles.clear();
			for (int i = 0; i < toDelete.length; i++)
				toDelete[i].destroy();
		}			
	}

	@Override
	public void finalize() throws Throwable
	{
		shutDown();
		super.finalize();
	}
	
	
	
}


