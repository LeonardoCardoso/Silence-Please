package com.leocardz.silence.please.fragment;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.leocardz.silence.please.ImageSelection;
import com.leocardz.silence.please.R;
import com.leocardz.silence.please.adapter.PSettingsArrayAdapter;
import com.leocardz.silence.please.adapter.item.PSettingsAdapterItem;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.Manager;

public class PSettingsOptionsFragment extends SherlockFragment {

	private Manager manager = Manager.getInstance();

	private PSettingsAdapterItem[] settingsItems;
	public int settingsCounter = 0;
	public Parcelable settingsListState;

	private SharedPreferences settings;

	private String versionX = "";

	private ListView listView;

	private String initialSound, currentSound;
	private MediaPlayer m;
	private String[] sounds, soundsRaw;
	public View soundView;

	private static PSettingsOptionsFragment pSettingsOptionsFragment;

	public PSettingsOptionsFragment() {
		pSettingsOptionsFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.p_settings_options,
				container, false);

		settings = manager.getContext().getSharedPreferences(
				Constants.PREFS_NAME, 0);

		buildSettingsList();
		listView = (ListView) rootView.findViewById(R.id.settings_list_view);
		listView.setAdapter(manager.getSettingsListAdapter());
		listView.setOnItemClickListener(settingsListener);

		try {
			versionX = manager.getContext().getPackageManager()
					.getPackageInfo(manager.getContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}

		rootView.bringToFront();
		return rootView;
	}

	private OnItemClickListener settingsListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long id) {

			if (position == 1)
				startActivity(new Intent(getActivity(), ImageSelection.class));
			else if (position == 2)
				callPickerRingToneDialog();
			else if (position == 4)
				feedbackUs();
			else if (position == 5)
				shareUs();
			else if (position == 6)
				rateUs();
			else if (position == 7)
				manager.getSettingsFragmentViewPager().setCurrentItem(1);

		}
	};

	public void updateSummary(View view, int position, int newSummary) {
		updateSummary(view, position, getString(newSummary));
	}

	public void updateSummary(View view, int position, String newSummary) {
		Animation fadeOut = AnimationUtils.loadAnimation(manager.getContext(),
				R.anim.fade_out);
		Animation rightLeft = AnimationUtils.loadAnimation(
				manager.getContext(), R.anim.right_left_summary);

		TextView summary = (TextView) view.findViewById(R.id.summary);
		summary.startAnimation(fadeOut);
		summary.setText(newSummary);
		summary.startAnimation(rightLeft);

		PSettingsAdapterItem item = manager.getSettingsListAdapter().getItem(
				position);
		item.setSummary(newSummary);
	}

	private void feedbackUs() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("pain/text");
		String[] recipients = new String[] { getString(R.string.feedback_email) };
		intent.putExtra(Intent.EXTRA_EMAIL, recipients);
		intent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.feedback_subject));
		intent.putExtra(Intent.EXTRA_TEXT,
				"\n\n\nApp Version: " + versionX + "\nModel: "
						+ android.os.Build.MANUFACTURER + " "
						+ android.os.Build.DEVICE + "\nSO: "
						+ android.os.Build.VERSION.RELEASE + "\n");
		startActivity(Intent.createChooser(intent,
				getString(R.string.feedback_via)));
	}

	private void shareUs() {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.spread_subject));
		intent.putExtra(Intent.EXTRA_TEXT,
				getString(R.string.spread_text));
		startActivity(Intent.createChooser(intent,
				getString(R.string.share_via)));
	}

	private void rateUs() {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(Uri
				.parse("market://details?id=com.leocardz.silence.please"));
		startActivity(intent);
	}

	private void buildSettingsList() {
		if (manager.getSettingsListAdapter() == null) {
			settingsItems = new PSettingsAdapterItem[8];

			int position = 0;

			addNewItem(R.string.alert, R.string.empty,
					Constants.SETTINGS_CATEGORY, position++);
			addNewItem(R.string.image, R.string.empty,
					Constants.SETTINGS_SIMPLE, position++);
			addNewItem(R.string.sound, getSoundTitle(),
					Constants.SETTINGS_SUMMARY, position++);

			addNewItem(R.string.about, R.string.empty,
					Constants.SETTINGS_CATEGORY, position++);
			addNewItem(R.string.feedback_us, R.string.feedback_us_subtitle,
					Constants.SETTINGS_SUMMARY, position++);
			addNewItem(R.string.share, R.string.share_subtitle,
					Constants.SETTINGS_SUMMARY, position++);
			addNewItem(R.string.rate_us, R.string.empty,
					Constants.SETTINGS_SIMPLE, position++);
			addNewItem(R.string.concept, R.string.empty,
					Constants.SETTINGS_SIMPLE, position++);

			manager.setSettingsListAdapter(new PSettingsArrayAdapter(manager
					.getContext(), R.layout.p_settings_simple, settingsItems,
					pSettingsOptionsFragment));
		}
	}

	private String getSoundTitle() {

		Context context = manager.getContext();

		String soundPref = settings.getString("sound", "Nuke");

		if (sounds == null) {
			sounds = context.getResources()
					.getStringArray(R.array.sounds_array);
			soundsRaw = context.getResources().getStringArray(
					R.array.sounds_array_format);
		}

		if (!manager.arrayContainsString(sounds, soundPref)) {
			Uri ringtoneUri = Uri.parse(soundPref);
			Ringtone ringtone = RingtoneManager.getRingtone(context,
					ringtoneUri);
			soundPref = ringtone.getTitle(context);
		}

		return soundPref;
	}

	private void callPickerRingToneDialog() {
		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				getActivity());
		builderSingle.setTitle(getString(R.string.select_sound));
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
				getActivity(), android.R.layout.select_dialog_singlechoice);
		arrayAdapter.add(getString(R.string.sp_sounds));
		arrayAdapter.add(getString(R.string.system_sounds));
		arrayAdapter.add(getString(R.string.songs));
		builderSingle.setNegativeButton(getString(R.string.cancel_button),
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builderSingle.setAdapter(arrayAdapter,
				new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (which == 0) {
							callSpSoundsDialog();
						} else if (which == 1) {
							pickRingtone();
						} else {
							callSongs();
						}
					}
				});
		builderSingle.show();
	}

	private void callSpSoundsDialog() {
		initialSound = currentSound = settings.getString("sound", "Nuke");

		int checked = -1;
		for (int i = 0; i < sounds.length; i++) {
			if (initialSound.equals(sounds[i]))
				checked = i;
		}

		AlertDialog.Builder builderSingle = new AlertDialog.Builder(
				getActivity());
		builderSingle.setTitle(getString(R.string.sp_sounds));

		builderSingle
				.setSingleChoiceItems(sounds, checked, new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							if (m != null) {
								m.stop();
								m.release();
							}
						} catch (Exception ex) {
						}

						currentSound = sounds[which];
						String toPlay = soundsRaw[which];

						m = new MediaPlayer();
						AssetFileDescriptor descriptor;
						try {

							descriptor = getActivity().getAssets().openFd(
									toPlay);
							m.setDataSource(descriptor.getFileDescriptor(),
									descriptor.getStartOffset(),
									descriptor.getLength());
							descriptor.close();

							m.prepare();
							m.setVolume(1f, 1f);
							m.setLooping(false);
							m.start();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				})
				.setNegativeButton(getString(R.string.cancel_button),
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									if (m != null) {
										m.stop();
										m.release();
									}
								} catch (Exception ex) {
								}
								dialog.dismiss();
							}
						})
				.setPositiveButton(getString(R.string.ok),
						new OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								try {
									if (m != null) {
										m.stop();
										m.release();
									}
								} catch (Exception ex) {
								}

								initialSound = currentSound;

								settings.edit()
										.putString("sound", initialSound)
										.commit();

								updateSongItem();

							}
						}).setOnKeyListener(new OnKeyListener() {

					@Override
					public boolean onKey(DialogInterface dialog, int keyCode,
							KeyEvent event) {
						keyDown(keyCode, event);
						return false;
					}
				});

		builderSingle.show();
	}

	public static void keyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			try {
				if (pSettingsOptionsFragment.m != null) {
					pSettingsOptionsFragment.m.stop();
					pSettingsOptionsFragment.m.release();
				}
			} catch (Exception ex) {
			}
		}
	}

	private void pickRingtone() {
		Uri soundPref = Uri.parse(settings.getString("sound", "Nuke"));
		Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE,
				RingtoneManager.TYPE_NOTIFICATION);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE,
				getString(R.string.select_sound));
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, false);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, soundPref);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, soundPref);
		getActivity().startActivityForResult(intent, Constants.RINGTONE_CODE);
	}

	private void callSongs() {
		Uri soundPref = Uri.parse(settings.getString("sound", "Nuke"));
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("audio/*");
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, soundPref);
		intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, soundPref);
		Intent c = Intent.createChooser(intent,
				getString(R.string.select_sound));
		getActivity().startActivityForResult(c, Constants.SONGS_CODE);
	}

	public static void result(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case Constants.RINGTONE_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data
						.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null) {
					String ringTonePath = uri.toString();
					pSettingsOptionsFragment.settings.edit()
							.putString("sound", ringTonePath).commit();
				} else {
					pSettingsOptionsFragment.settings.edit()
							.putString("sound", "").commit();
				}
				pSettingsOptionsFragment.updateSongItem();
			}
			break;
		case Constants.SONGS_CODE:
			if (resultCode == Activity.RESULT_OK) {
				Uri uri = data.getData();
				if (uri != null) {
					String ringTonePath = uri.toString();
					pSettingsOptionsFragment.settings.edit()
							.putString("sound", ringTonePath).commit();
				} else {
					pSettingsOptionsFragment.settings.edit()
							.putString("sound", "").commit();
				}
				pSettingsOptionsFragment.updateSongItem();
			}
			break;
		default:
			break;
		}
	}

	private void updateSongItem() {
		String sound = getSoundTitle();
		updateSummary(soundView, 2, sound);
	}

	private void addNewItem(int title, int summary, int type, int position) {
		addNewItem(title, getString(summary), type, position);
	}

	private void addNewItem(int title, String summary, int type, int position) {
		settingsItems[position] = new PSettingsAdapterItem(title, summary, type);
	}

}
