package com.leocardz.silence.please.dialog;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.leocardz.silence.please.fragment.PSilenceFragment;
import com.leocardz.silence.please.utils.Constants;

import net.simonvt.numberpicker.NumberPicker;

public class LightThemeActivity extends Activity {
	private int type;

	private static SharedPreferences settings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PSilenceFragment.onCountDown = true;

		settings = getSharedPreferences(Constants.PREFS_NAME, 0);

		setContentView(R.layout.dialog_light);

		Bundle bundle = getIntent().getExtras();

		setTitle(bundle.getString("title"));

		final NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);

		np.setMaxValue(bundle.getInt("max"));
		np.setMinValue(bundle.getInt("min"));

		if (bundle.getInt("current") != -1)
			np.setValue(bundle.getInt("current"));

		type = bundle.getInt("type");

		np.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

		if (type == Constants.SOUND_DECIBEL) {
			Button ok = (Button) findViewById(R.id.ok_button_full);
			ok.setVisibility(View.VISIBLE);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					settings.edit().putInt("decibels", np.getValue()).commit();
					finish();
				}
			});
		} else if (type == Constants.SOUND_COUNTDOWN) {
			Button immediately = (Button) findViewById(R.id.immediately_button);
			immediately.setVisibility(View.VISIBLE);
			immediately.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					settings.edit().putInt("countdown", 0).commit();
					finish();
				}
			});

			Button ok = (Button) findViewById(R.id.ok_button);
			ok.setVisibility(View.VISIBLE);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					settings.edit().putInt("countdown", np.getValue()).commit();
					finish();
				}
			});
		} else {
			Button illimited = (Button) findViewById(R.id.illimited_button);
			illimited.setVisibility(View.VISIBLE);
			illimited.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					settings.edit().putInt("duration", -1).commit();
					finish();
				}
			});

			Button ok = (Button) findViewById(R.id.ok_button);
			ok.setVisibility(View.VISIBLE);
			ok.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					settings.edit().putInt("duration", np.getValue()).commit();
					finish();
				}
			});
		}
	}

	@Override
	public void finish() {
		PSilenceFragment.onCountDown = false;
		super.finish();
	}
}
