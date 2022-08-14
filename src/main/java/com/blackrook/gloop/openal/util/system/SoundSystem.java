package com.blackrook.gloop.openal.util.system;

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

import org.lwjgl.BufferUtils;

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
 * <p> If this class is used, do NOT use the OALSystem class for sound playback. 
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
	private Map<SoundLocation, Deque<Voice>> locationToVoicesMap;

	/** Active processor thread. */
	private ProcessorThread processor;

	private SoundScapeType soundScape;
	private OcclusionFunction occlusionFunction;

	// ======================================================================
	
	private long updateEventNanos;
	private long updateVoiceNanos;
	
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
		{
			Voice voice = new Voice(context);
			availableVoices.add(voice);
		}

		this.soundToVoicesMap = new HashMap<>();
		this.groupToVoicesMap = new HashMap<>();
		this.locationToVoicesMap = new HashMap<>();
		
		this.processor = new ProcessorThread();
		
		this.soundScape = null;
		this.occlusionFunction = null;

		this.processor.start();
	}

	/**
	 * Creates a new sound data type.
	 * @param file the file to load. 
	 * @return a new category.
	 */
	public static SoundData fileData(File file)
	{
		return fileData(file, false, false, false, 0, 0f);
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
	 * @return a new category.
	 */
	public static SoundData resourceData(String resourcePath)
	{
		return resourceData(resourcePath, false, false, false, 0, 0f);
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
	 * Creates a new SoundScape.
	 * @param echoType the echo type.
	 * @param reverbType the reverb type.
	 * @param occlusionType the occlusion type.
	 * @param minEffectGain the minimum effect gain at distance 0 (gain is 1.0 at max distance).
	 * @param maxEffectDistance the distance in world units at which the effect gain is 1.0.
	 * @return a new soundscape.
	 */
	public static SoundScape soundScape(SoundEchoType echoType, SoundReverbType reverbType, SoundOcclusionType occlusionType, float minEffectGain, float maxEffectDistance)
	{
		return new SoundScape(echoType, reverbType, occlusionType, minEffectGain, maxEffectDistance);
	}
	
	/**
	 * Creates a new group type with defaults set, no parent.
	 * @param occludable if true, the sounds played from the this group are occludable.
	 * @param twoDimensional if true, the sounds played from the this group are panned, not spacialized.
	 * @param zeroPosition if true, the sounds played from the this group are always played from the observer and unattenuated.
	 * @param maxVoices the maximum amount of voices for this group. 0 is unlimited.
	 * @return a new group.
	 */
	public static SoundGroup group(boolean occludable, boolean twoDimensional, boolean zeroPosition, int maxVoices)
	{
		return group(null, occludable, twoDimensional, zeroPosition, maxVoices);
	}

	/**
	 * Creates a new group type with defaults set.
	 * @param parent the parent group to accumulate from.
	 * @param occludable if true, the sounds played from the this group are occludable.
	 * @param twoDimensional if true, the sounds played from the this group are panned, not spacialized.
	 * @param zeroPosition if true, the sounds played from the this group are always played from the observer and unattenuated.
	 * @param maxVoices the maximum amount of voices for this group. 0 is unlimited.
	 * @return a new group.
	 */
	public static SoundGroup group(SoundGroupType parent, boolean occludable, boolean twoDimensional, boolean zeroPosition, int maxVoices)
	{
		return new SoundGroup(parent, occludable, twoDimensional, zeroPosition, maxVoices);
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
		Event event = new Event();
		event.type = Event.Type.PLAY;
		event.channel = channel;
		event.location = location;
		event.category = category;
		event.group = group;
		event.sound = data;
		
		event.initGain = 1f;
		event.initPitch = 1f;
		
		enqueueEvent(event);
	}
	
	/**
	 * Plays a looping sound.
	 * Be careful - you need to be able to stop this sound!
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 */
	public void playLooping(SoundData data, SoundGroupType group)
	{
		playLooping(data, group, null, null, null);
	}
	
	/**
	 * Plays a looping sound.
	 * Be careful - you need to be able to stop this sound!
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param location the sound location in the world.
	 */
	public void playLooping(SoundData data, SoundGroupType group, SoundLocation location)
	{
		playLooping(data, group, null, location, null);
	}
	
	/**
	 * Plays a looping sound.
	 * Be careful - you need to be able to stop this sound!
	 * @param data the sound data.
	 * @param group the sound group it plays under. 
	 * @param category the sound rolloff category.
	 * @param location the sound location in the world.
	 */
	public void playLooping(SoundData data, SoundGroupType group, SoundCategoryType category, SoundLocation location)
	{
		playLooping(data, group, category, location, null);
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
		Event event = new Event();
		event.type = Event.Type.PLAY_LOOP;
		event.channel = channel;
		event.location = location;
		event.category = category;
		event.group = group;
		event.sound = data;
		
		event.initGain = 1f;
		event.initPitch = 1f;
		
		enqueueEvent(event);
	}
	
	/**
	 * Pauses all instances of sounds playing in a group.
	 * @param group the group object.
	 */
	public void pauseGroup(SoundGroup group)
	{
		Event event = new Event();
		event.type = Event.Type.PAUSE;
		event.group = group;
		enqueueEvent(event);
	}
	
	/**
	 * Pauses all instances of sounds playing on a location.
	 * @param location the location object.
	 */
	public void pauseLocation(SoundLocation location)
	{
		Event event = new Event();
		event.type = Event.Type.PAUSE;
		event.location = location;
		enqueueEvent(event);
	}
	
	/**
	 * Resumes all instances of sounds playing in a group.
	 * @param group the group object.
	 */
	public void resumeGroup(SoundGroup group)
	{
		Event event = new Event();
		event.type = Event.Type.RESUME;
		event.group = group;
		enqueueEvent(event);
	}
	
	/**
	 * Resumes all instances of sounds playing on a location.
	 * @param location the location object.
	 */
	public void resumeLocation(SoundLocation location)
	{
		Event event = new Event();
		event.type = Event.Type.RESUME;
		event.location = location;
		enqueueEvent(event);
	}
	
	/**
	 * Stops all instances of a sound playing.
	 * @param data the sound data.
	 */
	public void stopSound(SoundData data)
	{
		Event event = new Event();
		event.type = Event.Type.STOP;
		event.sound = data;
		enqueueEvent(event);
	}
	
	/**
	 * Stops all sounds in a group.
	 * @param group the sound group.
	 */
	public void stopGroup(SoundGroup group)
	{
		Event event = new Event();
		event.type = Event.Type.STOP;
		event.group = group;
		enqueueEvent(event);
	}
	
	/**
	 * Stops all sounds on a location.
	 * @param location the location object.
	 * @param channel the optional channel. 
	 */
	public void stopLocation(SoundLocation location, Integer channel)
	{
		Event event = new Event();
		event.type = Event.Type.STOP;
		event.location = location;
		event.channel = channel;
		enqueueEvent(event);
	}
	
	/**
	 * @return the amount of time it took the voice update loop to update in nanoseconds.
	 */
	public long getUpdateVoiceNanos()
	{
		return updateVoiceNanos;
	}
	
	/**
	 * @return the amount of time it took the event loop to update in nanoseconds.
	 */
	public long getUpdateEventNanos()
	{
		return updateEventNanos;
	}
	
	/**
	 * @return the amount of available voices. 
	 */
	public int getAvailableVoiceCount()
	{
		return availableVoices.size();
	}
	
	/**
	 * @return the amount of used voices. 
	 */
	public int getUsedVoiceCount()
	{
		return usedVoices.size();
	}
	
	/**
	 * Stops all threads, sounds, and deallocates everything.
	 */
	public void shutDown()
	{
		handleStopAll();

		if  (!primedStreams.isEmpty())
		{
			Iterator<SoundStream> it = primedStreams.values().iterator();
			SoundStream ss = null;
			while(it.hasNext())
			{
				ss = it.next();
				for (OALBuffer b : ss.buffers)
					b.destroy();
			}
			primedStreams.clear();
		}
		
		while (!usedVoices.isEmpty())
		{
			Voice voice = usedVoices.pollFirst();
			stopVoice(voice);
			voice.destroy();
		}
		while (!availableVoices.isEmpty())
		{
			Voice voice = availableVoices.pollFirst();
			stopVoice(voice);
			voice.destroy();
		}

		cache.destroy();
		
		processor.shutdown();
		
		soundToVoicesMap.clear();
		soundToVoicesMap = null;
		locationToVoicesMap.clear();
		locationToVoicesMap = null;
		groupToVoicesMap.clear();
		groupToVoicesMap = null;

		random = null;
		
		primedStreams = null;
		processDelay = null;
		usedVoices = null;
		availableVoices = null;
		deadVoices = null;
		
		if (system != null)
		{
			system.shutDown();
			system = null;
			context = null;
		}
	}
	
	private void enqueueEvent(Event event)
	{
		// lock queue during write.
		synchronized (eventQueue)
		{
			eventQueue.add(event);
		}		
	}
	
	/**
	 * Updates the events pending to be processed.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * no new events, like effect changes, sounds to play, or sounds
	 * to stop, will ever be processed.
	 */
	private void updateEvents()
	{
		long nanotime = System.nanoTime();

		// lock queue during read.
		synchronized (eventQueue)
		{
			while (!processDelay.isEmpty())
			{
				handleEvent(processDelay.pollFirst());
			}
			while(!eventQueue.isEmpty())
			{
				handleEvent(eventQueue.pollFirst());
			}
		}
		
		updateEventNanos = System.nanoTime() - nanotime;
	}

	/**
	 * Updates the active voices.
	 * Called by update(), but exposed to developers here for
	 * those who want to fine-tune stage update frequencies.
	 * <p>If this is never called, either by update() or directly,
	 * no voice attributes like pitch, panning, or gain attenuation 
	 * will be updated, nor will used voices be freed.
	 */
	private void updateVoices()
	{
		long nanotime = System.nanoTime();
		Iterator<Voice> it = usedVoices.iterator();
		while (it.hasNext())
			updateVoice(it.next());
		
		// Clean up dead voices.
		it = usedVoices.iterator();
		while (it.hasNext())
		{
			Voice voice = it.next();
			if (!voice.source.isPlaying() && !voice.source.isPaused())
			{
				deadVoices.add(voice);
				it.remove();
			}
		}

		while (!deadVoices.isEmpty())
			deallocateVoice(deadVoices.pollFirst());

		updateVoiceNanos = System.nanoTime() - nanotime;
	}
	
	/**
	 * Finds an unused or suitable voice for an incoming sound to play.
	 * Does basic checks for virtual channel availability and may stop other sounds in order to allocate a voice.
	 * @return a voice, or null if no available voice.
	 */
	private Voice allocateVoice(Event event)
	{
		SoundData data = event.sound; 
		SoundGroupType group = event.group;
		SoundCategoryType category = event.category; 
		SoundLocation location = event.location;
		Integer channel = event.channel;
		
		final Voice out;

		// actor clear?
		if (location != null && channel != null)
		{
			Voice voice = null;
			Deque<Voice> voiceList = locationToVoicesMap.get(location);
			if (voiceList != null)
			{
				for (Voice v : voiceList)
				{
					if (v.channel == channel)
					{
						voice = v;
						break;
					}
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
		else
			out = null;
		
		if (out == null)
			return null;

		try {
			listeners.forEach((listener) -> listener.onVoiceAllocated(out));
			prepareVoice(out, data);
			
			// sound stream set already, if any.
			
			out.looping = event.type == Event.Type.PLAY_LOOP;
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
			listeners.forEach((listener) -> listener.onVoiceStreamStarted(voice));
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

		listeners.forEach((listener) -> listener.onVoicePrepared(voice));
	}
	
	/**
	 * Deallocates a previously allocated voice.
	 * @param voice the voice to deallocate.
	 */
	private void deallocateVoice(Voice voice)
	{
		voice.source.stop();
		voice.reset();
		deregisterVoice(voice);
		listeners.forEach((listener) -> listener.onVoiceDeallocated(voice));
	}
	
	/**
	 * Registers an already-allocated voice in the system.
	 * @param voice the voice to register.
	 */
	private void registerVoice(Voice voice)
	{
		if (voice.group != null)
			addVoiceToMap(voice.group, voice, groupToVoicesMap);
		if (voice.location != null)
			addVoiceToMap(voice.location, voice, locationToVoicesMap);
		addVoiceToMap(voice.data, voice, soundToVoicesMap);
	}
	
	/**
	 * Deregisters an allocated voice in the system.
	 * Happens before deallocation.
	 * @param voice the voice to deregister.
	 */
	private void deregisterVoice(Voice voice)
	{
		if (voice.group != null)
			removeVoiceFromMap(voice.group, voice, groupToVoicesMap);
		if (voice.location != null)
			removeVoiceFromMap(voice.location, voice, locationToVoicesMap);
		removeVoiceFromMap(voice.data, voice, soundToVoicesMap);
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
			OALEffectSlot echoSlot = voice.effectSlot0;
			OALEffectSlot reverbSlot = voice.effectSlot1;
			EchoEffect echoEffect = voice.echoEffect;
			ReverbEffect reverbEffect = voice.reverbEffect;

			source.setPosition(update.sourceX, update.sourceY, update.sourceZ);
			source.setGain(update.gain);
			source.setPitch(update.pitch);
			
			filter.setGain(1.0f);
			filter.setHFGain(update.gainHF);
			filter.setLFGain(update.gainLF);
			source.setFilter(filter); // force update
			
			if (soundScape != null)
			{
				if (soundScape.getEcho() != null)
				{
					SoundEchoType echoType = soundScape.getEcho();
					echoEffect.setDamping(echoType.getDamping());
					echoEffect.setDelay(echoType.getDelay());
					echoEffect.setFeedback(echoType.getFeedback());
					echoEffect.setLRDelay(echoType.getLRDelay());
					echoEffect.setSpread(echoType.getSpread());
					echoSlot.setEffect(echoEffect);
				}
				
				if (soundScape.getReverb()!= null)
				{
					SoundReverbType reverbType = soundScape.getReverb();
					reverbEffect.setAirAbsorptionGainHF(reverbType.getAirAbsorptionHFGain());
					reverbEffect.setDecayHFLimit(reverbType.isDecayHFLimit());
					reverbEffect.setDecayHFRatio(reverbType.getDecayHFRatio());
					reverbEffect.setDecayTime(reverbType.getDecayTime());
					reverbEffect.setDensity(reverbType.getDensity());
					reverbEffect.setDiffusion(reverbType.getDiffusion());
					reverbEffect.setGain(reverbType.getGain());
					reverbEffect.setHFGain(reverbType.getHFGain());
					reverbEffect.setLateDelay(reverbType.getLateDelay());
					reverbEffect.setLateGain(reverbType.getLateGain());
					reverbEffect.setReflectionDelay(reverbType.getReflectionDelay());
					reverbEffect.setReflectionGain(reverbType.getReflectionGain());
					reverbEffect.setRoomRolloffFactor(reverbType.getRoomRolloffFactor());
					reverbSlot.setEffect(reverbEffect);
				}
			}
			
			echoSlot.setGain(update.gainEffectEcho);
			reverbSlot.setGain(update.gainEffectReverb);
			
			if (voice.stream != null)
			{
				try {
					voice.stream.streamUpdate(voice);
				} catch (UnsupportedAudioFileException e) {
					listeners.forEach((listener) -> listener.onSoundUnsupportedError(voice.data, e));
					stopVoice(voice);
					return false;
				} catch (IOException e) {
					listeners.forEach((listener) -> listener.onSoundIOError(voice.data, e));
					stopVoice(voice);
					return false;
				}
			}
			
			return true;
		}
		else
		{
			stopVoice(voice);
			return false;
		}
		
	}
	
	/**
	 * Handles an incoming sound event.
	 * @param event
	 */
	private void handleEvent(Event event) 
	{
		switch (event.type)
		{
			case PLAY:
				handlePlay(event);
				break;
			case PLAY_LOOP:
				handlePlayLoop(event);
				break;
			case STOP:
				handleStop(event);
				break;
			case STOP_ALL:
				handleStopAll();
				break;
			case PAUSE:
				handlePause(event);
				break;
			case RESUME:
				handleResume(event);
				break;
			case PRECACHE:
				handlePrecache(event);
				break;
		}
	}

	/**
	 * Handles a precache event.
	 */
	private void handlePrecache(Event event)
	{
		if (event.sound != null)
			cacheSounds(event.sound);
	}

	/**
	 * Handles a stop all event.
	 */
	private void handleStopAll()
	{
		for (Voice voice : usedVoices)
			stopVoice(voice);
	}

	/**
	 * Handles a sound stop event.
	 */
	private void handleStop(Event event)
	{
		if (event.location != null)
		{
			Deque<Voice> voiceList = locationToVoicesMap.get(event.location);
			if (voiceList != null) for (Voice voice : voiceList)
			{
				if (event.channel == null || voice.channel == event.channel)
					stopVoice(voice);
			}
		}
		
		if (event.group != null)
		{
			Deque<Voice> voiceList = groupToVoicesMap.get(event.group);
			if (voiceList != null) for (Voice voice : voiceList)
				stopVoice(voice);
		}
		
		if (event.sound != null)
		{
			Deque<Voice> voiceList = soundToVoicesMap.get(event.sound);
			if (voiceList != null) for (Voice voice : voiceList)
				stopVoice(voice);
		}
		
	}

	/**
	 * Handles a pause event.
	 */
	private void handlePause(Event event)
	{
		if (event.location != null)
		{
			Deque<Voice> voiceList = locationToVoicesMap.get(event.location);
			if (voiceList != null) for (Voice voice : voiceList)
				voice.source.pause();
		}
		
		if (event.group != null)
		{
			Deque<Voice> voiceList = groupToVoicesMap.get(event.group);
			if (voiceList != null) for (Voice voice : voiceList)
				voice.source.pause();
		}
	}

	/**
	 * Handles a resume event.
	 */
	private void handleResume(Event event)
	{
		if (event.location != null)
		{
			Deque<Voice> voiceList = locationToVoicesMap.get(event.location);
			if (voiceList != null) for (Voice voice : voiceList)
				voice.source.play();
		}
		
		if (event.group != null)
		{
			Deque<Voice> voiceList = groupToVoicesMap.get(event.group);
			if (voiceList != null) for (Voice voice : voiceList)
				voice.source.play();
		}
	}

	/**
	 * Handles a sound play event.
	 * Returns true if handled, false if this is to be belayed.
	 */
	private boolean handlePlay(Event event)
	{
		Voice voice = allocateVoice(event);
		if (voice != null)
		{
			voice.source.setLooping(false);
			voice.source.play();
		}
		else if (event.sound.isAlwaysPlayed())
		{
			processDelay.add(event);
			return false;
		}
		
		return true;
	}

	/**
	 * Handles a sound play loop event.
	 * Returns true if handled, false if this is to be belayed.
	 */
	private boolean handlePlayLoop(Event event)
	{
		Voice voice = allocateVoice(event);
		if (voice != null)
		{
			// if stream, handled at stream level, else must be at source.
			voice.source.setLooping(voice.looping && voice.stream == null);
			voice.source.play();
		}
		else if (event.sound.isAlwaysPlayed())
		{
			processDelay.add(event);
			return false;
		}
		
		return true;
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
			positionX = voice.location != null ? voice.location.getSoundPositionX() : 0.0f;
			positionY = 0.0f;
			positionZ = 1.0f;
			
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
		
		float voiceGain = voice.initGain * voice.group.getCalculatedGain();
		float voicePitch = voice.initPitch * voice.group.getCalculatedPitch();
		float voiceGainLF = voice.group.getCalculatedLowPassGain();
		float voiceGainHF = voice.group.getCalculatedHighPassGain();
		float voiceEffectGain = voice.group.getCalculatedEffectGain();
		
		float soundGain = 1.0f;
		float soundPitch = 1.0f + (float)RandomUtils.randDouble(random, -sound.getPitchVariance(), sound.getPitchVariance());
	
		SoundRolloffType rolloff = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffLF = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffHF = DEFAULT_ROLLOFF;
		SoundRolloffType rolloffConic = DEFAULT_ROLLOFF;

		if (voice.category != null)
		{
			if (voice.category.getRolloffType() != null)
				rolloff = voice.category.getRolloffType();
			if (voice.category.getLowPassRolloffType() != null)
				rolloffLF = voice.category.getLowPassRolloffType();
			if (voice.category.getHighPassRolloffType() != null)
				rolloffHF = voice.category.getHighPassRolloffType();
			if (voice.category.getConicRolloffType() != null)
				rolloffConic = voice.category.getConicRolloffType();
		}

		float rolloffGain;
		float rolloffLFGain;
		float rolloffHFGain;
		float rolloffGainConic;
		
		float occlusionGain;
		float occlusionHFGain;
		float occlusionLFGain;
		
		float soundScapeEffectGain;
		float soundScapeEffectEchoGain;
		float soundScapeEffectReverbGain;
		
		float distanceEffectGain; 
	
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
	
		if (soundScape != null)
		{
			if (soundScape.getOcclusion() != null)
			{
				occlusionGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getGain()));
				occlusionLFGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getLowPassGain()));
				occlusionHFGain = 1.0f - (update.occlusion * (1.0f - soundScape.getOcclusion().getHighPassGain()));
			}
			else
			{
				occlusionGain = 1.0f;
				occlusionLFGain = 1.0f;
				occlusionHFGain = 1.0f;
			}
			
			if (soundScape.getEcho() != null)
				soundScapeEffectEchoGain = 1.0f;
			else
				soundScapeEffectEchoGain = 0.0f;

			if (soundScape.getReverb() != null)
				soundScapeEffectReverbGain = 1.0f;
			else
				soundScapeEffectReverbGain = 0.0f;

			soundScapeEffectGain = 1.0f;
			
			if (update.distance > soundScape.getMaxEffectGainDistance())
				distanceEffectGain = 1.0f;
			else if (soundScape.getMaxEffectGainDistance() == 0f)
				distanceEffectGain = 1.0f;
			else
				distanceEffectGain = ((update.distance / soundScape.getMaxEffectGainDistance()) * (1.0f - soundScape.getMinEffectGain())) + soundScape.getMinEffectGain();
		}
		else
		{
			occlusionGain = 1.0f;
			occlusionLFGain = 1.0f;
			occlusionHFGain = 1.0f;
			soundScapeEffectGain = 0.0f;
			soundScapeEffectEchoGain = 0.0f;
			soundScapeEffectReverbGain = 0.0f;
			distanceEffectGain = 1.0f; 
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
		update.gainLF = rolloffLFGain * occlusionLFGain * voiceGainLF;
		update.gainHF = rolloffHFGain * occlusionHFGain * voiceGainHF;
		update.gainEffectEcho = voiceEffectGain * distanceEffectGain * soundScapeEffectGain * soundScapeEffectEchoGain;
		update.gainEffectReverb = voiceEffectGain * distanceEffectGain * soundScapeEffectGain * soundScapeEffectReverbGain;
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
	
	private <T> void addVoiceToMap(T key, Voice voice, Map<T, Deque<Voice>> voiceMap)
	{
		Deque<Voice> voices;
		if ((voices = voiceMap.get(key)) == null)
			voiceMap.put(key, voices = new LinkedList<>());
		voices.add(voice);
	}

	private <T> void removeVoiceFromMap(T key, Voice voice, Map<T, Deque<Voice>> voiceMap)
	{
		Deque<Voice> voices;
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
		void onVoiceAllocated(Voice voice);
		void onVoiceDeallocated(Voice voice);
		void onStreamThreadStarted();
		void onStreamStep(Voice voice);
		void onStreamThreadEnded();
		void onSoundCached(SoundData data);
		void onSoundIOError(SoundData data, IOException e);
		void onSoundUnsupportedError(SoundData data, UnsupportedAudioFileException e);
	}
	
	/**
	 * A soundscape type.
	 */
	private static class SoundScape implements SoundScapeType
	{
		private SoundEchoType echoType;
		private SoundReverbType reverbType;
		private SoundOcclusionType occlusionType;
		private float minEffectGain;
		private float maxEffectDistance;

		private SoundScape(SoundEchoType echoType, SoundReverbType reverbType, SoundOcclusionType occlusionType, float minEffectGain, float maxEffectDistance)
		{
			super();
			this.echoType = echoType;
			this.reverbType = reverbType;
			this.occlusionType = occlusionType;
			this.minEffectGain = 1f;
			this.maxEffectDistance = 0f;
		}

		@Override
		public SoundEchoType getEcho()
		{
			return echoType;
		}

		@Override
		public SoundReverbType getReverb()
		{
			return reverbType;
		}

		@Override
		public SoundOcclusionType getOcclusion()
		{
			return occlusionType;
		}

		@Override
		public float getMaxEffectGainDistance()
		{
			return maxEffectDistance;
		}

		@Override
		public float getMinEffectGain()
		{
			return minEffectGain;
		}
	}
	
	/**
	 * A playback group type.
	 */
	public static class SoundGroup implements SoundGroupType
	{
		private SoundGroupType parent;
		
		private float gain;
		private float gainLF;
		private float gainHF;
		private float gainEffect;
		private float pitch;
		
		private boolean occludable;
		private boolean twoDimensional;
		private boolean zeroPosition;
		private int maxVoices;
	
		public SoundGroup(SoundGroupType parent, boolean occludable, boolean twoDimensional, boolean zeroPosition, int maxVoices)
		{
			this.parent = parent;
			this.gain = 1f;
			this.gainLF = 1f;
			this.gainHF = 1f;
			this.gainEffect = 0f;
			this.pitch = 1f;
			this.occludable = occludable;
			this.twoDimensional = twoDimensional;
			this.zeroPosition = zeroPosition;
			this.maxVoices = maxVoices;
		}
	
		@Override
		public float getCalculatedGain()
		{
			return (parent != null ? parent.getCalculatedGain() : 1f) * gain;
		}
	
		@Override
		public float getCalculatedPitch()
		{
			return (parent != null ? parent.getCalculatedPitch() : 1f) * pitch;
		}
	
		@Override
		public float getCalculatedLowPassGain()
		{
			return (parent != null ? parent.getCalculatedLowPassGain() : 1f) * gainLF;
		}
	
		@Override
		public float getCalculatedHighPassGain()
		{
			return (parent != null ? parent.getCalculatedHighPassGain() : 1f) * gainHF;
		}
	
		@Override
		public float getCalculatedEffectGain()
		{
			return (parent != null ? parent.getCalculatedEffectGain() : 1f) * gainEffect;
		}
	
		@Override
		public float getGain()
		{
			return gain;
		}
	
		public void setGain(float gain)
		{
			this.gain = gain;
		}

		@Override
		public float getPitch()
		{
			return pitch;
		}

		public void setPitch(float pitch)
		{
			this.pitch = pitch;
		}
	
		@Override
		public float getLowPassGain()
		{
			return gainLF;
		}
	
		public void setLowPassGain(float gainLF)
		{
			this.gainLF = gainLF;
		}

		@Override
		public float getHighPassGain()
		{
			return gainHF;
		}
	
		public void setHighPassGain(float gainHF)
		{
			this.gainHF = gainHF;
		}

		@Override
		public float getEffectGain()
		{
			return gainEffect;
		}
	
		public void setEffectGain(float gainEffect)
		{
			this.gainEffect = gainEffect;
		}

		@Override
		public boolean isOccludable()
		{
			return occludable;
		}
	
		@Override
		public boolean isTwoDimensional()
		{
			return twoDimensional;
		}
	
		@Override
		public boolean isZeroPosition()
		{
			return zeroPosition;
		}
	
		@Override
		public int getMaximumVoices()
		{
			return maxVoices;
		}
		
	}

	public static class Voice
	{
		private OALSource source;
		private BandPassFilter filter;
		private OALEffectSlot effectSlot0;
		private OALEffectSlot effectSlot1;
		private EchoEffect echoEffect;
		private ReverbEffect reverbEffect;
	
		private SoundData data;
		private SoundCategoryType category;
		private SoundGroupType group;
		private SoundLocation location;
		private Integer channel;
		private float initGain;
		private float initPitch;
	
		private SoundStream stream;
		private boolean looping;
	
		private Voice(OALContext context)
		{
			this.source = context.createSource();
			this.filter = context.createBandPassFilter();
			this.source.setFilter(this.filter);
			this.effectSlot0 = context.createEffectSlot();
			this.effectSlot0.setAutoUpdating(true);
			this.effectSlot1 = context.createEffectSlot();
			this.effectSlot1.setAutoUpdating(true);
			this.effectSlot0.setEffect(this.echoEffect = context.createEchoEffect());
			this.effectSlot1.setEffect(this.reverbEffect = context.createReverbEffect());
			this.source.setEffectSlot(0, this.effectSlot0);
			this.source.setEffectSlot(1, this.effectSlot1);
			reset();
		}
		
		private void reset()
		{
			this.data = null;
			this.category = null;
			this.group = null;
			this.location = null;
			this.channel = null;
			this.initGain = 1.0f;
			this.initPitch = 1.0f;
		}
		
		private void destroy()
		{
			source.setEffectSlot(1, null);
			source.setEffectSlot(0, null);
			effectSlot1.destroy();
			effectSlot0.destroy();
			reverbEffect.destroy();
			echoEffect.destroy();
			source.setFilter(null);
			source.setBuffer(null);
			filter.destroy();
			source.destroy();
		}
		
		@Override
		public String toString()
		{
			return source.toString();
		}
		
	}

	/**
	 * The streamer object made for each streaming voice.  
	 */
	private class SoundStream
	{
		protected OALBuffer[] buffers;
		protected JSPISoundHandle soundHandle;
		protected JSPISoundHandle.Decoder decoderRef;
	
		protected ByteBuffer bytebuffer;
		
		SoundStream(JSPISoundHandle soundHandle) throws UnsupportedAudioFileException, IOException
		{
			restartDecoder(soundHandle);
			buffers = context.createBuffers(2);
			AudioFormat decoderFormat = decoderRef.getDecodedAudioFormat();
			for (OALBuffer b : buffers)
			{
				b.setSamplingRate((int)decoderFormat.getSampleRate());
				b.setFormatByChannelsAndBits(decoderFormat.getChannels(), decoderFormat.getSampleSizeInBits());
				bytebuffer.clear();
				decoderRef.readPCMBytes(bytebuffer);
				bytebuffer.flip();
				b.setData(bytebuffer);
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
				bytebuffer = BufferUtils.createByteBuffer(buffersize);
			}
		}
		
		/**
		 * Updates the stream.
		 * @param voice the playing voice.
		 * @return the amount of bytes loaded.
		 * @throws UnsupportedAudioFileException if the audio file's format is not supported.
		 * @throws IOException if the stream cannot be read.
		 */
		public int streamUpdate(Voice voice) throws UnsupportedAudioFileException, IOException
		{
			OALSource source = voice.source;
			
			int out = -1;
			int p = source.getProcessedBufferCount();
			while (p-- > 0 && out != 0)
			{
				OALBuffer b = source.dequeueBuffer();
				bytebuffer.clear();
				out = decoderRef.readPCMBytes(bytebuffer);
				bytebuffer.flip();
				if (out > 0)
				{
					b.setData(bytebuffer);
					source.enqueueBuffer(b);
					listeners.forEach((listener) -> listener.onStreamStep(voice));
				}
				else if (out == 0 && voice.looping)
				{
					restartDecoder(soundHandle);
					bytebuffer.clear();
					out = decoderRef.readPCMBytes(bytebuffer);
					bytebuffer.flip();
					if (out > 0)
					{
						b.setData(bytebuffer);
						source.enqueueBuffer(b);
						listeners.forEach((listener) -> listener.onStreamStep(voice));
					}
				}
			}
			return out;
		}
		
	}

	/**
	 * Processor thread.
	 * Waits until events need processing, and then runs at update intervals until there's no more work to do.
	 */
	private class ProcessorThread extends Thread
	{
		private final long EVENTMILLIS = 1000L / 60;
		
		private volatile boolean keepAlive;
		
		private ProcessorThread() 
		{
			setName("SoundSystem-Processor");
			setDaemon(true);
			this.keepAlive = true;
		}
		
		@Override
		public void run()
		{
			boolean flipflop = false;
			
			while (keepAlive)
			{
				if (flipflop)
					updateVoices();
				updateEvents();
				
				flipflop = !flipflop;
				
				ThreadUtils.sleep(EVENTMILLIS);
			}
		}
		
		/**
		 * Stops this thread.
		 */
		public void shutdown()
		{
			keepAlive = false;
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
		private float gainEffectEcho;
		private float gainEffectReverb;
		private float pitch;
	}

	private static class Event
	{
		enum Type
		{
			PLAY,
			PLAY_LOOP,
			STOP,
			STOP_ALL,
			PAUSE,
			RESUME,
			PRECACHE
		}
		
		private Type type;
		private SoundData sound;
		private SoundGroupType group; 
		private SoundCategoryType category;
		private SoundLocation location; 
		private Integer channel;
		private float initGain;
		private float initPitch;
	}
	
}
