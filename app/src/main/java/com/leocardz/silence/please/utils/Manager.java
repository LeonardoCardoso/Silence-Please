package com.leocardz.silence.please.utils;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.widget.ListView;

import com.actionbarsherlock.ActionBarSherlock;
import com.actionbarsherlock.app.ActionBar;
import com.leocardz.silence.please.adapter.PSettingsArrayAdapter;
import com.leocardz.silence.please.adapter.PTableArrayAdapter;
import com.leocardz.silence.please.custom.SPViewPager;

public class Manager {

	private static Manager instance = null;

	public static Manager getInstance() {
		synchronized (Manager.class) {
			if (instance == null)
				instance = new Manager();
			return instance;
		}
	}

	private boolean isInFront = false;
	private PSettingsArrayAdapter settingsListAdapter = null;
	private PTableArrayAdapter tableListAdapter = null;
	private int navigationScrolled = -1;
	private int settingsScrolled = -1;
	private int tableScrolled = -1;
	private Context context;

	private ActionBar ab;
	private ActionBarSherlock abs;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private int currentPosition = -1;

	private SPViewPager settingsFragmentViewPager;

	public int getNavigationScrolled() {
		return navigationScrolled;
	}

	public void setNavigationScrolled(int navigationScrolled) {
		this.navigationScrolled = navigationScrolled;
	}

	public Context getContext() {
		return context;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public ActionBar getAb() {
		return ab;
	}

	public void setAb(ActionBar ab) {
		this.ab = ab;
	}

	public ActionBarSherlock getAbs() {
		return abs;
	}

	public void setAbs(ActionBarSherlock abs) {
		this.abs = abs;
	}

	public DrawerLayout getDrawerLayout() {
		return mDrawerLayout;
	}

	public void setDrawerLayout(DrawerLayout mDrawerLayout) {
		this.mDrawerLayout = mDrawerLayout;
	}

	public ListView getDrawerList() {
		return mDrawerList;
	}

	public void setDrawerList(ListView mDrawerList) {
		this.mDrawerList = mDrawerList;
	}

	public int getCurrentPosition() {
		return currentPosition;
	}

	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}

	public int getSettingsScrolled() {
		return settingsScrolled;
	}

	public void setSettingsScrolled(int settingsScrolled) {
		this.settingsScrolled = settingsScrolled;
	}

	public PSettingsArrayAdapter getSettingsListAdapter() {
		return settingsListAdapter;
	}

	public void setSettingsListAdapter(PSettingsArrayAdapter settingsListAdapter) {
		this.settingsListAdapter = settingsListAdapter;
	}

	public PTableArrayAdapter getTableListAdapter() {
		return tableListAdapter;
	}

	public void setTableListAdapter(PTableArrayAdapter tableListAdapter) {
		this.tableListAdapter = tableListAdapter;
	}

	public SPViewPager getSettingsFragmentViewPager() {
		return settingsFragmentViewPager;
	}

	public void setSettingsFragmentViewPager(
			SPViewPager settingsFragmentViewPager) {
		this.settingsFragmentViewPager = settingsFragmentViewPager;
	}

	public int getTableScrolled() {
		return tableScrolled;
	}

	public void setTableScrolled(int tableScrolled) {
		this.tableScrolled = tableScrolled;
	}

	public boolean arrayContainsString(String[] set, String item) {

		for (String string : set)
			if (item.equals(string))
				return true;

		return false;
	}

	public boolean isInFront() {
		return isInFront;
	}

	public void setInFront(boolean isInFront) {
		this.isInFront = isInFront;
	}
}
