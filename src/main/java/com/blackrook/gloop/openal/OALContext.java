/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.openal.AL11;
import org.lwjgl.openal.ALC11;

import com.blackrook.gloop.openal.exception.SoundException;

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
	
	OALContext(OALSystem system, OALDevice device, AttributeValue ... attributes)
	{
		super(system);
		this.device = device;
		this.attributeMap = new HashMap<>(Math.max(attributes.length, 1), 1f);
		for (AttributeValue av : attributes)
			attributeMap.put(av.attribute, av.value);
		create();
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
	
	@Override
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
	protected void free() throws SoundException 
	{
		ALC11.alcDestroyContext(getHandle());
	}

}
