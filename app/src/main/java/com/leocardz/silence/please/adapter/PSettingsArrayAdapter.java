package com.leocardz.silence.please.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leocardz.silence.please.R;
import com.leocardz.silence.please.adapter.item.PSettingsAdapterItem;
import com.leocardz.silence.please.adapter.view.holder.PSettingsViewHolder;
import com.leocardz.silence.please.custom.SPTextView;
import com.leocardz.silence.please.fragment.PSettingsOptionsFragment;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.Manager;

public class PSettingsArrayAdapter extends ArrayAdapter<PSettingsAdapterItem> {

	private Manager manager = Manager.getInstance();

	private PSettingsAdapterItem[] listItems;
	private Context context;
	private PSettingsOptionsFragment pSettingsOptionsFragment;

	public PSettingsArrayAdapter(Context context, int textViewResourceId,
			PSettingsAdapterItem[] listItems,
			PSettingsOptionsFragment pSettingsOptionsFragment) {
		super(context, textViewResourceId, listItems);
		this.listItems = listItems;
		this.context = context;
		this.pSettingsOptionsFragment = pSettingsOptionsFragment;
	}

	@Override
	public int getViewTypeCount() {
		return 3;
	}

	@Override
	public int getItemViewType(int position) {
		return listItems[position].getItemViewType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int viewType = this.getItemViewType(position);

		PSettingsAdapterItem listItem = listItems[position];
		PSettingsViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (viewType == Constants.SETTINGS_CATEGORY)
				convertView = vi.inflate(R.layout.p_settings_title, null);
			else if (viewType == Constants.SETTINGS_SIMPLE)
				convertView = vi.inflate(R.layout.p_settings_simple, null);
			else
				convertView = vi.inflate(R.layout.p_settings_summary, null);

			SPTextView title = (SPTextView) convertView
					.findViewById(R.id.title);
			TextView summary = (TextView) convertView
					.findViewById(R.id.summary);

			holder = new PSettingsViewHolder(title, summary);
		} else
			holder = (PSettingsViewHolder) convertView.getTag();

		if (viewType == Constants.SETTINGS_CATEGORY)
			holder.getTitle().setText(
					context.getString(listItem.getTitle()).toUpperCase());
		else
			holder.getTitle().setText(listItem.getTitle());

		holder.getSummary().setText(listItem.getSummary());

		convertView.setTag(holder);

		animate(position, viewType, convertView);

		if (position == 2)
			pSettingsOptionsFragment.soundView = convertView;

		return convertView;

	}

	private void animate(int position, int viewType, View convertView) {
		if (position > manager.getSettingsScrolled()) {
			manager.setSettingsScrolled(position);
			Animation anim = null;

			if (viewType == Constants.SETTINGS_CATEGORY) {
				anim = AnimationUtils.loadAnimation(context,
						R.anim.fade_in_settings);
			} else
				anim = AnimationUtils.loadAnimation(context, R.anim.right_left);

			convertView.startAnimation(anim);
		}
	}

	@Override
	public boolean isEnabled(int position) {
		return listItems[position].getItemViewType() != Constants.SETTINGS_CATEGORY;
	}
}
