/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal;

import org.lwjgl.openal.AL11;

import com.blackrook.gloop.openal.OALSystem.ContextLock;
import com.blackrook.gloop.openal.exception.SoundException;

/**
 * The Listener in the sound environment.
 * @author Matthew Tropiano
 */
public class OALListener
{
	/** This object's owning context. */
	private OALContext context;
	
	/** Listener's position. */
	protected float[] position;
	/** Listener's velocity vector. */
	protected float[] velocity;
	/** Listener's orientation vector (facing direction, then upward vector).*/
	protected float[] orientation;
	/** Listener Gain (master gain).*/
	protected float gain;
	
	/**
	 * Constructs a new Listener.
	 * Default position and velocity is (0, 0, 0), facing is -Z (0, 0, -1), up is (0, 1, 0) (y-axis).
	 * Gain is 1.0.
	 * There should only be one of these in a sound system.
	 */
	OALListener(OALContext context)
	{
		this.context = context;
		this.position = new float[3];
		this.velocity = new float[3];
		this.orientation = new float[6];
		this.gain = 1.0f;

		setPosition(0, 0, 0);
		setVelocity(0, 0, 0);
		setTop(0, 1, 0);
		setFacing(0, 0, 1);
		setGain(1f);
	}
	
	/**
	 * Convenience method for checking for an OpenAL error and throwing a SoundException
	 * if an error is raised. 
	 */
	private void errorCheck()
	{
		int error = AL11.alGetError();
		if (error != AL11.AL_NO_ERROR)
			throw new SoundException("Listener: AL returned \"" + AL11.alGetString(error) + "\"");
	}
	
	/**
	 * Sets the Listener's position attributes.
	 * @param x the x-axis component value.
	 * @param y	the y-axis component value.
	 * @param z	the z-axis component value.
	 */
	public void setPosition(float x, float y, float z)
	{
		position[0] = x;
		position[1] = y;
		position[2] = z;
		try (ContextLock lock = context.setCurrentContext()) {
			AL11.alListenerfv(AL11.AL_POSITION, position);
		}
		errorCheck();
	}

	/**
	 * Sets the Listener's velocity attributes.
	 * @param x the x-axis component value.
	 * @param y	the y-axis component value.
	 * @param z	the z-axis component value.
	 */
	public void setVelocity(float x, float y, float z)
	{
		velocity[0] = x;
		velocity[1] = y;
		velocity[2] = z;
		try (ContextLock lock = context.setCurrentContext()) {
			AL11.alListenerfv(AL11.AL_VELOCITY, velocity);
		}
		errorCheck();
	}

	/**
	 * Sets the Listener's facing attributes.
	 * @param x the x-axis component value.
	 * @param y	the y-axis component value.
	 * @param z	the z-axis component value.
	 */
	public void setFacing(float x, float y, float z)
	{
		orientation[0] = x;
		orientation[1] = y;
		orientation[2] = z;
		try (ContextLock lock = context.setCurrentContext()) {
			AL11.alListenerfv(AL11.AL_ORIENTATION, orientation);
		}
		errorCheck();
	}

	/**
	 * Sets the Listener's top-orientation.
	 * @param x the x-axis component value.
	 * @param y	the y-axis component value.
	 * @param z	the z-axis component value.
	 */
	public void setTop(float x, float y, float z)
	{
		orientation[3] = x;
		orientation[4] = y;
		orientation[5] = z;
		try (ContextLock lock = context.setCurrentContext()) {
			AL11.alListenerfv(AL11.AL_ORIENTATION, orientation);
		}
		errorCheck();
	}

	/**
	 * Sets the master gain for this Listener.
	 * @param f the new gain.
	 */
	public void setGain(float f)
	{
		gain = f;
		try (ContextLock lock = context.setCurrentContext()) {
			AL11.alListenerf(AL11.AL_GAIN, gain);
		}
		errorCheck();
	}
	
	/**
	 * Returns values into an array containing the listener's position in space: X, Y, Z.
	 * @param out the output array to put the values into.
	 * @throws ArrayIndexOutOfBoundsException if out's length is less than 3.
	 */
	public void getPosition(float[] out)
	{
		System.arraycopy(position, 0, out, 0, position.length);
	}

	/**
	 * Returns values into an array containing the listener's velocity: X, Y, Z.
	 * @param out the output array to put the values into.
	 * @throws ArrayIndexOutOfBoundsException if out's length is less than 3.
	 */
	public void getVelocity(float[] out)
	{
		System.arraycopy(velocity, 0, out, 0, velocity.length);
	}

	/**
	 * Returns values into an array containing the listener's facing 
	 * orientation and upward orientation: facing X, Y, Z, then upward X, Y, Z.
	 * @param out the output array to put the values into.
	 * @throws ArrayIndexOutOfBoundsException if out's length is less than 6.
	 */
	public void getOrientation(float[] out)
	{
		System.arraycopy(orientation, 0, out, 0, orientation.length);
	}

	/**
	 * @return this listener's gain.
	 */
	public float getGain()
	{
		return gain;
	}

}
