package com.leocardz.silence.please.utils;

import java.io.File;

import android.os.Environment;

public class Constants {

	public static final String PREFS_NAME = "SP";
	public static final String DB_INITIAL = "0";
	public static final int ANIMATION_DURATION = 500;
	public static final double AMP = 1.000;
	public static final double EMA_FILTER = 0.6;
	public static final int SETTINGS_CATEGORY = 0;
	public static final int SETTINGS_SIMPLE = 1;
	public static final int SETTINGS_SUMMARY = 2;
	public static final int SETTINGS_SIMPLE_CENTER = 3;
	public static final int SETTINGS_SUMMARY_CENTER = 4;
	public static final int RINGTONE_CODE = 0x131;
	public static final int SONGS_CODE = 0x132;
	public static final int SELECT_PICTURE = 0x12311;
	public static final int CROP_PICTURE = 0x12347;
	public static final int SOUND_DECIBEL = 0;
	public static final int SOUND_COUNTDOWN = 1;
	public static final int SOUND_DURATION = 2;

	public static final String ROOT_PICTURE_DIRECTORY = Environment
			.getExternalStorageDirectory() + File.separator + "Pictures";
	public static final String SP_PICTURE_DIRECTORY = ROOT_PICTURE_DIRECTORY
			+ File.separator + ".SP" + File.separator;

}
