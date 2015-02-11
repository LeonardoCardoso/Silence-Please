package com.leocardz.silence.please.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.leocardz.silence.please.R;

public class PAboutFragment extends SherlockFragment {

	public PAboutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.p_about, container, false);

		rootView.bringToFront();
		return rootView;
	}

}
