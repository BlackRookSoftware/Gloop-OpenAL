/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.util.system;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.blackrook.gloop.openal.OALBuffer;

/**
 * A Buffer cache used for caching often-used buffers 
 * so that they don't need to be reloaded.
 */
public class SoundCache implements AutoCloseable
{
	/** Comparator for resource names. */
	protected static final Comparator<Node> NAME_COMPARATOR = new Comparator<Node>()
	{
		@Override
		public int compare(Node n1, Node n2)
		{
			return n1.sound.getPath().compareTo(n2.sound.getPath());
		}
	};
	
	/** Comparator for buffer sizes. */
	protected static final Comparator<Node> SIZE_COMPARATOR = new Comparator<Node>()
	{
		@Override
		public int compare(Node n1, Node n2)
		{
			return n2.buffer.getSize() - n1.buffer.getSize();
		}
	};
		
	protected int maxByteSize;
	protected int currBytes;
	
	private SortedSet<Node> buffersBySize;
	private Map<SoundData, Node> buffersByName;
	
	/**
	 * Creates a new buffer cache with a set amount of byte capacity.
	 * @param maxByteSize the maximum bytes of this cache.
	 */
	public SoundCache(int maxByteSize)
	{
		this.maxByteSize = maxByteSize;
		currBytes = 0;
		buffersBySize = new TreeSet<>(SIZE_COMPARATOR);
		buffersByName = new HashMap<>(20);
	}
	
	/**
	 * Adds a buffer to the cache.
	 * @param sound the sound definition to map.
	 * @param buffer the created buffer to store.
	 */
	public void addBuffer(SoundData sound, OALBuffer buffer)
	{
		if (buffersByName.containsKey(sound))
			return;
		
		buffer.getSize();
		currBytes += buffer.getSize();
		while (maxByteSize > 0 && currBytes > maxByteSize)
			removeLargestBuffer().destroy();
		
		Node n = new Node(sound, buffer);
		buffersByName.put(sound, n);
		buffersBySize.add(n);
	}

	/**  
	 * Gets an existing buffer.
	 * Null if not found. 
	 * @param resource the sound resource.
	 * @return the buffer that contains the sound data.
	 */
	public OALBuffer getBuffer(SoundData resource)
	{
		Node n = buffersByName.get(resource);
		if (n == null)
			return null;
		return n.buffer;
	}
	
	/**
	 * Removes the largest buffer.
	 * @return the buffer removed. It is NOT freed.
	 */
	public OALBuffer removeLargestBuffer()
	{
		if (!buffersBySize.isEmpty())
		{
			Node n = buffersBySize.first();
			buffersBySize.remove(n);
			currBytes -= n.buffer.getSize();
			buffersByName.remove(n.sound);
			return n.buffer;
		}
		return null;
	}

	/**
	 * Destroys all buffers and stuff.
	 */
	public void destroy()
	{
		while (!buffersBySize.isEmpty())
			removeLargestBuffer().destroy();
		buffersByName.clear();
	}
	
	@Override
	public void close() throws Exception
	{
		destroy();
	}

	/** 
	 * Node class for combining resources with buffers. 
	 */
	public class Node
	{
		SoundData sound;
		OALBuffer buffer;
		
		public Node(SoundData res, OALBuffer buf)
		{
			sound = res;
			buffer = buf;
		}
	}
	
}

