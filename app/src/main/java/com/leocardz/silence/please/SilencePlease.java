package com.leocardz.silence.please;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.leocardz.silence.please.adapter.PNavigationListArrayAdapter;
import com.leocardz.silence.please.adapter.item.PNavigationListAdapterItem;
import com.leocardz.silence.please.fragment.PSettingsFragment;
import com.leocardz.silence.please.fragment.PSettingsOptionsFragment;
import com.leocardz.silence.please.fragment.PSilenceFragment;
import com.leocardz.silence.please.fragment.PTableFragment;
import com.leocardz.silence.please.utils.Manager;

public class SilencePlease extends SherlockFragmentActivity {

	private static Manager manager = Manager.getInstance();

	private ActionBarDrawerToggle mDrawerToggle;

	private PNavigationListAdapterItem[] mFragmentTitles;
	private String[] navigationListItems, navigationLabels;

	private SherlockFragment[] sherlockFragments = { new PSilenceFragment(),
			new PTableFragment(), new PSettingsFragment() };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		manager.setContext(SilencePlease.this);
		manager.setAb(getSupportActionBar());
		manager.setAbs(getSherlock());

		setContentView(R.layout.main);

		navigationListItems = getResources().getStringArray(R.array.list_array);
		navigationLabels = getResources().getStringArray(R.array.labels_array);
		mFragmentTitles = new PNavigationListAdapterItem[navigationListItems.length];

		for (int i = 0; i < navigationListItems.length; i++)
			mFragmentTitles[i] = new PNavigationListAdapterItem(
					navigationListItems[i], View.INVISIBLE);

		mFragmentTitles[0].setSelectedVisibility(View.VISIBLE);

		manager.setDrawerLayout((DrawerLayout) findViewById(R.id.drawer_layout));
		manager.setDrawerList((ListView) findViewById(R.id.left_drawer));

		manager.getDrawerLayout().setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		manager.getDrawerList().setAdapter(
				new PNavigationListArrayAdapter(manager.getContext(),
						R.layout.p_drawer_list_item, mFragmentTitles));
		manager.getDrawerList().setOnItemClickListener(
				new DrawerItemClickListener());

		manager.getAb().setDisplayHomeAsUpEnabled(true);
		manager.getAb().setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(this,
				manager.getDrawerLayout(), R.drawable.ic_drawer,
				R.string.drawer_open, R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				manager.getAb().setTitle(
						navigationLabels[manager.getCurrentPosition()]);
				supportInvalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getSupportActionBar().setTitle(R.string.app);
				supportInvalidateOptionsMenu();
			}
		};

		manager.getDrawerLayout().setDrawerListener(mDrawerToggle);

		selectItem(0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {

		boolean drawerOpen = manager.getDrawerLayout().isDrawerOpen(
				manager.getDrawerList());

		MenuItem item = menu.findItem(R.id.menu_settings);
		item.setVisible(!drawerOpen && manager.getCurrentPosition() == 0);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_silence_please, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			PSilenceFragment.stopNuke();

			if (manager.getSettingsFragmentViewPager() != null
					&& manager.getSettingsFragmentViewPager().getCurrentItem() == 1) {
				manager.getSettingsFragmentViewPager().setCurrentItem(0);
			} else {
				if (!manager.getDrawerLayout().isDrawerOpen(
						manager.getDrawerList()))
					manager.getDrawerLayout().openDrawer(
							manager.getDrawerList());
				else
					manager.getDrawerLayout().closeDrawer(
							manager.getDrawerList());
			}
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK)
			finish();
		return super.onKeyDown(keyCode, event);
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {

		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();

		SherlockFragment fragment = sherlockFragments[position];

		if (fragment.isAdded())
			fragmentTransaction.remove(fragment);
		
		fragmentTransaction.add(R.id.main_empty, fragment);

		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();

		if (position != 0) {
			PSilenceFragment.listenButton.performClick();
		}

		if (manager.getCurrentPosition() != position) {

			for (int i = 0; i < mFragmentTitles.length; i++)
				mFragmentTitles[i].setSelectedVisibility(View.INVISIBLE);

			mFragmentTitles[position].setSelectedVisibility(View.VISIBLE);

			manager.setCurrentPosition(position);

			manager.getDrawerLayout().closeDrawer(manager.getDrawerList());

			manager.getDrawerList().setItemChecked(position, true);
			SilencePlease.updateTitle(navigationLabels[position]);

		} else
			manager.getDrawerLayout().closeDrawer(manager.getDrawerList());
	}

	public static void updateTitle(int resId) {
		updateTitle(manager.getContext().getString(resId));
	}

	public static void updateTitle(String resId) {
		manager.getAb().setTitle(resId);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		PSettingsOptionsFragment.result(requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		PSettingsOptionsFragment.keyDown(keyCode, event);
		return super.onKeyDown(keyCode, event);
	}
}
