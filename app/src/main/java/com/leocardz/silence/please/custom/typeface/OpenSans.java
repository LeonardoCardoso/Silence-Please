package com.leocardz.silence.please.custom.typeface;

import android.graphics.Typeface;

import com.leocardz.silence.please.utils.Manager;

public class OpenSans {

	private static OpenSans instance;

	public OpenSans() {
	}

	public static OpenSans getInstance() {
		synchronized (OpenSans.class) {
			if (instance == null)
				instance = new OpenSans();
			return instance;
		}
	}

	public Typeface getTypeFace() {
		return Typeface.createFromAsset((Manager.getInstance().getContext()
				.getApplicationContext()).getResources().getAssets(),
				"open_sans_light.ttf");
	}
}
