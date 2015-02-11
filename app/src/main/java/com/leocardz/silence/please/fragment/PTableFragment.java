package com.leocardz.silence.please.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.leocardz.silence.please.R;
import com.leocardz.silence.please.adapter.PTableArrayAdapter;
import com.leocardz.silence.please.adapter.item.PTableAdapterItem;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.Manager;

public class PTableFragment extends SherlockFragment {
	private Manager manager = Manager.getInstance();

	private PTableAdapterItem[] tableItems;
	public int tableCounter = 0;
	public Parcelable tableListState;

	private ListView listView;

	public PTableFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.p_settings_options,
				container, false);

		buildTableList();
		listView = (ListView) rootView.findViewById(R.id.settings_list_view);
		listView.setAdapter(manager.getTableListAdapter());
		listView.setClickable(false);

		rootView.bringToFront();
		return rootView;
	}

	private void buildTableList() {
		if (manager.getTableListAdapter() == null) {
			tableItems = new PTableAdapterItem[31];

			int position = 0;

			addNewItem(R.string.max_exposure_day, R.string.empty,
					Constants.SETTINGS_CATEGORY, position++);
			addNewItem(R.string.max_exposure_day_1, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_2, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_3, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_4, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_5, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_6, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_7, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_8, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);
			addNewItem(R.string.max_exposure_day_9, R.string.empty,
					Constants.SETTINGS_SIMPLE_CENTER, position++);

			addNewItem(R.string.sound_source_decibels, R.string.empty,
					Constants.SETTINGS_CATEGORY, position++);
			addNewItem(R.string.first, R.string.first_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.second, R.string.second_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.third, R.string.third_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.fourth, R.string.fourth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.fifth, R.string.fifth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.sixth, R.string.sixth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.seventh, R.string.seventh_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.eighth, R.string.eighth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.nineth, R.string.nineth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.tenth, R.string.tenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.eleventh, R.string.eleventh_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.twelveth, R.string.twelveth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.thirteenth, R.string.thirteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.fourteenth, R.string.fourteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.fifteenth, R.string.fifteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.sixteenth, R.string.sixteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.seventeenth, R.string.seventeenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.eighteenth, R.string.eighteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.nineteenth, R.string.nineteenth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);
			addNewItem(R.string.twentieth, R.string.twentieth_decibels,
					Constants.SETTINGS_SUMMARY_CENTER, position++);

			manager.setTableListAdapter(new PTableArrayAdapter(manager
					.getContext(), R.layout.p_settings_simple, tableItems));
		}
	}

	private void addNewItem(int title, int summary, int type, int position) {
		addNewItem(title, getString(summary), type, position);
	}

	private void addNewItem(int title, String summary, int type, int position) {
		tableItems[position] = new PTableAdapterItem(title, summary, type);
	}
}
