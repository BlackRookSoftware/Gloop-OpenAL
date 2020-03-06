/*******************************************************************************
 * Copyright (c) 2014 Black Rook Software
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

/**
 * A listener class for Sources.
 * Sources fire events to listeners attached to them when events occur on them.
 * @author Matthew Tropiano
 */
public interface OALSourceListener
{
	/**
	 * Called when a Source is played.
	 * @param source the source that this occurred on. 
	 */
	public void sourcePlayed(OALSource source);
	
	/**
	 * Called when a Source is paused.
	 * @param source the source that this occurred on. 
	 */
	public void sourcePaused(OALSource source);
	
	/**
	 * Called when a Source is rewound.
	 * @param source the source that this occurred on. 
	 */
	public void sourceRewound(OALSource source);
	
	/**
	 * Called when a Source is stopped, NOT when it stops naturally.
	 * @param source the source that this occurred on. 
	 */
	public void sourceStopped(OALSource source);

	/**
	 * Called when a Source gets a buffer enqueued on it.
	 * @param source the source that this occurred on. 
	 * @param buffer the buffer enqueued. 
	 */
	public void sourceBufferEnqueued(OALSource source, OALBuffer buffer);
	
	/**
	 * Called when a Source gets a buffer dequeued from it.
	 * @param source the source that this occurred on. 
	 * @param buffer the buffer dequeued. 
	 */
	public void sourceBufferDequeued(OALSource source, OALBuffer buffer);
	
}
