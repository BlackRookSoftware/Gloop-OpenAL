/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.enums;

import org.lwjgl.openal.AL11;

/**
 * Distance Model enumeration for internal OpenAL distance models for attenuating
 * the final gain of a Source in relation to the position/direction of the listener.
 * @author Matthew Tropiano
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
