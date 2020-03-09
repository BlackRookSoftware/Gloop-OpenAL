/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.io.IOException;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC11;
import org.lwjgl.openal.ALCapabilities;

import com.blackrook.gloop.openal.OALSystem.ContextLock;
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
 * A context handle in OpenAL. 
 * Represents the rendering context.
 * @author Matthew Tropiano
 */
public class OALContext extends OALHandle
{
	/**
	 * Distance Model enumeration for internal OpenAL distance models for attenuating
	 * the final gain of a Source in relation to the position/direction of the listener.
	 */
	public enum DistanceModel
	{
		NONE(AL11.AL_NONE),
		INVERSE_DISTANCE(AL11.AL_INVERSE_DISTANCE),
		INVERSE_DISTANCE_CLAMPED(AL11.AL_INVERSE_DISTANCE_CLAMPED),
		LINEAR_DISTANCE(AL11.AL_LINEAR_DISTANCE),
		LINEAR_DISTANCE_CLAMPED(AL11.AL_LINEAR_DISTANCE_CLAMPED),
		EXPONENT_DISTANCE(AL11.AL_EXPONENT_DISTANCE),
		EXPONENT_DISTANCE_CLAMPED(AL11.AL_EXPONENT_DISTANCE_CLAMPED);
		
		public final int alVal;
		
		private DistanceModel(int val) 
		{alVal = val;}
	}
	
	/**
	 * Enumeration of Context creation attributes.
	 */
	public enum ContextAttribute
	{
		FREQUENCY(ALC11.ALC_FREQUENCY),
		REFRESH(ALC11.ALC_REFRESH),
		SYNC(ALC11.ALC_SYNC),
		MONO_SOURCES(ALC11.ALC_MONO_SOURCES),
		STEREO_SOURCES(ALC11.ALC_STEREO_SOURCES);
		
		public final int alVal;
		
		private ContextAttribute(int val) 
		{alVal = val;}

	}
	
	/**
	 * A single pair of attribute-value.
	 */
	public static class AttributeValue
	{
		private ContextAttribute attribute;
		private int value;
		private AttributeValue(ContextAttribute attribute, int value)
		{
			this.attribute = attribute;
			this.value = value;
		}

		/**
		 * Creates a context attribute.
		 * @param attribute the attribute type.
		 * @param value the attribute's value.
		 * @return the created attribute.
		 */
		public static AttributeValue create(ContextAttribute attribute, int value)
		{
			return new AttributeValue(attribute, value);
		}
	}
	
	/** The device that the context is derived from. */
	private OALDevice device;
	/** The attribute values used to create this context. */
	private Map<ContextAttribute, Integer> attributeMap;
	
	/** This object's handle. */
	private long handle;
	/** Was this object allocated? */
	private boolean allocated;

	/** Map of created sources. */
	private Map<Integer, OALSource> nameToSource;
	/** Map of created buffers. */
	private Map<Integer, OALBuffer> nameToBuffer;
	/** Map of created filters. */
	private Map<Integer, OALFilter> nameToFilter;
	/** Map of created effects. */
	private Map<Integer, OALEffect> nameToEffect;
	/** Map of created effect slots. */
	private Map<Integer, OALEffectSlot> nameToEffectSlot;
	
	/** AL vendor name. */
	private String vendorName;
	/** AL version name. */
	private String versionName;
	/** AL renderer name. */
	private String rendererName;
	/** AL extensions. */
	private Set<String> extensions;
	/** AL capabilities. */
	private ALCapabilities capabilities;

	/** Listener. */
	private OALListener listener;

	/** Maximum effect slots per source. */
	private int maxEffectSlots;
	/** Distance model. */
	private DistanceModel currentDistanceModel;
	
	OALContext(OALDevice device, AttributeValue ... attributes)
	{
		this.device = device;
		this.attributeMap = new HashMap<>(Math.max(attributes.length, 1), 1f);
		for (AttributeValue av : attributes)
			attributeMap.put(av.attribute, av.value);

		this.handle = allocate();
		this.allocated = true;

		this.nameToSource = new HashMap<>();
		this.nameToBuffer = new HashMap<>();
		this.nameToFilter = new HashMap<>();
		this.nameToEffect = new HashMap<>();
		this.nameToEffectSlot = new HashMap<>();

		this.vendorName = null;
		this.versionName = null;
		this.rendererName = null;
		this.extensions = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
		this.listener = null;
		this.capabilities = null;
		
	}

	/**
	 * Adds an object's reference as a managed object.
	 * @param object the object to add.
	 */
	void registerObject(OALObject object)
	{
		if (object instanceof OALBuffer)
			registerObjectOnMap((OALBuffer)object, nameToBuffer);
		else if (object instanceof OALSource)
			registerObjectOnMap((OALSource)object, nameToSource);
		else if (object instanceof OALEffect)
			registerObjectOnMap((OALEffect)object, nameToEffect);
		else if (object instanceof OALEffectSlot)
			registerObjectOnMap((OALEffectSlot)object, nameToEffectSlot);
		else if (object instanceof OALFilter)
			registerObjectOnMap((OALFilter)object, nameToFilter);
		else
			throw new SoundSystemException("Unknown object type.");
	}
	
	/**
	 * Removes an object's reference as a managed object.
	 * @param object the object to add.
	 */
	void unregisterObject(OALObject object)
	{
		if (object instanceof OALBuffer)
			unregisterObjectOnMap((OALBuffer)object, nameToBuffer);
		else if (object instanceof OALSource)
			unregisterObjectOnMap((OALSource)object, nameToSource);
		else if (object instanceof OALEffect)
			unregisterObjectOnMap((OALEffect)object, nameToEffect);
		else if (object instanceof OALEffectSlot)
			unregisterObjectOnMap((OALEffectSlot)object, nameToEffectSlot);
		else if (object instanceof OALFilter)
			unregisterObjectOnMap((OALFilter)object, nameToFilter);
		else
			throw new SoundSystemException("Unknown object type.");
	}
	
	private <O extends OALObject> void registerObjectOnMap(O obj, final Map<Integer, O> map)
	{
		synchronized (map) 
		{
			map.put(obj.getName(), obj);
		}
	}

	private <O extends OALObject> void unregisterObjectOnMap(O obj, final Map<Integer, O> map)
	{
		synchronized (map) 
		{
			map.remove(obj.getName());
		}
	}

	private <O extends OALObject> void destroyObjectsOnMap(final Map<Integer, O> map)
	{
		synchronized (map) 
		{
			// need to copy set contents - deleting these objects will affect the set as we iterate.
			OALObject[] toDelete = new OALObject[map.size()];
			map.values().toArray(toDelete);
			for (int i = 0; i < toDelete.length; i++)
				toDelete[i].destroy();
			map.clear();
		}
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

	protected long allocate() throws SoundException 
	{
		long out;
		if (attributeMap.isEmpty())
		{
			out = ALC11.alcCreateContext(device.getHandle(), (IntBuffer)null);
		}
		else
		{
			int[] attribs = new int[attributeMap.size() * 2];
			int i = 0;
			for (Map.Entry<ContextAttribute, Integer> entry : attributeMap.entrySet())
			{
				attribs[i + 0] = entry.getKey().alVal;
				attribs[i + 1] = entry.getValue();
			}
			out = ALC11.alcCreateContext(device.getHandle(), attribs);
		}
		return out;
	}

	@Override
	public void destroy() throws SoundException 
	{
		if (allocated)
		{
			suspend();
			// sources must be first. sources are connected to everything else in a context.
			destroyObjectsOnMap(nameToSource);
			destroyObjectsOnMap(nameToBuffer);
			destroyObjectsOnMap(nameToEffect);
			destroyObjectsOnMap(nameToEffectSlot);
			destroyObjectsOnMap(nameToFilter);
			ALC11.alcDestroyContext(getHandle());
			handle = 0L;
			allocated = false;
		}
	}

	void setCapabilities(ALCapabilities capabilities) 
	{
		this.capabilities = capabilities;
	}

	void setVendorName(String vendorName)
	{
		this.vendorName = vendorName;
	}

	void setVersionName(String versionName) 
	{
		this.versionName = versionName;
	}

	void setRendererName(String rendererName) 
	{
		this.rendererName = rendererName;
	}

	void setExtensions(String ... extensions) 
	{
		this.extensions.clear();
		for (String e : extensions)
			this.extensions.add(e);
	}

	void setMaxEffectSlots(int maxEffectSlots) 
	{
		this.maxEffectSlots = maxEffectSlots;
	}

	void setListener(OALListener listener)
	{
		this.listener = listener;
	}
	
	/**
	 * Sets a new context as current.
	 * @param context the context to make current, or null for no context.
	 */
	ContextLock setCurrentContext()
	{
		return device.setCurrentContext(this);
	}
	
	/**
	 * Returns the corresponding value of a context attribute used to create this context.
	 * @param attribute the attribute.
	 * @return the value used, or null if the default was used.
	 */
	public Integer getAttributeValue(ContextAttribute attribute)
	{
		return attributeMap.get(attribute);
	}

	/**
	 * @return the vendor name for this OpenAL implementation.
	 */
	public String getVendorName() 
	{
		return vendorName;
	}
	
	/**
	 * @return the version name for this OpenAL implementation.
	 */
	public String getVersionName() 
	{
		return versionName;
	}
	
	/**
	 * @return the renderer name for this OpenAL implementation.
	 */
	public String getRendererName()
	{
		return rendererName;
	}
	
	/**
	 * @return the set of extensions.
	 */
	public Set<String> getExtensions() 
	{
		return extensions;
	}
	
	/**
	 * Checks if an extension is present for this context. Case-insensitive.
	 * @param extensionName the name of the extension.
	 * @return true if so, false if not.
	 */
	public boolean extensionIsPresent(String extensionName)
	{
		return extensions.contains(extensionName);
	}

	/**
	 * @return the max effect slots allowed per source.
	 */
	public int getMaxEffectSlots() 
	{
		return maxEffectSlots;
	}
	
	public void setCurrentDistanceModel(DistanceModel currentDistanceModel) 
	{
		this.currentDistanceModel = currentDistanceModel;
	}

	/**
	 * @return this device's current distance model.
	 */
	public DistanceModel getCurrentDistanceModel() 
	{
		return currentDistanceModel;
	}
	
	/**
	 * @return this context's listener.
	 */
	public OALListener getListener() 
	{
		return listener;
	}

	/**
	 * @return the capabilities that this context was created with.
	 */
	public ALCapabilities getCapabilities() 
	{
		return capabilities;
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	public void getError()
	{
		int error = AL11.alGetError();
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("OpenAL returned \"" + AL11.alGetString(error) + "\".");
	}

	/**
	 * Suspends processing of this context.
	 */
	public void suspend()
	{
		ALC11.alcSuspendContext(getHandle());
		device.getContextError();
	}

	/**
	 * Resumes processing of this context.
	 */
	public void process()
	{
		ALC11.alcProcessContext(getHandle());
		device.getContextError();
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
	 * Sets the sound environment's Doppler Factor.
	 * 0 = disabled.
	 * @param f the Doppler factor. 
	 */
	public void setDopplerFactor(float f)
	{
		try (ContextLock lock = setCurrentContext()) {
			AL11.alDopplerFactor(f);
			getError();
		}
	}

	/**
	 * @return the sound environment's Doppler Factor.
	 */
	public float getDopplerFactor()
	{
		float out;
		try (ContextLock lock = setCurrentContext()) {
			out = AL11.alGetFloat(AL11.AL_DOPPLER_FACTOR);
		}
		return out;
	}

	/**
	 * Sets the sound environment's speed of sound factor.
	 * @param s the speed of sound. 
	 */
	public void setSpeedOfSound(float s)
	{
		try (ContextLock lock = setCurrentContext()) {
			AL11.alDopplerVelocity(s);
			getError();
		}
	}

	/**
	 * @return the sound environment's speed of sound factor.
	 */
	public float getSpeedOfSound()
	{
		float out;
		try (ContextLock lock = setCurrentContext()) {
			// AL_DOPPLER_VELOCITY - for some reason, not defined in AL11
			out = AL11.alGetFloat(0xC001);
		}
		return out;
	}

	/**
	 * Sets the current context's distance model.
	 * By default, this is DistanceModel.INVERSE_DISTANCE_CLAMPED. 
	 * @param model the distance model to use.
	 */
	public void setDistanceModel(DistanceModel model)
	{
		try (ContextLock lock = setCurrentContext()) {
			AL11.alDistanceModel(model.alVal);
			getError();
		}
		currentDistanceModel = model;
	}

	/**
	 * @return the current context's distance model.
	 */
	public DistanceModel getDistanceModel()
	{
		return currentDistanceModel;
	}
	
}
