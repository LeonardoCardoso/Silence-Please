package com.leocardz.silence.please.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.actionbarsherlock.app.SherlockFragment;
import com.leocardz.silence.please.R;
import com.leocardz.silence.please.SilencePlease;
import com.leocardz.silence.please.custom.SPViewPager;
import com.leocardz.silence.please.utils.Manager;

public class PSettingsFragment extends SherlockFragment {

	private Manager manager = Manager.getInstance();

	public PSettingsFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.p_view_pager_template,
				container, false);

		manager.setSettingsFragmentViewPager((SPViewPager) rootView
				.findViewById(R.id.pager));
		manager.getSettingsFragmentViewPager().setScrollDurationFactor(2);
		manager.getSettingsFragmentViewPager().setPagingEnabled(false);
		manager.getSettingsFragmentViewPager().setAdapter(
				new SectionsPagerAdapter(getChildFragmentManager()));

		manager.getSettingsFragmentViewPager().setOnPageChangeListener(
				new OnPageChangeListener() {

					@Override
					public void onPageSelected(int pos) {
						if (pos == 0)
							SilencePlease.updateTitle(R.string.settings);
						else if (pos == 1)
							SilencePlease.updateTitle(R.string.concept);
					}

					@Override
					public void onPageScrolled(int arg0, float arg1, int arg2) {
					}

					@Override
					public void onPageScrollStateChanged(int arg0) {
					}
				});

		rootView.bringToFront();
		return rootView;
	}

	public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			SherlockFragment fragment = null;

			if (position == 0) {
				fragment = new PSettingsOptionsFragment();
			} else if (position == 1) {
				fragment = new PAboutFragment();
			}

			return fragment;
		}

		@Override
		public int getCount() {
			return 2;
		}
	}
}
