package com.leocardz.silence.please.custom;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class SPScroller extends Scroller {

	private double mScrollFactor = 1;

	public SPScroller(Context context) {
		super(context);
	}

	public SPScroller(Context context, Interpolator interpolator) {
		super(context, interpolator);
	}

	public void setScrollDurationFactor(double scrollFactor) {
		mScrollFactor = scrollFactor;
	}

	@Override
	public void startScroll(int startX, int startY, int dx, int dy, int duration) {
		super.startScroll(startX, startY, dx, dy,
				(int) (duration * mScrollFactor));
	}

}
