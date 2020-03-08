/*******************************************************************************
 * Copyright (c) 2020 Black Rook Software
 * This program and the accompanying materials are made available under the 
 * terms of the GNU Lesser Public License v2.1 which accompanies this 
 * distribution, and is available at 
 * http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 ******************************************************************************/
package com.blackrook.gloop.openal.effect;

import org.lwjgl.openal.EXTEfx;

import com.blackrook.gloop.openal.OALEffect;
import com.blackrook.gloop.openal.OALSystem;
import com.blackrook.gloop.openal.struct.MathUtils;

/**
 * Vocal Modifier effect for sources.
 * @author Matthew Tropiano
 */
public class VocalMorpherEffect extends OALEffect
{
	/** WaveForm type enumeration. */
	public static enum WaveForm
	{
		SINUSOID(EXTEfx.AL_VOCAL_MORPHER_WAVEFORM_SINUSOID),
		TRIANGLE(EXTEfx.AL_VOCAL_MORPHER_WAVEFORM_TRIANGLE),
		SAWTOOTH(EXTEfx.AL_VOCAL_MORPHER_WAVEFORM_SAWTOOTH);
		
		final int alVal;
		private WaveForm(int alVal) {this.alVal = alVal;}
	}
	
	/** Phoneme type enumeration. */
	public static enum Phoneme
	{
		A(EXTEfx.AL_VOCAL_MORPHER_PHONEME_A),
		E(EXTEfx.AL_VOCAL_MORPHER_PHONEME_E),
		I(EXTEfx.AL_VOCAL_MORPHER_PHONEME_I),
		O(EXTEfx.AL_VOCAL_MORPHER_PHONEME_O),
		U(EXTEfx.AL_VOCAL_MORPHER_PHONEME_U),
		AA(EXTEfx.AL_VOCAL_MORPHER_PHONEME_AA),
		AE(EXTEfx.AL_VOCAL_MORPHER_PHONEME_AE),
		AH(EXTEfx.AL_VOCAL_MORPHER_PHONEME_AH),
		AO(EXTEfx.AL_VOCAL_MORPHER_PHONEME_AO),
		EH(EXTEfx.AL_VOCAL_MORPHER_PHONEME_EH),
		ER(EXTEfx.AL_VOCAL_MORPHER_PHONEME_ER),
		IH(EXTEfx.AL_VOCAL_MORPHER_PHONEME_IH),
		IY(EXTEfx.AL_VOCAL_MORPHER_PHONEME_IY),
		UH(EXTEfx.AL_VOCAL_MORPHER_PHONEME_UH),
		UW(EXTEfx.AL_VOCAL_MORPHER_PHONEME_UW),
		B(EXTEfx.AL_VOCAL_MORPHER_PHONEME_B),
		D(EXTEfx.AL_VOCAL_MORPHER_PHONEME_D),
		F(EXTEfx.AL_VOCAL_MORPHER_PHONEME_F),
		G(EXTEfx.AL_VOCAL_MORPHER_PHONEME_G),
		J(EXTEfx.AL_VOCAL_MORPHER_PHONEME_J),
		K(EXTEfx.AL_VOCAL_MORPHER_PHONEME_K),
		L(EXTEfx.AL_VOCAL_MORPHER_PHONEME_L),
		M(EXTEfx.AL_VOCAL_MORPHER_PHONEME_M),
		N(EXTEfx.AL_VOCAL_MORPHER_PHONEME_N),
		P(EXTEfx.AL_VOCAL_MORPHER_PHONEME_P),
		R(EXTEfx.AL_VOCAL_MORPHER_PHONEME_R),
		S(EXTEfx.AL_VOCAL_MORPHER_PHONEME_S),
		T(EXTEfx.AL_VOCAL_MORPHER_PHONEME_T),
		V(EXTEfx.AL_VOCAL_MORPHER_PHONEME_V),
		Z(EXTEfx.AL_VOCAL_MORPHER_PHONEME_Z);
		
		final int alVal;
		private Phoneme(int alVal) {this.alVal = alVal;}
	}
	
	/** Vocal morpher rate in Hertz. */
	protected float rate;
	/** Morpher phoneme A. */
	protected Phoneme phonemeA;
	/** Morpher phoneme B. */
	protected Phoneme phonemeB;
	/** Morpher phoneme A coarse tuning in semitones. */
	protected int phonemeACoarseTuning;
	/** Morpher phoneme B coarse tuning in semitones. */
	protected int phonemeBCoarseTuning;
	/** Morpher waveform. */
	protected WaveForm waveForm;

	public VocalMorpherEffect(OALSystem system)
	{
		super(system, EXTEfx.AL_EFFECT_VOCAL_MORPHER);
		setPhonemeA(Phoneme.A);
		setPhonemeB(Phoneme.ER);
		setPhonemeACoarseTuning(EXTEfx.AL_VOCAL_MORPHER_DEFAULT_PHONEMEA_COARSE_TUNING);
		setPhonemeBCoarseTuning(EXTEfx.AL_VOCAL_MORPHER_DEFAULT_PHONEMEB_COARSE_TUNING);
		setWaveform(WaveForm.SINUSOID);
		setRate(EXTEfx.AL_VOCAL_MORPHER_DEFAULT_RATE);
	}

	/** 
	 * @return the current morpher phoneme A. 
	 */
	public final Phoneme getPhonemeA()
	{
		return phonemeA;
	}

	/** 
	 * Set morpher phoneme A.
	 * @param phonemeA the new value.
	 */
	public final void setPhonemeA(Phoneme phonemeA)
	{
		this.phonemeA = phonemeA;
		// AL_VOCAL_MORPHER_PHONEMEA was not in LWJGL.
		EXTEfx.alEffecti(getALId(), 1 /* AL_VOCAL_MORPHER_PHONEMEA */, phonemeA.alVal);
		errorCheck();
	}

	/** 
	 * @return the current morpher phoneme A coarse tuning in semitones. 
	 */
	public final int getPhonemeACoarseTuning()
	{
		return phonemeACoarseTuning;
	}

	/** 
	 * Set morpher phoneme A coarse tuning in semitones (-24 to 24). 
	 * @param phonemeACoarseTuning the new value.
	 */
	public final void setPhonemeACoarseTuning(int phonemeACoarseTuning)
	{
		this.phonemeACoarseTuning = phonemeACoarseTuning;
		// AL_VOCAL_MORPHER_PHONEMEA_COARSE_TUNING was not in LWJGL.
		EXTEfx.alEffecti(getALId(), 2 /* AL_VOCAL_MORPHER_PHONEMEA_COARSE_TUNING */, MathUtils.clampValue(phonemeACoarseTuning, EXTEfx.AL_VOCAL_MORPHER_MIN_PHONEMEA_COARSE_TUNING, EXTEfx.AL_VOCAL_MORPHER_MAX_PHONEMEA_COARSE_TUNING));
		errorCheck();
	}

	/** 
	 * @return the current morpher phoneme B. 
	 */
	public final Phoneme getPhonemeB()
	{
		return phonemeB;
	}

	/** 
	 * Set morpher phoneme B. 
	 * @param phonemeB the new value.
	 */
	public final void setPhonemeB(Phoneme phonemeB)
	{
		this.phonemeB = phonemeB;
		// AL_VOCAL_MORPHER_PHONEMEB was not in LWJGL.
		EXTEfx.alEffecti(getALId(), 3 /* AL_VOCAL_MORPHER_PHONEMEB */, phonemeB.alVal);
		errorCheck();
	}

	/** 
	 * @return the current morpher phoneme B coarse tuning in semitones. 
	 */
	public final int getPhonemeBCoarseTuning()
	{
		return phonemeBCoarseTuning;
	}

	/** 
	 * Set morpher phoneme B coarse tuning in semitones (-24 to 24).
	 * @param phonemeBCoarseTuning the new value.
	 */
	public final void setPhonemeBCoarseTuning(int phonemeBCoarseTuning)
	{
		this.phonemeBCoarseTuning = phonemeBCoarseTuning;
		// AL_VOCAL_MORPHER_PHONEMEB_COARSE_TUNING was not in LWJGL.
		EXTEfx.alEffecti(getALId(), 4 /* AL_VOCAL_MORPHER_PHONEMEB_COARSE_TUNING */, MathUtils.clampValue(phonemeBCoarseTuning, EXTEfx.AL_VOCAL_MORPHER_MIN_PHONEMEB_COARSE_TUNING, EXTEfx.AL_VOCAL_MORPHER_MAX_PHONEMEB_COARSE_TUNING));
		errorCheck();
	}

	/** 
	 * @return the current vocal morpher rate in Hertz. 
	 */
	public final float getRate()
	{
		return rate;
	}

	/** 
	 * Set vocal morpher rate in Hertz (0.0 to 10.0). 
	 * @param rate the new value.
	 */
	public final void setRate(float rate)
	{
		this.rate = rate;
		// AL_VOCAL_MORPHER_RATE was not in LWJGL.
		EXTEfx.alEffectf(getALId(), 6 /* AL_VOCAL_MORPHER_RATE */, MathUtils.clampValue(rate, EXTEfx.AL_VOCAL_MORPHER_MIN_RATE, EXTEfx.AL_VOCAL_MORPHER_MAX_RATE));
		errorCheck();
	}

	/** 
	 * @return the current morpher waveform. 
	 */
	public final WaveForm getWaveform()
	{
		return waveForm;
	}

	/** 
	 * Set morpher waveform. 
	 * @param waveform the waveform type.
	 */
	public final void setWaveform(WaveForm waveform)
	{
		this.waveForm = waveform;
		// AL_VOCAL_MORPHER_WAVEFORM was not in LWJGL.
		EXTEfx.alEffecti(getALId(), 5 /* AL_VOCAL_MORPHER_WAVEFORM */, waveform.alVal);
		errorCheck();
	}
	
}
