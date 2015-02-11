package com.leocardz.silence.please.adapter.view.holder;

import android.widget.TextView;

public class PNavigationListViewHolder {
	private TextView title;
	private TextView selectedItem;

	public PNavigationListViewHolder(TextView title, TextView selectedItem) {
		super();
		this.title = title;
		this.selectedItem = selectedItem;
	}

	public TextView getTitle() {
		return title;
	}

	public void setTitle(TextView title) {
		this.title = title;
	}

	public TextView getSelectedItem() {
		return selectedItem;
	}

	public void setSelectedItem(TextView selectedItem) {
		this.selectedItem = selectedItem;
	}

}
