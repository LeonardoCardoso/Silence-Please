package com.leocardz.silence.please;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class AppInit extends SherlockFragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		startActivity(new Intent(this, SilencePlease.class));
		finish();
	}

}