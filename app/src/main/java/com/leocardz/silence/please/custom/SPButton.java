package com.leocardz.silence.please.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.leocardz.silence.please.custom.typeface.OpenSans;

public class SPButton extends Button {

	public SPButton(Context context) {
		super(context);
		setTypeface(OpenSans.getInstance().getTypeFace());
	}

	public SPButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setTypeface(OpenSans.getInstance().getTypeFace());
	}

	public SPButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setTypeface(OpenSans.getInstance().getTypeFace());
	}

}
