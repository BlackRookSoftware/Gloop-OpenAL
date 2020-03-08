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
import com.blackrook.gloop.openal.enums.DistanceModel;
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
	/** This device. */
	private OALDevice alcDevice;
	/** This context instance. */
	private OALContext alcContext;
	/** Distance model. */
	private DistanceModel currentDistanceModel;

	/** Listener. */
	private OALListener listener;
	/** Object references. */
	private Set<OALObject> createdObjects;
	/** Handle references. */
	private Set<OALHandle> createdHandles;

	/** Maximum effect slots per source. */
	private int maxEffectSlots;

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
		createdObjects = new HashSet<>();
		createdHandles = new HashSet<>();
		String dname = deviceName != null ? "device \""+deviceName+"\"" : "default device";
		
		// create device.
		alcDevice = createDevice(deviceName);
		alcContext = createContext(alcDevice);
		if (!ALC11.alcMakeContextCurrent(alcContext.getHandle()))
			throw new SoundSystemException("The context for " + dname + " couldn't be made current.");
		getError();
		
		AL.createCapabilities(alcDevice.getCapabilities());
		getError();
		
		maxEffectSlots = AL11.alGetInteger(EXTEfx.ALC_MAX_AUXILIARY_SENDS);
		getError();
		
		listener = new OALListener();
	}
	
	/**
	 * Adds an object's reference as a managed object.
	 * @param object the object to add.
	 */
	void registerObject(OALObject object)
	{
		synchronized (createdObjects)
		{
			createdObjects.add(object);
		}
	}

	/**
	 * Removes an object's reference as a managed object.
	 * @param object the object to add.
	 */
	void unregisterObject(OALObject object)
	{
		synchronized (createdObjects)
		{
			createdObjects.remove(object);
		}
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
	 * Suspends processing of the current context.
	 */
	public void suspendCurrentContext()
	{
		ALC11.alcSuspendContext(alcContext.getHandle());
		getError();
	}
	
	/**
	 * Resumes processing of the current context.
	 */
	public void processCurrentContext()
	{
		ALC11.alcProcessContext(alcContext.getHandle());
		getError();
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
	 * Allocates a new source and assigns it internally to the current context.
	 * @return the newly allocated source.
	 * @throws SoundSystemException if the source can't be created or there is no current context selected.
	 */
	public OALSource createSource()
	{
		return createSource(false);
	}
	
	/**
	 * Allocates a new source and assigns it internally to the current context.
	 * @param autoVelocity if true, set auto velocity to on for this Source.
	 * @return the newly allocated source.
	 * @throws SoundSystemException if the source can't be created.
	 */
	public OALSource createSource(boolean autoVelocity)
	{
		return new OALSource(this, autoVelocity, maxEffectSlots);
	}

	/**
	 * Allocates a new buffer for loading data into. Buffers are independent
	 * of device context. 
	 * @return	a newly allocated buffer.
	 * @throws	SoundException	if the Buffer can't be allocated somehow.
	 */
	public OALBuffer createBuffer()
	{
		return new OALBuffer(this);
	}
	
	/**
	 * Allocates a new buffer for loading data into. Buffers are independent
	 * of device context. 
	 * @param amount the amount of buffers to create.
	 * @return a set of newly allocated buffers.
	 * @throws SoundException if the Buffer can't be allocated somehow.
	 */
	public OALBuffer[] createBuffers(int amount)
	{
		OALBuffer[] out = new OALBuffer[amount];
		for (int i = 0; i < amount; i++)
			out[i] = createBuffer();
		return out;
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	public void getError()
	{
		int error = AL11.alGetError();
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("OpenAL returned \""+AL11.alGetString(error)+"\".");
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	public void getContextError()
	{
		int error = ALC11.alcGetError(alcDevice.getHandle());
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("OpenAL returned \""+ALC11.alcGetString(alcDevice.getHandle(), error)+"\".");
	}
	
	/**
	 * Allocates a new buffer with data loaded into it. All of the sound data
	 * readable by the SoundData instance is read into the buffer.
	 * If you know that the data being loaded is very long or large, you
	 * should consider using a Streaming Source to conserve memory.
	 * Buffers are independant of device context. 
	 * @param handle the handle to the sound data to load into this buffer.
	 * @return a newly allocated buffer.
	 * @throws IOException if the data can't be read.
	 * @throws SoundException if the Buffer can't be allocated somehow.
	 */
	public OALBuffer createBuffer(JSPISoundHandle handle) throws IOException
	{
		return new OALBuffer(this, handle);
	}
	
	/**
	 * Allocates a new buffer with data loaded into it. All of the sound data
	 * readable by the SoundDataDecoder instance is read into the buffer.
	 * If you know that the data being loaded is very long or large, you
	 * should consider using a Streaming Source to conserve memory.
	 * Buffers are independent of device context. 
	 * @param dataDecoder the decoder of the sound data to load into this buffer.
	 * @return a newly allocated buffer.
	 * @throws IOException if the data can't be read.
	 * @throws SoundException if the Buffer can't be allocated somehow.
	 */
	public OALBuffer createBuffer(JSPISoundHandle.Decoder dataDecoder) throws IOException
	{
		return new OALBuffer(this, dataDecoder);
	}
	
	/**
	 * Creates a new Auxiliary Effect Slot for adding a filter and effects to Sources.
	 * These slots can be added to sources. If you have more than one source 
	 * that uses the same sets of filters and effects, it might be better to
	 * bind one slot to more than one source to save memory, especially if you
	 * need to alter an effect of filter for more than one sound that is playing.
	 * @return a new AuxEffectSlot object.
	 * @throws SoundException	if the slot can't be allocated somehow.
	 */
	public OALEffectSlot createEffectSlot()
	{
		return new OALEffectSlot(this);
	}
	
	/**
	 * Creates a new Autowah effect.
	 * @return	a new effect of this type with default values set.
	 */
	public AutowahEffect createAutowahEffect()
	{
		return new AutowahEffect(this);
	}
	
	/**
	 * Creates a new Chorus effect.
	 * @return	a new effect of this type with default values set.
	 */
	public ChorusEffect createChorusEffect()
	{
		return new ChorusEffect(this);
	}
	
	/**
	 * Creates a new Compressor effect.
	 * @return	a new effect of this type with default values set.
	 */
	public CompressorEffect createCompressorEffect()
	{
		return new CompressorEffect(this);
	}
	
	/**
	 * Creates a new Distortion effect.
	 * @return	a new effect of this type with default values set.
	 */
	public DistortionEffect createDistortionEffect()
	{
		return new DistortionEffect(this);
	}
	
	/**
	 * Creates a new Echo effect.
	 * @return	a new effect of this type with default values set.
	 */
	public EchoEffect createEchoEffect()
	{
		return new EchoEffect(this);
	}
	
	/**
	 * Creates a new Equalizer effect.
	 * @return	a new effect of this type with default values set.
	 */
	public EqualizerEffect createEqualizerEffect()
	{
		return new EqualizerEffect(this);
	}
	
	/**
	 * Creates a new Flanger effect.
	 * @return	a new effect of this type with default values set.
	 */
	public FlangerEffect createFlangerEffect()
	{
		return new FlangerEffect(this);
	}
	
	/**
	 * Creates a new Frequency Shift effect.
	 * @return	a new effect of this type with default values set.
	 */
	public FrequencyShiftEffect createFrequencyShiftEffect()
	{
		return new FrequencyShiftEffect(this);
	}
	
	/**
	 * Creates a new Pitch Shift effect.
	 * @return	a new effect of this type with default values set.
	 */
	public PitchShiftEffect createPitchShiftEffect()
	{
		return new PitchShiftEffect(this);
	}
	
	/**
	 * Creates a new Reverb effect.
	 * @return	a new effect of this type with default values set.
	 */
	public ReverbEffect createReverbEffect()
	{
		return new ReverbEffect(this);
	}
	
	/**
	 * Creates a new Ring Modulator effect.
	 * @return	a new effect of this type with default values set.
	 */
	public RingModulatorEffect createRingModulatorEffect()
	{
		return new RingModulatorEffect(this);
	}
	
	/**
	 * Creates a new Vocal Morpher effect.
	 * @return	a new effect of this type with default values set.
	 */
	public VocalMorpherEffect createVocalMorpherEffect()
	{
		return new VocalMorpherEffect(this);
	}
	
	/**
	 * Creates a new High Pass filter.
	 * @return	a new filter of this type with default values set.
	 */
	public HighPassFilter createHighPassFilter()
	{
		return new HighPassFilter(this);
	}
	
	/**
	 * Creates a new Low Pass filter.
	 * @return	a new filter of this type with default values set.
	 */
	public LowPassFilter createLowPassFilter()
	{
		return new LowPassFilter(this);
	}
	
	/**
	 * Creates a new Band Pass filter.
	 * @return	a new filter of this type with default values set.
	 */
	public BandPassFilter createBandPassFilter()
	{
		return new BandPassFilter(this);
	}
	
	/**
	 * Get the reference to this system's listener.
	 * @return		the Listener of this environment.
	 */
	public OALListener getListener()
	{
		return listener;
	}

	/**
	 * @return the name of the current OpenAL renderer (device). 
	 */
	public String getALRendererName()
	{
		return AL11.alGetString(AL11.AL_RENDERER);
	}
	
	/**
	 * @return OpenAL's version string. 
	 */
	public String getALVersionName()
	{
		return AL11.alGetString(AL11.AL_VERSION);
	}

	/**
	 * @return the name of the current OpenAL vendor. 
	 */
	public String getALVendorName()
	{
		return AL11.alGetString(AL11.AL_VENDOR);
	}
	
	/**
	 * @return the names of all available OpenAL extensions (newline-separated). 
	 */
	public String getALExtensions()
	{
		return AL11.alGetString(AL11.AL_EXTENSIONS);
	}
	
	/**
	 * Sets the sound environment's Doppler Factor.
	 * 0 = disabled.
	 * @param f the Doppler factor. 
	 */
	public void setDopplerFactor(float f)
	{
		AL11.alDopplerFactor(f);
		getError();
	}

	/**
	 * @return the sound environment's Doppler Factor.
	 */
	public float getDopplerFactor()
	{
		return AL11.alGetFloat(AL11.AL_DOPPLER_FACTOR);
	}

	/**
	 * Sets the sound environment's speed of sound factor.
	 * @param s the speed of sound. 
	 */
	public void setSpeedOfSound(float s)
	{
		AL11.alDopplerVelocity(s);
		getError();
	}

	/**
	 * @return the sound environment's speed of sound factor.
	 */
	public float getSpeedOfSound()
	{
		// AL_DOPPLER_VELOCITY - for some reason, not defined in AL11
		return AL11.alGetFloat(0xC001);
	}
	
	/**
	 * Sets the current context's distance model.
	 * By default, this is DistanceModel.INVERSE_DISTANCE_CLAMPED. 
	 * @param model the distance model to use.
	 */
	public void setDistanceModel(DistanceModel model)
	{
		AL11.alDistanceModel(model.alVal);
		getError();
		currentDistanceModel = model;
	}

	/**
	 * @return the current context's distance model.
	 */
	public DistanceModel getDistanceModel()
	{
		return currentDistanceModel;
	}

	/**
	 * Runs all Shut Down hooks, destroys all contexts and closes all open devices.
	 */
	public void shutDown()
	{
		getContextError();
		ALC11.alcMakeContextCurrent(0);
		getContextError();
		alcContext = null;
		ALC11.alcCloseDevice(alcDevice.getHandle());
		getContextError();
		alcDevice = null;
		
		for (OALObject object : createdObjects)
			object.destroy();
		createdObjects.clear();
		for (OALHandle object : createdHandles)
			object.destroy();
		createdHandles.clear();
	}

	@Override
	public void finalize() throws Throwable
	{
		shutDown();
		super.finalize();
	}
	
}


