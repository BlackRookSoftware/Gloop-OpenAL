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
import java.util.concurrent.locks.ReentrantLock;

import org.lwjgl.openal.ALC11;

import com.blackrook.gloop.openal.exception.SoundException;
import com.blackrook.gloop.openal.exception.SoundSystemException;

/**
 * This class is a central sound system class designed to manage an OpenAL instance and environment.
 * @author Matthew Tropiano
 */
public final class OALSystem
{
	/**
	 * A way to lock a current context down for a series of calls to OpenAL.
	 */
	public interface ContextLock extends AutoCloseable
	{
		/**
		 * <b>DO NOT CALL THIS METHOD DIRECTLY.</b> 
		 * This is called by the time the user receives it, 
		 * and serves as a means for obscuring a locking mechanism. 
		 */
		public void lock();
		
		/**
		 * <b>YOU SHOULD NOT NEED TO CALL THIS METHOD DIRECTLY.</b> 
		 * This is meant to be automatically called from a try-with-resources. 
		 */
		@Override
		public void close();
	}
	
	/**
	 * A way to lock a current context down for a series of calls to OpenAL.
	 * The "AL" (not "ALC") calls rely on current context, so while the call is made,
	 * it cannot change when a context is being altered through them.
	 */
	private static class ReentrantContextLock implements ContextLock
	{
		private ReentrantLock lock;

		private ReentrantContextLock()
		{
			this.lock = new ReentrantLock();
		}
		
		@Override
		public void lock() 
		{
			lock.lock();
		}
		
		@Override
		public void close()
		{
			lock.unlock();
		}
	}
	
	/**
	 * A no-op context lock for single-context environment.
	 * In single-context setups, doing any sort of multithread protection
	 * is unnecessary since OpenAL is thread-safe already and caring about a 
	 * critical section, if any, is moot if there is only one context to manage.
	 */
	public static class NoOpContextLock implements ContextLock
	{
		@Override
		public void lock() 
		{
			// Do nothing.
		}

		@Override
		public void close()
		{
			// Do nothing.
		}
	}
	
	/** Current context. */
	private OALContext currentContext;
	/** The context lock. */
	private ContextLock contextLock;
	/** Set of created devices. */
	private Set<OALDevice> openDevices;

	/**
	 * Creates a new OpenAL Sound System.
	 */
	public OALSystem()
	{
		this.currentContext = null;
		this.openDevices = new HashSet<>(2, 1f);
		this.contextLock = new NoOpContextLock();
	}

	/**
	 * Upgrades the context lock to a strict lock.
	 */
	void upgradeLock()
	{
		if (!(contextLock instanceof ReentrantContextLock))
			contextLock = new ReentrantContextLock();
	}
	
	/**
	 * Sets a new context as current.
	 * @param context the context to make current, or null for no context.
	 */
	ContextLock setCurrentContext(OALContext context)
	{
		contextLock.lock();
		// already current? Do nothing.
		if (currentContext == context)
			return contextLock;
		else if (ALC11.alcMakeContextCurrent(context != null ? context.getHandle() : 0L))
		{
			currentContext = context;
			return contextLock;
		}
		else
			throw new SoundException("Could not acquire context lock.");
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
		OALDevice device = new OALDevice(this, name);
		openDevices.add(device);
		return device;
	}
	
	/**
	 * Runs all Shut Down hooks, destroys all contexts and closes all open devices.
	 */
	public void shutDown()
	{
		synchronized (openDevices)
		{
			// need to copy set contents - deleting these handles will affect the set as we iterate.
			OALHandle[] toDelete = new OALHandle[openDevices.size()];
			openDevices.toArray(toDelete);
			for (int i = 0; i < toDelete.length; i++)
			{
				toDelete[i].destroy();
				openDevices.remove(toDelete[i]);
			}
		}
	}

	@Override
	public void finalize() throws Throwable
	{
		shutDown();
		super.finalize();
	}
	
}


