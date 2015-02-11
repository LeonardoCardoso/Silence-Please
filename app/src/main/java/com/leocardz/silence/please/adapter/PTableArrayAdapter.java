package com.leocardz.silence.please.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.leocardz.silence.please.R;
import com.leocardz.silence.please.adapter.item.PTableAdapterItem;
import com.leocardz.silence.please.adapter.view.holder.PTableViewHolder;
import com.leocardz.silence.please.custom.SPTextView;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.Manager;

@SuppressLint("ViewConstructor")
public class PTableArrayAdapter extends ArrayAdapter<PTableAdapterItem> {

	private Manager manager = Manager.getInstance();

	private PTableAdapterItem[] listItems;
	private Context context;
	boolean animate;

	public PTableArrayAdapter(Context context, int textViewResourceId,
			PTableAdapterItem[] listItems) {
		super(context, textViewResourceId, listItems);
		this.listItems = listItems;
		this.context = context;
		this.animate = true;
	}

	public PTableArrayAdapter(Context context, int textViewResourceId,
			PTableAdapterItem[] listItems, boolean animate) {
		super(context, textViewResourceId, listItems);
		this.listItems = listItems;
		this.context = context;
		this.animate = animate;
	}

	@Override
	public int getViewTypeCount() {
		return 5;
	}

	@Override
	public int getItemViewType(int position) {
		return listItems[position].getItemViewType();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		int viewType = this.getItemViewType(position);

		PTableAdapterItem listItem = listItems[position];
		PTableViewHolder holder = null;

		if (convertView == null) {
			LayoutInflater vi = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

			if (viewType == Constants.SETTINGS_CATEGORY)
				convertView = vi.inflate(R.layout.p_settings_title, null);
			else if (viewType == Constants.SETTINGS_SIMPLE)
				convertView = vi.inflate(R.layout.p_settings_simple, null);
			else if (viewType == Constants.SETTINGS_SIMPLE_CENTER)
				convertView = vi.inflate(R.layout.p_settings_simple_center,
						null);
			else if (viewType == Constants.SETTINGS_SUMMARY_CENTER)
				convertView = vi.inflate(R.layout.p_settings_summary_center,
						null);
			else
				convertView = vi.inflate(R.layout.p_settings_summary, null);

			SPTextView title = (SPTextView) convertView
					.findViewById(R.id.title);
			TextView summary = (TextView) convertView
					.findViewById(R.id.summary);

			holder = new PTableViewHolder(title, summary);
		} else
			holder = (PTableViewHolder) convertView.getTag();

		if (viewType == Constants.SETTINGS_CATEGORY)
			holder.getTitle().setText(
					context.getString(listItem.getTitle()).toUpperCase());
		else
			holder.getTitle().setText(listItem.getTitle());

		holder.getSummary().setText(listItem.getSummary());

		convertView.setTag(holder);

		if (animate)
			animate(position, viewType, convertView);

		return convertView;

	}

	private void animate(int position, int viewType, View convertView) {
		if (position > manager.getTableScrolled()) {
			manager.setTableScrolled(position);
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
		return false;
	}
}
