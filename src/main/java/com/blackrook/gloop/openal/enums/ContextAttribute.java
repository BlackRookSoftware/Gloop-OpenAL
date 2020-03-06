/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.enums;

import org.lwjgl.openal.ALC11;

/**
 * Enumeration of Context creation attributes.
 * @author Matthew Tropiano
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
