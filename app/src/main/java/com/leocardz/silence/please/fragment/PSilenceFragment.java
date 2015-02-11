package com.leocardz.silence.please.fragment;

import java.io.File;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.devspark.appmsg.AppMsg;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphView.GraphViewData;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.LineGraphView;
import com.leocardz.silence.please.R;
import com.leocardz.silence.please.custom.SPButton;
import com.leocardz.silence.please.custom.SPTextView;
import com.leocardz.silence.please.dialog.LightThemeActivity;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.CountDownTimer;
import com.leocardz.silence.please.utils.Manager;
import com.leocardz.silence.please.utils.Medias;

public class PSilenceFragment extends SherlockFragment {

	private static Manager manager = Manager.getInstance();

	public PSilenceFragment() {
		pSilenceFragment = this;
	}

	private static PSilenceFragment pSilenceFragment;

	private SPTextView soundCountdownTextView, soundDurationTextView,
			limitDecibelsTextView, currentDecibelsTextView;
	public static SPButton listenButton;
	private ImageButton graphTable;
	private boolean graphButtonImage;
	private SPButton stopNoiseButton;
	private MediaPlayer m;
	private LinearLayout listenWrap;
	private static RelativeLayout noiseWrap;
	private Animation fadeInListen, fadeOutListen, fadeInNoise, fadeOutNoise;

	private RelativeLayout secondWrap, thirdWrap, fourthWrap, fifthWrap,
			sixthWrap, seventhWrap, eighthWrap, ninethWrap, tenthWrap,
			eleventhWrap;

	private String[] sounds, soundsRaw;

	private int limitDecibels, soundCountdown, soundDuration;
	private double decibelsRate;

	private static MediaRecorder mRecorder;
	private double mEMA = 0.0;
	private MenuItem menuSettings;

	private SoundDuration soundDurationCountdown;

	private Runnable updater;
	private final Handler mHandler = new Handler();
	private final int HANDLER_TICK = 100;

	private static SharedPreferences settings;
	private static String imagePref, soundPref;
	private DisplayMetrics displaymetrics;
	private int height;
	private int width;
	private View rootView;
	public boolean isListen = false;

	public static boolean onCountDown = false;

	private GraphView graphView;
	private GraphViewSeries exampleSeries;
	private double graphLastXValue = 5D;
	private LinearLayout graphLayout, tableLayout;

	private final int[] DECIBEL_RANGES = { 140, 125, 110, 100, 85, 70, 60, 40,
			30, 20 };

	private int returnListViewSelectedPosition() {

		int pos = 0;

		if (decibelsRate >= DECIBEL_RANGES[pos])
			return pos;

		pos++;

		for (int i = 1; i < DECIBEL_RANGES.length - 1; i++) {
			if (decibelsRate < DECIBEL_RANGES[i]
					&& decibelsRate >= DECIBEL_RANGES[i + 1])
				return pos;
			pos++;
		}

		return pos;

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.silence_layout, container,
				false);

		pSilenceFragment.rootView = rootView;

		settings = getActivity().getApplicationContext().getSharedPreferences(
				Constants.PREFS_NAME, 0);

		listenWrap = (LinearLayout) pSilenceFragment.rootView
				.findViewById(R.id.listen_wrap);
		noiseWrap = (RelativeLayout) pSilenceFragment.rootView
				.findViewById(R.id.noise_wrap);

		limitDecibelsTextView = (SPTextView) rootView
				.findViewById(R.id.limit_decibels);

		currentDecibelsTextView = (SPTextView) rootView
				.findViewById(R.id.current_decibels);
		currentDecibelsTextView.setText(String.valueOf(Constants.DB_INITIAL));

		soundCountdownTextView = (SPTextView) rootView
				.findViewById(R.id.edge_countdown);
		soundDurationTextView = (SPTextView) rootView
				.findViewById(R.id.edge_duration);
		updateSoundDecibels();
		updateSoundCountdown();
		updateSoundDuration();

		graphLayout = (LinearLayout) pSilenceFragment.rootView
				.findViewById(R.id.graph);
		tableLayout = (LinearLayout) pSilenceFragment.rootView
				.findViewById(R.id.table);

		graphTable = (ImageButton) pSilenceFragment.rootView
				.findViewById(R.id.graph_table);
		graphButtonImage = settings.getBoolean("graph", false);
		setGraphButtonImage(graphButtonImage);

		graphTable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				graphButtonImage = !graphButtonImage;
				setGraphButtonImage(graphButtonImage);
			}
		});

		listenButton = (SPButton) pSilenceFragment.rootView
				.findViewById(R.id.listen);
		listenButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				buttonClicked();
			}

		});

		initAnimations(rootView);
		initTable(rootView);

		sounds = getResources().getStringArray(R.array.sounds_array);
		soundsRaw = getResources().getStringArray(R.array.sounds_array_format);

		return rootView;
	}

	private void initTable(View rootView) {
		secondWrap = (RelativeLayout) rootView.findViewById(R.id.second_wrap);
		thirdWrap = (RelativeLayout) rootView.findViewById(R.id.third_wrap);
		fourthWrap = (RelativeLayout) rootView.findViewById(R.id.fourth_wrap);
		fifthWrap = (RelativeLayout) rootView.findViewById(R.id.fifth_wrap);
		sixthWrap = (RelativeLayout) rootView.findViewById(R.id.sixth_wrap);
		seventhWrap = (RelativeLayout) rootView.findViewById(R.id.seventh_wrap);
		eighthWrap = (RelativeLayout) rootView.findViewById(R.id.eighth_wrap);
		ninethWrap = (RelativeLayout) rootView.findViewById(R.id.nineth_wrap);
		tenthWrap = (RelativeLayout) rootView.findViewById(R.id.tenth_wrap);
		eleventhWrap = (RelativeLayout) rootView
				.findViewById(R.id.eleventh_wrap);
	}

	public void setGraphButtonImage(boolean graphButtonImage) {

		if (graphButtonImage) {
			graphTable.setImageResource(R.drawable.table);
			graphLayout.setVisibility(View.VISIBLE);
			tableLayout.setVisibility(View.GONE);
		} else {
			graphTable.setImageResource(R.drawable.graph);
			tableLayout.setVisibility(View.VISIBLE);
			graphLayout.setVisibility(View.GONE);
		}

		settings.edit().putBoolean("graph", graphButtonImage).commit();
	}

	private void buttonClicked() {
		isListen = !isListen;
		if (isListen) {
			startListen();
			listenButton.setText(R.string.stop_listen);
		} else {
			decibelsRate = 0.0;
			updateTable();
			stopListen();
			listenButton.setText(R.string.start_listen);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		displaymetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

		exampleSeries = new GraphViewSeries(
				new GraphViewData[] { new GraphViewData(.0d, .0d) });

		graphView = new LineGraphView(getActivity(), getString(R.string.empty));

		graphView.addSeries(exampleSeries);
		graphView.setLimit(limitDecibels);

		graphView.setViewPort(0, 150);
		graphView.setManualYAxisBounds(150, 0);
		graphView.setScalable(true);
		graphView.getGraphViewStyle().setHorizontalLabelsColor(
				getResources().getColor(R.color.blue_bar));
		graphView.getGraphViewStyle().setHorizontalLabelsColor(
				getResources().getColor(android.R.color.transparent));
		graphView.getGraphViewStyle().setVerticalLabelsColor(Color.BLACK);

		graphLayout.addView(graphView);

		isListen = false;
		buttonClicked();

		super.onActivityCreated(savedInstanceState);
	}

	public static void setImage() {
		settings = manager.getContext().getSharedPreferences(
				Constants.PREFS_NAME, 0);

		imagePref = settings.getString("image", "nurse");
		ImageView myImage = (ImageView) pSilenceFragment.rootView
				.findViewById(R.id.image);
		if (!imagePref.equals("nurse") && !imagePref.equals("")) {
			File imgFile = new File(imagePref);
			if (imgFile.exists()) {
				Bitmap myBitmap = Medias.decodeFile(imgFile,
						pSilenceFragment.width, pSilenceFragment.height);
				if (myBitmap != null && myImage != null) {
					myImage.setImageBitmap(myBitmap);
					settings.edit().putString("image", imagePref).commit();
				}
			}
		} else {
			myImage.setImageResource(R.drawable.silence);
		}
	}

	private void descendingDecibels() {
		currentDecibelsTextView.setText(String.valueOf(Constants.DB_INITIAL));
	}

	private void startListen() {
		startRecorder();
	}

	public void stopListen() {
		stopNuke();
		descendingDecibels();
		stopRecorder();
	}

	public void startRecorder() {
		if (mRecorder == null) {
			mRecorder = new MediaRecorder();
			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mRecorder.setOutputFile("/dev/null");

			try {
				mRecorder.prepare();
			} catch (java.io.IOException ioe) {
				AppMsg.makeText(getActivity(), R.string.mic_error,
						AppMsg.STYLE_ALERT).show();

			} catch (SecurityException e) {
				AppMsg.makeText(getActivity(), R.string.mic_error,
						AppMsg.STYLE_ALERT).show();
			}
			try {
				mRecorder.start();
			} catch (SecurityException e) {
				AppMsg.makeText(getActivity(), R.string.mic_error,
						AppMsg.STYLE_ALERT).show();
			}

		}
	}

	public void stopRecorder() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.release();
			mRecorder = null;
		}
	}

	private void initAnimations(View rootView) {
		stopNoiseButton = (SPButton) pSilenceFragment.rootView
				.findViewById(R.id.stop_noise);
		stopNoiseButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (soundDuration != -1)
					soundDurationCountdown.cancel();
				stopNuke();
			}
		});

		fadeInListen = new AlphaAnimation(0f, 1f);
		fadeOutListen = new AlphaAnimation(1f, 0f);
		fadeOutListen.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				listenWrap.setVisibility(View.GONE);
				noiseWrap.setVisibility(View.VISIBLE);
				menuSettings.setVisible(false);
				noiseWrap.startAnimation(fadeInNoise);
				updateSoundCountdown();
			}
		});

		fadeInNoise = new AlphaAnimation(0f, 1f);
		fadeOutNoise = new AlphaAnimation(1f, 0f);
		fadeOutNoise.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				noiseWrap.setVisibility(View.GONE);
				menuSettings.setVisible(true);
				listenWrap.setVisibility(View.VISIBLE);
				listenWrap.startAnimation(fadeInListen);
				updateSoundDuration();
				buttonClicked();
			}
		});

		fadeInListen.setDuration(Constants.ANIMATION_DURATION);
		fadeOutListen.setDuration(Constants.ANIMATION_DURATION);
		fadeInNoise.setDuration(Constants.ANIMATION_DURATION);
		fadeOutNoise.setDuration(Constants.ANIMATION_DURATION);
	}

	private void playNuke() {
		new SoundCountdown(soundCountdown * 1000 + 2000, 1000).start();
	}

	private void startNuke() {
		listenWrap.startAnimation(fadeOutListen);
		buttonClicked();
		try {
			soundPref = settings.getString("sound", "Nuke");

			if (!soundPref.equals("")) {

				stopPlaying();

				m = new MediaPlayer();

				if (!manager.arrayContainsString(
						getResources().getStringArray(R.array.sounds_array),
						soundPref)) {
					Uri uri = Uri.parse(soundPref);
					m.setDataSource(getActivity(), uri);
				} else {
					int i = 0;
					for (i = 0; i < sounds.length; i++) {
						if (sounds[i].equals(soundPref))
							break;
					}

					AssetFileDescriptor descriptor = getActivity().getAssets()
							.openFd(soundsRaw[i]);
					m.setDataSource(descriptor.getFileDescriptor(),
							descriptor.getStartOffset(), descriptor.getLength());
					descriptor.close();
				}

				boolean repeat = settings.getBoolean("repeat", true);

				m.prepare();
				m.setVolume(1f, 1f);
				m.setLooping(repeat);
				m.start();

				if (repeat == false) {
					m.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							stopNuke();
						}
					});
				}
			}
		} catch (Exception e) {
		}

		if (soundDuration != -1) {
			soundDurationCountdown = new SoundDuration(
					soundDuration * 1000 + 2000, 1000);
			soundDurationCountdown.start();
		}

	}

	private class SoundDuration extends CountDownTimer {

		public SoundDuration(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			stopNuke();
			updateSoundCountdown();
		}

		@Override
		public void onTick(long millisUntilFinished) {
			millisUntilFinished -= 2000;
			Double tick = (double) millisUntilFinished / (double) 1000;
			tick = Math.ceil(tick);
			soundDurationTextView.setText(String.valueOf(tick.intValue()));
		}
	}

	private class SoundCountdown extends CountDownTimer {

		public SoundCountdown(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
			onCountDown = true;
		}

		@Override
		public void onFinish() {
			onCountDown = false;
		}

		private void finish() {
			updateSoundCountdown();
			this.cancel();
			onCountDown = false;
		}

		@Override
		public void onTick(long millisUntilFinished) {
			if (!manager.isInFront())
				finish();

			if (isListen) {
				millisUntilFinished -= 2000;
				if (millisUntilFinished <= 0) {
					soundCountdownTextView.setText("0");
					startNuke();
					finish();
				} else if (decibelsRate > limitDecibels) {
					Double tick = (double) millisUntilFinished / (double) 1000;
					tick = Math.ceil(tick);
					soundCountdownTextView.setText(String.valueOf(tick
							.intValue()));
				} else {
					finish();
				}
			} else
				finish();

		}
	}

	public static void stopNuke() {
		if (noiseWrap.getVisibility() == View.VISIBLE)
			noiseWrap.startAnimation(pSilenceFragment.fadeOutNoise);
		stopPlaying();
	}

	private static void stopPlaying() {
		try {
			if (pSilenceFragment.m != null) {
				pSilenceFragment.m.stop();
				pSilenceFragment.m.release();
			}
		} catch (Exception ex) {
		}
	}

	private void updateDecibels() {
		decibelsRate = soundDb();

		if (decibelsRate > limitDecibels && !onCountDown) {
			playNuke();
		} else if (decibelsRate > 0.0) {
			currentDecibelsTextView.setText(String.valueOf((int) decibelsRate));
		} else {
			descendingDecibels();
		}
	}

	@SuppressWarnings("deprecation")
	private void updateTable() {
		int pos = returnListViewSelectedPosition();

		secondWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		thirdWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		fourthWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		fifthWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		sixthWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		seventhWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		eighthWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		ninethWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		tenthWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));
		eleventhWrap.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.p_background_transparent));

		if (decibelsRate == 0.0)
			pos = -1;

		if (pos == 0)
			secondWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_second));
		else if (pos == 1)
			thirdWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_third));
		else if (pos == 2)
			fourthWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_fourth));
		else if (pos == 3)
			fifthWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_fifth));
		else if (pos == 4)
			sixthWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_sixth));
		else if (pos == 5)
			seventhWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_seventh));
		else if (pos == 6)
			eighthWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_eighth));
		else if (pos == 7)
			ninethWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_nineth));
		else if (pos == 8)
			tenthWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_tenth));
		else if (pos == 9)
			eleventhWrap.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.p_background_eleventh));

	}

	private void updateGraph() {
		graphLastXValue += 1d;
		exampleSeries.appendData(new GraphViewData(graphLastXValue,
				decibelsRate), true);
	}

	public void updateSoundDecibels() {
		limitDecibels = settings.getInt("decibels", 80);
		limitDecibelsTextView.setText(String.valueOf(limitDecibels));
		if (graphView != null)
			graphView.setLimit(limitDecibels);
	}

	public void updateSoundCountdown() {
		soundCountdown = settings.getInt("countdown", 10);
		soundCountdownTextView.setText(String.valueOf(soundCountdown));
	}

	public void updateSoundDuration() {
		soundDuration = settings.getInt("duration", -1);
		soundDurationTextView.setText(soundDuration == -1 ? "∞" : String
				.valueOf(soundDuration));
	}

	public double soundDb() {
		return 20 * Math.log10(getAmplitudeEMA() / Constants.AMP);
	}

	public double getAmplitude() {
		if (mRecorder != null)
			return (mRecorder.getMaxAmplitude());
		else
			return 0;

	}

	public double getAmplitudeEMA() {
		double amp = getAmplitude();
		mEMA = Constants.EMA_FILTER * amp + (1.0 - Constants.EMA_FILTER) * mEMA;
		return mEMA;
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menuSettings = menu.findItem(R.id.menu_settings);
		menu.findItem(R.id.repeat_sound).setChecked(
				getActivity().getApplicationContext()
						.getSharedPreferences(Constants.PREFS_NAME, 0)
						.getBoolean("repeat", true));
		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent;
		switch (item.getItemId()) {
		case R.id.max_decibel_level:
			intent = new Intent(getActivity(), LightThemeActivity.class);
			intent.putExtra("title", getString(R.string.max_decibel_level)
					+ " (" + getString(R.string.db) + ")");
			intent.putExtra("min", 0);
			intent.putExtra("max", 150);
			intent.putExtra("current", settings.getInt("decibels", 80));
			intent.putExtra("type", Constants.SOUND_DECIBEL);
			startActivity(intent);
			break;
		case R.id.sound_countdown:
			intent = new Intent(getActivity(), LightThemeActivity.class);
			intent.putExtra("title", getString(R.string.sound_countdown) + " ("
					+ getString(R.string.sec) + ")");
			intent.putExtra("min", 1);
			intent.putExtra("max", 60);
			intent.putExtra("current", settings.getInt("countdown", 10));
			intent.putExtra("type", Constants.SOUND_COUNTDOWN);
			startActivity(intent);
			break;
		case R.id.sound_duration:
			intent = new Intent(getActivity(), LightThemeActivity.class);
			intent.putExtra("title", getString(R.string.sound_duration) + " ("
					+ getString(R.string.sec) + ")");
			intent.putExtra("min", 1);
			intent.putExtra("max", 60);
			intent.putExtra("current", settings.getInt("duration", -1));
			intent.putExtra("type", Constants.SOUND_DURATION);
			startActivity(intent);
			break;
		case R.id.repeat_sound:
			settings.edit().putBoolean("repeat", !item.isChecked()).commit();
			item.setChecked(!item.isChecked());
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		manager.setInFront(false);

		stopListen();

		mHandler.removeCallbacks(updater);

		super.onPause();
	}

	@Override
	public void onStop() {
		manager.setInFront(false);

		stopNuke();
		super.onStop();
	}

	@Override
	public void onResume() {

		manager.setInFront(true);

		updater = new Runnable() {
			@Override
			public void run() {
				if (isListen) {
					updateDecibels();
					updateGraph();
					updateTable();
				}
				mHandler.postDelayed(this, HANDLER_TICK);
			}
		};
		mHandler.postDelayed(updater, HANDLER_TICK);

		if (isListen) {
			startListen();
		}

		setImage();
		updateSoundDecibels();
		updateSoundCountdown();
		updateSoundDuration();

		super.onResume();
	}

	@Override
	public void onDestroy() {
		stopNuke();
		super.onDestroy();
	}

}
