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
import com.leocardz.silence.please.adapter.item.PNavigationListAdapterItem;
import com.leocardz.silence.please.adapter.view.holder.PNavigationListViewHolder;
import com.leocardz.silence.please.utils.Manager;

public class PNavigationListArrayAdapter extends
		ArrayAdapter<PNavigationListAdapterItem> {

	private Manager manager = Manager.getInstance();

	private PNavigationListAdapterItem[] listItems;
	private Context context;

	public PNavigationListArrayAdapter(Context context, int textViewResourceId,
			PNavigationListAdapterItem[] listItems) {
		super(context, textViewResourceId, listItems);
		this.listItems = listItems;
		this.context = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PNavigationListViewHolder holder = null;

		LayoutInflater vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		PNavigationListAdapterItem listItem = listItems[position];

		if (convertView == null) {
			convertView = vi.inflate(R.layout.p_drawer_list_item, null);

			TextView title = (TextView) convertView.findViewById(R.id.name);
			TextView selected = (TextView) convertView
					.findViewById(R.id.selected_item);

			holder = new PNavigationListViewHolder(title, selected);
		} else
			holder = (PNavigationListViewHolder) convertView.getTag();

		holder.getTitle().setText(listItem.getTitle());
		holder.getSelectedItem()
				.setVisibility(listItem.getSelectedVisibility());

		convertView.setTag(holder);

		if (position > manager.getNavigationScrolled()) {
			manager.setNavigationScrolled(position);
			Animation anim = AnimationUtils.loadAnimation(context,
					R.anim.right_left);
			convertView.startAnimation(anim);
		}

		return convertView;
	}

}
