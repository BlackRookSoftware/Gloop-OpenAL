package com.blackrook.gloop.openal.system;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.blackrook.gloop.openal.JSPISoundHandle;
import com.blackrook.gloop.openal.OALBuffer;
import com.blackrook.gloop.openal.OALContext;
import com.blackrook.gloop.openal.OALDevice;
import com.blackrook.gloop.openal.OALEffectSlot;
import com.blackrook.gloop.openal.OALSource;
import com.blackrook.gloop.openal.OALSystem;
import com.blackrook.gloop.openal.effect.EchoEffect;
import com.blackrook.gloop.openal.effect.ReverbEffect;
import com.blackrook.gloop.openal.filter.BandPassFilter;
import com.blackrook.gloop.openal.struct.IOUtils;
import com.blackrook.gloop.openal.struct.MathUtils;
import com.blackrook.gloop.openal.struct.RandomUtils;
import com.blackrook.gloop.openal.struct.ThreadUtils;


/**
 * The main sound system.
 * This is a high-level implementation of OpenAL which performs a lot of
 * out-of-the-box conveniences for sound playback.
 * @author Matthew Tropiano
 */
public class SoundSystem
{
	private static final ThreadLocal<UpdateCache> UPDATECACHE = ThreadLocal.withInitial(() -> new UpdateCache());

	private static final SoundRolloffType DEFAULT_ROLLOFF = new SoundRolloffType()
	{
		@Override
		public SoundRolloffFunction getRolloffFunction()
		{
			return SoundRolloffFunction.NONE;
		}
		
		@Override
		public float getMinimumDistance()
		{
			return 0;
		}
		
		@Override
		public float getMaximumDistance()
		{
			return 0;
		}
	};

	
	private OALSystem system;
	private OALContext context;
	
	private SoundCache cache;
	
	private Deque<OALSource> availableSources;
	private Deque<OALSource> usedSources;
	private Deque<BandPassFilter> availableFilters;
	private Deque<BandPassFilter> usedFilters;

	private OALEffectSlot echoEffectSlot;
	private OALEffectSlot reverbEffectSlot;

	private EchoEffect echoEffect;
	private ReverbEffect reverbEffect;

	private String vendorName;
	private String versionName;
	private String rendererName;
	private Set<String> extensions;
	
	private List<Listener> listeners;
	
	// ======================================================================
	
	private Random random;
	private SoundLocation observer;
	
	private Map<SoundData, SoundStream> primedStreams;
	private Deque<Event> eventQueue;
	private Deque<Event> processDelay;

	private Deque<Voice> availableVoices;
	private Deque<Voice> deadVoices;
	private Deque<Voice> usedVoices;
	
	private Map<SoundData, Deque<Voice>> soundToVoicesMap;
	private Map<SoundGroupType, Deque<Voice>> groupToVoicesMap;
	private Map<SoundLocation, Deque<Voice>> sourceToVoicesMap;

	/** Deque of active streams. */
	private Deque<Voice> activeStreams;
	/** Active stream updater thread. */
	private Streamer streamer;

	private SoundScapeType soundScape;
	private OcclusionFunction occlusionFunction;
	
	// ======================================================================

	/**
	 * Creates and initializes a new sound system.
	 */
	public SoundSystem()
	{
		this(48, 16 * 1024 * 1024);
	}
	
	/**
	 * Creates and initializes a new sound system.
	 * @param voices the total amount of voices to allocate.
	 */
	public SoundSystem(int voices)
	{
		this(voices, 16 * 1024 * 1024);
	}
	
	/**
	 * Creates and initializes a new sound system.
	 * @param voices the total amount of voices to allocate.
	 * @param cacheSize the cache size for the sound clip cache.
	 */
	public SoundSystem(int voices, int cacheSize)
	{
		this.system = new OALSystem();
		OALDevice device = system.createDevice();
		this.context = device.createContext();

		this.cache = new SoundCache(cacheSize);
		
		this.availableSources = new LinkedList<>();
		this.usedSources = new LinkedList<>();
		
		// Make sources.
		while (availableSources.size() < voices)
			availableSources.add(context.createSource());

		this.availableFilters = new LinkedList<>();
		this.usedFilters = new LinkedList<>();
		
		// Make filters.
		while (availableFilters.size() < voices)
			availableFilters.add(context.createBandPassFilter());
		
		// Make shared effects.
		
		this.echoEffectSlot = context.createEffectSlot();
		this.reverbEffectSlot = context.createEffectSlot();
		
		this.echoEffect = context.createEchoEffect();
		this.reverbEffect = context.createReverbEffect();
		
		this.echoEffectSlot.setAutoUpdating(true);
		this.echoEffectSlot.setEffect(echoEffect);
		this.reverbEffectSlot.setAutoUpdating(true);
		this.reverbEffectSlot.setEffect(reverbEffect);
		
		this.vendorName = context.getVendorName();
		this.versionName = context.getVersionName();
		this.rendererName = context.getRendererName();
		this.extensions = context.getExtensions();
		
		this.listeners = new LinkedList<>();
		
		this.random = new Random();
		this.observer = new Location(0f, 0f, 0f, 0f);
		
		this.primedStreams = new HashMap<>();
		this.eventQueue = new LinkedList<>();
		this.processDelay = new LinkedList<>();
		
		this.availableVoices = new LinkedList<>();
		this.deadVoices = new LinkedList<>();
		this.usedVoices = new LinkedList<>();

		while (availableVoices.size() < voices)
			availableVoices.add(new Voice());

		this.soundToVoicesMap = new HashMap<>();
		this.groupToVoicesMap = new HashMap<>();
		this.sourceToVoicesMap = new HashMap<>();
		
		this.soundScape = null;
		this.occlusionFunction = null;
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param limit the concurrent play limit until a cull or replace.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, int limit)
	{
		return fileData(file, false, false, false, limit, 0f);
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, boolean stream, int limit)
	{
		return fileData(file, stream, false, false, limit, 0f);
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, int limit, float pitchVariance)
	{
		return fileData(file, false, false, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, boolean stream, int limit, float pitchVariance)
	{
		return fileData(file, stream, false, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param replacesOldSounds if true, hitting the limit will replace a sound, not cull it.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, boolean stream, boolean replacesOldSounds, int limit, float pitchVariance)
	{
		return fileData(file, stream, replacesOldSounds, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param replacesOldSounds if true, hitting the limit will replace a sound, not cull it.
	 * @param alwaysPlayed if true, must play once it is able to.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData fileData(File file, boolean stream, boolean replacesOldSounds, boolean alwaysPlayed, int limit, float pitchVariance)
	{
		return new FileData(file, stream, replacesOldSounds, alwaysPlayed, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, int limit)
	{
		return resourceData(resourcePath, false, false, false, limit, 0f);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, boolean stream, int limit)
	{
		return resourceData(resourcePath, stream, false, false, limit, 0f);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, int limit, float pitchVariance)
	{
		return resourceData(resourcePath, false, false, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, boolean stream, int limit, float pitchVariance)
	{
		return resourceData(resourcePath, stream, false, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param replacesOldSounds if true, hitting the limit will replace a sound, not cull it.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, boolean stream, boolean replacesOldSounds, int limit, float pitchVariance)
	{
		return resourceData(resourcePath, stream, replacesOldSounds, false, limit, pitchVariance);
	}

	/**
	 * Creates a new sound data type.
	 * @param resourcePath the path to the internal resource.
	 * @param stream if true, the data is streamed, not loaded fully.
	 * @param replacesOldSounds if true, hitting the limit will replace a sound, not cull it.
	 * @param alwaysPlayed if true, must play once it is able to.
	 * @param limit the concurrent play limit until a cull or replace.
	 * @param pitchVariance the pitch variance scalar.
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath, boolean stream, boolean replacesOldSounds, boolean alwaysPlayed, int limit, float pitchVariance)
	{
		return new ResourceData(resourcePath, stream, replacesOldSounds, alwaysPlayed, limit, pitchVariance);
	}

	/**
	 * Creates a new rolloff category type.
	 * @param rolloff the main rolloff. 
	 * @return a new category.
	 */
	public static SoundCategoryType category(SoundRolloffType rolloff)
	{
		return new Category(rolloff, null, null, null);
	}

	/**
	 * Creates a new rolloff category type.
	 * @param rolloff the main rolloff. 
	 * @param rolloffConic the conic gain rolloff.
	 * @return a new category.
	 */
	public static SoundCategoryType category(SoundRolloffType rolloff, SoundRolloffType rolloffConic)
	{
		return new Category(rolloff, null, null, rolloffConic);
	}

	/**
	 * Creates a new rolloff category type.
	 * @param rolloff the main rolloff. 
	 * @param rolloffLF the low-pass gain rolloff.
	 * @param rolloffHF the high-pass gain rolloff.
	 * @param rolloffConic the conic gain rolloff.
	 * @return a new category.
	 */
	public static SoundCategoryType category(SoundRolloffType rolloff, SoundRolloffType rolloffLF, SoundRolloffType rolloffHF, SoundRolloffType rolloffConic)
	{
		return new Category(rolloff, rolloffLF, rolloffHF, rolloffConic);
	}
	
	/**
	 * Creates a new rolloff type instance.
	 * @param minDistance the minimum distance that attenuation starts occurring.
	 * @param maxDistance the maximum distance that full attenuation happens.
	 * @param rolloffFunction the rolloff function for the distance betweeen min and max.
	 * @return a new rolloff.
	 */
	public static SoundRolloffType rolloff(float minDistance, float maxDistance, SoundRolloffFunction rolloffFunction)
	{
		return new Rolloff(minDistance, maxDistance, rolloffFunction);
	}
	
	/**
	 * Creates a new set of occlusion parameters.
	 * @param maximumWidth the maximum occlusion width for max occlusion.
	 * @param gain the full gain attenuation.
	 * @param lowPassGain the full low-pass gain attenuation. 
	 * @param highPassGain the full high-pass gain attenuation. 
	 * @return a new occlusion.
	 */
	public static SoundOcclusionType occlusion(float maximumWidth, float gain, float lowPassGain, float highPassGain)
	{
		return new Occlusion(maximumWidth, gain, lowPassGain, highPassGain);
	}

	/**
	 * Creates a new location instance.
	 * @param x the location x-coordinate, in world units.
	 * @param y the location y-coordinate, in world units.
	 * @param z the location z-coordinate, in world units.
	 * @param angle the location angle, in degrees.
	 * @return a new location.
	 */
	public static SoundLocation location(float x, float y, float z, float angle)
	{
		return new Location(x, y, z, angle);
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
	 * @return the set of extensions for this OpenAL implementation.
	 */
	public Set<String> getExtensions() 
	{
		return extensions;
	}

	/**
	 * Adds a listener to this sound system.
	 * @param listener the listener to add.
	 */
	public void addListener(Listener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 * Adds a listener to this sound system.
	 * @param listener the listener to add.
	 */
	public void removeListener(Listener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 * Sets the observer location.
	 * @param location the observer's location reference.
	 */
	public void setObserver(SoundLocation location)
	{
		this.observer = location;
	}
	
	/**
	 * Sets the soundscape to use.
	 * @param soundScape the new soundscape. Can be null.
	 */
	public void setSoundScape(SoundScapeType soundScape)
	{
		this.soundScape = soundScape;
	}

	/**
	 * Sets the occlusion function to use to calculate occlusion.
	 * @param occlusionFunction the function.
	 */
	public void setOcclusionFunction(OcclusionFunction occlusionFunction)
	{
		this.occlusionFunction = occlusionFunction;
	}

	/**
	 * Precaches a series of sound resources. Will NOT cache sounds
	 * if they designated as not cacheable or if they are streaming: instead,
	 * they are "primed" - which means that it is prebuffered and ready to be played later.
	 * @param resources	the list of resources to cache.
	 */
	public void cacheSounds(SoundData ... resources)
	{
		for (SoundData resource : resources)
		{			
			// streams are "primed," not cached - they are still disposable.
			if (resource.isStream())
			{
				try {
					JSPISoundHandle handle = openSoundHandle(resource);
					SoundStream ss = new SoundStream(handle);
					primedStreams.put(resource, ss);
				} catch (UnsupportedAudioFileException e) {
					listeners.forEach((listener) -> listener.onSoundUnsupportedError(resource, e));
				} catch (IOException e) {
					listeners.forEach((listener) -> listener.onSoundIOError(resource, e));
				}
			}
			else
			{
				OALBuffer buf = null; 
				JSPISoundHandle handle = null;
				try {
					handle = openSoundHandle(resource);
					if ((buf = cache.getBuffer(resource)) == null)
					{	
						buf = context.createBuffer(handle);
						cache.addBuffer(resource, buf);
						listeners.forEach((listener) -> listener.onSoundCached(resource));
					}
				} catch (UnsupportedAudioFileException e) {
					listeners.forEach((listener) -> listener.onSoundUnsupportedError(resource, e));
				} catch (IOException e) {
					listeners.forEach((listener) -> listener.onSoundIOError(resource, e));
				} 
			}
		}
	}

	/**
	 * Plays a sound. No virtual channel, location, nor rolloff.
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 */
	public void play(SoundData data, SoundGroupType group)
	{
		play(data, group, null, null, null);
	}
	
	/**
	 * Plays a sound. No virtual channel, nor rolloff.
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param location the sound location in the world.
	 */
	public void play(SoundData data, SoundGroupType group, SoundLocation location)
	{
		play(data, group, null, location, null);
	}
	
	/**
	 * Plays a sound. No virtual channel.
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param category the sound rolloff category.
	 * @param location the sound location in the world.
	 */
	public void play(SoundData data, SoundGroupType group, SoundCategoryType category, SoundLocation location)
	{
		play(data, group, category, location, null);
	}
	
	/**
	 * Plays a sound.
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param category the sound rolloff category.
	 * @param location the sound location in the world.
	 * @param channel the virtual channel on the location.
	 */
	public void play(SoundData data, SoundGroupType group, SoundCategoryType category, SoundLocation location, Integer channel)
	{
		// TODO Finish this.
	}
	
	/**
	 * Plays a looping sound.
	 * Be careful - you need to be able to stop this sound!
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param category the sound rolloff category.
	 * @param location the sound location in the world.
	 * @param channel the virtual channel on the location.
	 */
	public void playLooping(SoundData data, SoundGroupType group, SoundCategoryType category, SoundLocation location, Integer channel)
	{
		// TODO Finish this.
	}
	
	/**
	 * Finds an unused or suitable voice for an incoming sound to play.
	 * Does basic checks for virtual channel availability and may stop other sounds in order to allocate a voice.
	 * @return a voice, or null if no available voice.
	 */
	private Voice allocateVoice(Event event)
	{
		SoundData data = event.data; 
		SoundGroupType group = event.group;
		SoundCategoryType category = event.category; 
		SoundLocation location = event.location;
		Integer channel = event.channel;
		
		Voice out = null;
	
		// actor clear?
		if (location != null && channel != null)
		{
			Voice voice = null;
			Deque<Voice> voiceList = sourceToVoicesMap.get(location);
			if (voiceList != null) for (Voice v : voiceList)
			{
				if (v.channel == channel)
				{
					voice = v;
					break;
				}
			}
			if (voice != null)
				deallocateVoice(voice);
		}
		
		// group clear?
		if (group != null && group.getMaximumVoices() > 0)
		{
			Deque<Voice> voiceList = groupToVoicesMap.get(group);
			if (voiceList != null && voiceList.size() >= group.getMaximumVoices())
				deallocateVoice(voiceList.pollFirst());
		}
		
		// sound clear?
		if (data.getLimit() > 0)
		{
			Deque<Voice> voiceList = soundToVoicesMap.get(data);
			if (voiceList != null && voiceList.size() >= data.getLimit())
				deallocateVoice(voiceList.pollFirst());
		}
		
		if (!availableVoices.isEmpty())
			out = availableVoices.pollFirst();
		
		if (out == null)
			return null;

		try {
			
			prepareVoice(out, data);
			
			// sound stream set already, if any.
			
			out.group = group;
			out.location = location;
			out.category = category;
			out.initGain = event.initGain;
			out.initPitch = event.initPitch;

			if (updateVoice(out))
			{
				registerVoice(out);
				usedVoices.add(out);
				return out;
			}
			else
			{
				deallocateVoice(out);
				return null;
			}

		} catch (UnsupportedAudioFileException e) {
			listeners.forEach((listener) -> listener.onSoundUnsupportedError(data, e));
			availableVoices.add(out);
			return null;
		} catch (IOException e) {
			listeners.forEach((listener) -> listener.onSoundIOError(data, e));
			availableVoices.add(out);
			return null;
		}
		
	}

	/**
	 * Prepares a voice/source for playback and sets its characteristics.
	 * @param voice the voice to set up.
	 * @param sound the sound resource to load, or retrieve if already in memory.
	 * @throws UnsupportedAudioFileException if the audio file type is not supported.
	 * @throws IOException if the resource couldn't be read.
	 * @throws SoundException if a Buffer can't be allocated.
	 */
	private void prepareVoice(final Voice voice, final SoundData sound) throws UnsupportedAudioFileException, IOException
	{
		// it's a stream
		if (sound.isStream())
		{
			SoundStream ss = primedStreams.get(sound);
			if (ss != null)
				primedStreams.remove(sound);
			else
			{
				JSPISoundHandle handle = openSoundHandle(sound);
				if (handle != null)
					ss = new SoundStream(handle);
			}

			OALSource source = voice.source;
			source.enqueueBuffers(ss.buffers);
			voice.stream = ss;
			
			synchronized (activeStreams)
			{
				activeStreams.add(voice);
				listeners.forEach((listener) -> listener.onVoiceStreamStarted(voice));
			}
			
			startStreamer();
			
			listeners.forEach((listener) -> listener.onStreamThreadStarted());
		}
		// not a stream
		else
		{
			OALBuffer buf = null;
			if ((buf = cache.getBuffer(sound)) == null)
			{
				cacheSounds(sound);
				buf = cache.getBuffer(sound);
			}
			
			// attach buffer.
			OALSource source = voice.source;
			source.setBuffer(buf);
		}
		
		voice.data = sound;

		// Set up filters/effects.
		BandPassFilter filter = availableFilters.pollFirst();
		voice.source.setFilter(filter);
		voice.source.setEffectSlot(0, echoEffectSlot);
		voice.source.setEffectSlot(1, reverbEffectSlot);
		
		listeners.forEach((listener) -> listener.onVoicePrepared(voice));
	}
	
	/**
	 * Deallocates a previously allocated voice.
	 * @param voice the voice to deallocate.
	 */
	private void deallocateVoice(Voice voice)
	{
		voice.source.stop();
		voice.source.setEffectSlot(1, null);
		voice.source.setEffectSlot(0, null);
		voice.source.setFilter(null);
		voice.source.setBuffer(null);
		voice.reset();
		
		// TODO: Finish this.
	}
	
	/**
	 * Registers an already-allocated voice in the system.
	 * @param voice the voice to register.
	 */
	private void registerVoice(Voice voice)
	{
		// TODO: Finish this.
		// Add voice to maps.
	}
	
	/**
	 * Deregisters an allocated voice in the system.
	 * Happens before deallocation.
	 * @param voice the voice to deregister.
	 */
	private void deregisterVoice(Voice voice)
	{
		// TODO: Finish this.
		// Remove voice from maps.
	}
	
	/**
	 * Updates a voice (attenuation and source position).
	 * @param voice the voice to update.
	 * @return if false, cull this voice. if true, don't.
	 */
	private boolean updateVoice(Voice voice)
	{
		UpdateCache update = UPDATECACHE.get();
		
		referenceValues(voice, update);
		sourceValues(voice, update);
		
		if (update.gain > 0.0f)
		{
			OALSource source = voice.source;
			BandPassFilter filter = voice.filter;

			source.setPosition(update.sourceX, update.sourceY, update.sourceZ);
			source.setGain(update.gain);
			source.setPitch(update.pitch);
			
			filter.setGain(1.0f);
			filter.setHFGain(update.gainHF);
			filter.setLFGain(update.gainLF);
			source.setFilter(filter); // force update
			return true;
		}
		else
		{
			stopVoice(voice);
			return false;
		}
		
	}
	
	/**
	 * Calculates the reference position for a voice relative to the observer (camera).
	 * @param voice the input voice.
	 */
	private void referenceValues(Voice voice, UpdateCache update)
	{
		SoundLocation location;
		
		float positionX = 0f;
		float positionY = 0f;
		float positionZ = 0f;
		
		// actor source
		if ((location = voice.location) != null)
		{
			if (observer == location)
			{
				update.distance = 0.0f; 
				update.coneAngle = 0.0f; 
				update.observerAngle = 0.0f;
				update.occlusion = 0.0f;
			}
			else
			{
				float sourceX = location.getSoundPositionX();
				float sourceY = location.getSoundPositionY();
				float sourceZ = location.getSoundPositionZ();
				float cameraX = observer.getSoundPositionX();
				float cameraY = observer.getSoundPositionY();
				float cameraZ = observer.getSoundPositionZ();
			
				positionX = sourceX - cameraX;
				positionY = sourceY - cameraY;
				positionZ = sourceZ - cameraZ;
	
				double sourceToCamera = MathUtils.getVectorAngleDegrees(-positionX, -positionY);
				double cameraToSource = MathUtils.getVectorAngleDegrees(positionX, positionY);
				update.distance = (float)MathUtils.getVectorLength(positionX, positionY, positionZ); 
				update.observerAngle = (float)MathUtils.getRelativeAngleDegrees(observer.getSoundAngle(), cameraToSource);
				update.coneAngle = (float)Math.abs(MathUtils.getRelativeAngleDegrees(location.getSoundAngle(), sourceToCamera));
				update.occlusion = occlusionFunction != null ? occlusionFunction.getOcclusionScalar(
					location.getSoundPositionX(),
					location.getSoundPositionY(),
					location.getSoundPositionZ(),
					observer.getSoundPositionX(),
					observer.getSoundPositionY(),
					observer.getSoundPositionZ()
				) : 0.0f; 
			}
		}
		// not positional, position is strict panning
		else if (voice.group.isTwoDimensional()) 
		{
			positionX = voice.location.getSoundPositionX();
			positionY = 0.0f;
			positionZ = 0.0f;
			
			update.distance = 0.0f; 
			update.coneAngle = 0.0f; 
			update.observerAngle = -(float)MathUtils.linearInterpolate((positionX + 1f) / 2f, -90, 90);
			update.occlusion = 0.0f;
		}
	}

	/**
	 * Calculates the data to set on the source.
	 * @param voice the input voice.
	 */
	private void sourceValues(Voice voice, UpdateCache update)
	{
		SoundData sound = voice.data;
		
		float voiceGain = voice.initGain * voice.group.getGain();
		float voicePitch = voice.initPitch * voice.group.getPitch();
		
		float soundGain = 1.0f;
		float soundPitch = 1.0f + (float)RandomUtils.randDouble(random, -sound.getPitchVariance(), sound.getPitchVariance());
	
		SoundRolloffType rolloff = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffLF = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffHF = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffConic = DEFAULT_ROLLOFF;

		if (voice.category.getRolloffType() != null)
			rolloff = voice.category.getRolloffType();
		if (voice.category.getLowPassRolloffType() != null)
			rolloffLF = voice.category.getLowPassRolloffType();
		if (voice.category.getHighPassRolloffType() != null)
			rolloffHF = voice.category.getHighPassRolloffType();
		if (voice.category.getConicRolloffType() != null)
			rolloffConic = voice.category.getConicRolloffType();

		float rolloffGain;
		float rolloffLFGain;
		float rolloffHFGain;
		float rolloffGainConic;
		
		float occlusionGain;
		float occlusionHFGain;
		float occlusionLFGain;
	
		if (rolloff != null)
		{
			rolloffGain = gainFactor(rolloff, update.distance);
			rolloffLFGain = gainFactor(rolloffLF, update.distance);
			rolloffHFGain = gainFactor(rolloffHF, update.distance);
			rolloffGainConic = gainFactor(rolloffConic, update.coneAngle);
		}
		else
		{
			rolloffGain = 1.0f;
			rolloffLFGain = 1.0f;
			rolloffHFGain = 1.0f;
			rolloffGainConic = 1.0f;
		}
	
		if (soundScape != null && soundScape.getOcclusion() != null)
		{
			occlusionGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getGain()));
			occlusionHFGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getHighPassGain()));
			occlusionLFGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getLowPassGain()));
		}
		else
		{
			occlusionGain = 1.0f;
			occlusionHFGain = 1.0f;
			occlusionLFGain = 1.0f;
		}
		
		float dopplerPitch = 1.0f;
	
		// OpenAL XYZ is Game XZY, like OpenGL
		double rad = MathUtils.degToRad(update.observerAngle);
		float rotatedX = (float)Math.sin(rad);
		float rotatedY = (float)Math.cos(rad);
		
		update.sourceX = rotatedX;
		update.sourceZ = rotatedY;
		update.sourceY = 0f;
		
		update.gain = voiceGain * soundGain * rolloffGain * rolloffGainConic * occlusionGain;
		update.pitch = voicePitch * soundPitch * dopplerPitch;
		update.gainHF = rolloffHFGain * occlusionHFGain;
		update.gainLF = rolloffLFGain * occlusionLFGain;
	}

	private static float gainFactor(SoundRolloffType rolloff, float distance)
	{
		float minDistance = rolloff.getMinimumDistance(); 
		float maxDistance = rolloff.getMaximumDistance();
		
		if (distance < minDistance)
			return 1.0f;
		else if (distance > maxDistance)
			return 0.0f;
		else if (distance == minDistance && minDistance == maxDistance)
			return 1.0f;
		else 
			return rolloff.getAttenuationScalar((distance - minDistance) / (maxDistance - minDistance));
	}
	
	/**
	 * Stops a voice.
	 * @param voice the voice to stop.
	 */
	private void stopVoice(Voice voice)
	{
		voice.source.stop();
		listeners.forEach((listener) -> listener.onVoiceStopped(voice));
	}
	
	private <T> void addVoiceToMap(T key, Voice voice, Map<T, List<Voice>> voiceMap)
	{
		List<Voice> voices;
		if ((voices = voiceMap.get(key)) == null)
			voiceMap.put(key, voices = new LinkedList<>());
		voices.add(voice);
	}

	private <T> void removeVoiceFromMap(T key, Voice voice, Map<T, List<Voice>> voiceMap)
	{
		List<Voice> voices;
		if ((voices = voiceMap.get(key)) != null)
		{
			voices.remove(voice);
			if (voices.isEmpty())
				voiceMap.remove(key);
		}
	}

	/**
	 * Creates a sound data object from a resource.
	 * @param sound the sound definition to get the path from.
	 */
	private JSPISoundHandle openSoundHandle(SoundData sound) throws UnsupportedAudioFileException, IOException
	{
		String path = sound.getPath();
		if (path.trim().length() == 0)
			throw new IOException("Resource does not have a path.");
			
		InputStream in = null;
		JSPISoundHandle out;
		try {
			in = sound.getInputStream();
			out = new JSPISoundHandle(sound.getPath(), IOUtils.getBinaryContents(in));
		} finally {
			IOUtils.close(in);
		}
		return out;
	}

	/** 
	 * Starts the streamer thread if it is not active. 
	 */
	private void startStreamer()
	{
		if (streamer != null)
			return;
		streamer = new Streamer();
		streamer.start();
	}
	
	/** 
	 * Stops the streamer thread if it is active. 
	 */
	private void stopStreamer()
	{
		if (streamer == null)
			return;
		streamer.kill();
		streamer = null;
	}

	/**
	 * The function type to use for figuring out an occlusion scalar.
	 */
	public interface OcclusionFunction
	{
		/**
		 * Gets the occlusion scalar for a set of coordinates.
		 * @param soundX the sound source, X-coordinate.
		 * @param soundY the sound source, Y-coordinate.
		 * @param soundZ the sound source, Z-coordinate.
		 * @param observerX the observer source, X-coordinate.
		 * @param observerY the observer source, Y-coordinate.
		 * @param observerZ the observer source, Z-coordinate.
		 * @return the resultant scalar.
		 */
		float getOcclusionScalar(float soundX, float soundY, float soundZ, float observerX, float observerY, float observerZ);
	}
	
	/**
	 * 
	 */
	public interface Listener
	{
		void onVoiceRejected();
		void onVoicePrepared(Voice voice);
		void onVoicePlayed(Voice voice);
		void onVoiceStopped(Voice voice);
		void onVoiceStreamStarted(Voice voice);
		void onStreamThreadStarted();
		void onStreamThreadEnded();
		void onSoundCached(SoundData data);
		void onSoundIOError(SoundData data, IOException e);
		void onSoundUnsupportedError(SoundData data, UnsupportedAudioFileException e);
	}
	
	/**
	 * The streamer object made for each streaming voice.  
	 */
	private class SoundStream
	{
		protected OALBuffer[] buffers;
		protected JSPISoundHandle soundHandle;
		protected JSPISoundHandle.Decoder decoderRef;
	
		protected byte[] bytebuffer;
		
		SoundStream(JSPISoundHandle soundHandle) throws UnsupportedAudioFileException, IOException
		{
			restartDecoder(soundHandle);
			buffers = context.createBuffers(2);
			AudioFormat decoderFormat = decoderRef.getDecodedAudioFormat();
			for (OALBuffer b : buffers)
			{
				b.setSamplingRate((int)decoderFormat.getSampleRate());
				b.setFormatByChannelsAndBits(decoderFormat.getChannels(), decoderFormat.getSampleSizeInBits());
				// TODO: Make this approach better. I don't trust its memory efficiency.
				int len = decoderRef.readPCMBytes(bytebuffer);
				b.setData(ByteBuffer.wrap(bytebuffer, 0, len));
			}
		}
	
		/**
		 * Primes/restarts the decoder.
		 * @param soundHandle the sound definition to use.
		 * @throws UnsupportedAudioFileException if the audio file's format is not supported.
		 * @throws IOException if the stream cannot be read.
		 */
		public void restartDecoder(JSPISoundHandle soundHandle) throws UnsupportedAudioFileException, IOException
		{
			if (this.decoderRef != null)
			{
				decoderRef.close();
				decoderRef = null;
			}
			
			this.soundHandle = soundHandle;
			this.decoderRef = soundHandle.getDecoder();
			AudioFormat decoderFormat = decoderRef.getDecodedAudioFormat();
			if (bytebuffer == null)
			{
				int buffersize = (int)decoderFormat.getSampleRate() * decoderFormat.getChannels() * (decoderFormat.getSampleSizeInBits()/8);
				bytebuffer = new byte[buffersize];
			}
		}
		
		/**
		 * Updates the stream.
		 * @param voice the playing voice.
		 * @param source the source to update.
		 * @return the amount of bytes loaded.
		 * @throws UnsupportedAudioFileException if the audio file's format is not supported.
		 * @throws IOException if the stream cannot be read.
		 */
		public int streamUpdate(Voice voice, OALSource source) throws UnsupportedAudioFileException, IOException
		{
			int out = -1;
			int p = source.getProcessedBufferCount();
			while (p-- > 0 && out != 0)
			{
				OALBuffer b = source.dequeueBuffer();
				out = decoderRef.readPCMBytes(bytebuffer);
				if (out > 0)
				{
					b.setData(ByteBuffer.wrap(bytebuffer, 0, out));
					source.enqueueBuffer(b);
				}
				else if (out == 0 && voice.looping)
				{
					restartDecoder(soundHandle);
					out = decoderRef.readPCMBytes(bytebuffer);
					if (out > 0)
					{
						b.setData(ByteBuffer.wrap(bytebuffer, 0, out));
						source.enqueueBuffer(b);
					}
				}
			}
			return out;
		}
		
	}

	/**
	 * Streamer thread.
	 * Kept alive if streams need updating.
	 * This is to keep work off of the main update thread.
	 */
	private class Streamer extends Thread
	{
		private boolean killed;
		
		Streamer()
		{
			super();
			setName("SoundStreamerThread");
			setDaemon(true);
		}
		
		public void kill()
		{
			killed = true;
		}
		
		@Override
		public void run()
		{
			listeners.forEach((listener) -> listener.onStreamThreadStarted());
			while (!killed && !activeStreams.isEmpty())
			{
				synchronized (activeStreams)
				{
					if (!activeStreams.isEmpty())
					{
						Iterator<Voice> streamIterator = activeStreams.iterator();
						while (streamIterator.hasNext() && !killed)
						{
							Voice voice = streamIterator.next();
							OALSource source = voice.source;
							SoundStream stream = voice.stream;
							SoundData sound = voice.data;
							try {
								stream.streamUpdate(voice, source);
							} catch (UnsupportedAudioFileException e) {
								listeners.forEach((listener) -> listener.onSoundUnsupportedError(sound, e));
								stopVoice(voice);
							} catch (IOException e) {
								listeners.forEach((listener) -> listener.onSoundIOError(sound, e));
								stopVoice(voice);
							} 
							
						}
	
					}
				}
				ThreadUtils.sleep(100);
			}
			stopStreamer();
			listeners.forEach((listener) -> listener.onStreamThreadEnded());
		}
		
	}

	/**
	 * A resource data type.
	 */
	private static class ResourceData extends Data
	{
		private String resourcePath;

		private ResourceData(String resourcePath, boolean stream, boolean replacesOldSounds, boolean alwaysPlayed, int limit, float pitchVariance)
		{
			super(stream, replacesOldSounds, alwaysPlayed, limit, pitchVariance);
			this.resourcePath = resourcePath;
		}

		@Override
		public String getPath()
		{
			return resourcePath;
		}

		@Override
		public InputStream getInputStream()
		{
			return ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
		}
	}

	/**
	 * A file data type.
	 */
	private static class FileData extends Data
	{
		private File file;

		private FileData(File file, boolean stream, boolean replacesOldSounds, boolean alwaysPlayed, int limit, float pitchVariance)
		{
			super(stream, replacesOldSounds, alwaysPlayed, limit, pitchVariance);
			this.file = file;
		}

		@Override
		public String getPath()
		{
			return file.getPath();
		}

		@Override
		public InputStream getInputStream()
		{
			try {
				return new FileInputStream(file);
			} catch (FileNotFoundException e) {
				return null;
			}
		}
	}
	
	/**
	 * A data type.
	 */
	private abstract static class Data implements SoundData
	{
		private boolean stream; 
		private boolean replacesOldSounds; 
		private boolean alwaysPlayed; 
		private int limit;
		private float pitchVariance;
		
		protected Data(boolean stream, boolean replacesOldSounds, boolean alwaysPlayed, int limit, float pitchVariance)
		{
			this.stream = stream;
			this.replacesOldSounds = replacesOldSounds;
			this.alwaysPlayed = alwaysPlayed;
			this.limit = limit;
			this.pitchVariance = pitchVariance;
		}

		@Override
		public boolean isStream()
		{
			return stream;
		}

		@Override
		public boolean replacesOldSounds()
		{
			return replacesOldSounds;
		}

		@Override
		public boolean isAlwaysPlayed()
		{
			return alwaysPlayed;
		}

		@Override
		public int getLimit()
		{
			return limit;
		}

		@Override
		public float getPitchVariance()
		{
			return pitchVariance;
		}
	}
	
	/**
	 * A rolloff category type.
	 */
	private static class Category implements SoundCategoryType
	{
		private SoundRolloffType rolloff;
		private SoundRolloffType rolloffLF;
		private SoundRolloffType rolloffHF;
		private SoundRolloffType rolloffConic;
		
		private Category(SoundRolloffType rolloff, SoundRolloffType rolloffLF, SoundRolloffType rolloffHF, SoundRolloffType rolloffConic)
		{
			this.rolloff = rolloff;
			this.rolloffLF = rolloffLF;
			this.rolloffHF = rolloffHF;
			this.rolloffConic = rolloffConic;
		}

		@Override
		public SoundRolloffType getRolloffType()
		{
			return rolloff;
		}

		@Override
		public SoundRolloffType getLowPassRolloffType()
		{
			return rolloffLF;
		}

		@Override
		public SoundRolloffType getHighPassRolloffType()
		{
			return rolloffHF;
		}

		@Override
		public SoundRolloffType getConicRolloffType()
		{
			return rolloffConic;
		}
		
	}
	
	/**
	 * A rolloff type instance.
	 */
	private static class Rolloff implements SoundRolloffType
	{
		private float minDistance;
		private float maxDistance;
		private SoundRolloffFunction rolloffFunction;

		private Rolloff(float minDistance, float maxDistance, SoundRolloffFunction rolloffFunction)
		{
			this.minDistance = minDistance;
			this.maxDistance = maxDistance;
			this.rolloffFunction = rolloffFunction;
		}

		@Override
		public float getMinimumDistance()
		{
			return minDistance;
		}

		@Override
		public float getMaximumDistance()
		{
			return maxDistance;
		}

		@Override
		public SoundRolloffFunction getRolloffFunction()
		{
			return rolloffFunction;
		}
		
	}
	
	/**
	 * A set of occlusion parameters.
	 */
	private static class Occlusion implements SoundOcclusionType
	{
		private float maxWidth;
		private float gain;
		private float gainLF;
		private float gainHF;
		
		private Occlusion(float maxWidth, float gain, float gainLF, float gainHF)
		{
			super();
			this.maxWidth = maxWidth;
			this.gain = gain;
			this.gainLF = gainLF;
			this.gainHF = gainHF;
		}

		@Override
		public float getMaximumWidth()
		{
			return maxWidth;
		}

		@Override
		public float getGain()
		{
			return gain;
		}

		@Override
		public float getLowPassGain()
		{
			return gainLF;
		}

		@Override
		public float getHighPassGain()
		{
			return gainHF;
		}
	}
	
	/**
	 * A single place in 3D world space to play the sound from.
	 */
	private static class Location implements SoundLocation
	{
		private float x;
		private float y;
		private float z;
		private float angle;
		
		private Location(float x, float y, float z, float angle)
		{
			this.x = x;
			this.y = y;
			this.z = z;
			this.angle = angle;
		}
		
		@Override
		public float getSoundPositionX()
		{
			return x;
		}
	
		@Override
		public float getSoundPositionY()
		{
			return y;
		}
	
		@Override
		public float getSoundPositionZ()
		{
			return z;
		}

		@Override
		public float getSoundAngle()
		{
			return angle;
		}
		
	}

	/**
	 * An object used for updating voices and storing calculated values.
	 */
	private static class UpdateCache
	{
		private float distance;
		private float observerAngle;
		private float coneAngle;
		private float occlusion;
		
		private float sourceX;
		private float sourceY;
		private float sourceZ;
		private float gain;
		private float gainLF;
		private float gainHF;
		private float gainEffect;
		private float pitch;
	}

	private static class Event
	{
		enum Type
		{
			PLAY,
			PLAY_LOOP,
			STOP,
		}
		
		private Type type;
		private SoundData data;
		private SoundGroupType group; 
		private SoundCategoryType category;
		private SoundLocation location; 
		private Integer channel;
		private float initGain;
		private float initPitch;
	}

	private static class Voice
	{
		private OALSource source;
		private BandPassFilter filter;

		private SoundData data;
		private SoundCategoryType category;
		private SoundGroupType group;
		private SoundLocation location;
		private Integer channel;
		private long age;
		private float initGain;
		private float initPitch;

		private SoundStream stream;
		private boolean looping;

		private Voice()
		{
			reset();
		}
		
		private void reset()
		{
			this.source = null;
			this.filter = null;
			
			this.data = null;
			this.category = null;
			this.group = null;
			this.location = null;
			this.channel = null;
			this.age = -1L;
			this.initGain = 1.0f;
			this.initPitch = 1.0f;
		}
		
	}
	
}
