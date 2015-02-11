package com.leocardz.silence.please.adapter.view.holder;

import android.widget.TextView;

import com.leocardz.silence.please.custom.SPTextView;

public class PSettingsViewHolder {
	private SPTextView title;
	private TextView summary;

	public PSettingsViewHolder(SPTextView title, TextView summary) {
		super();
		this.title = title;
		this.summary = summary;
	}

	public SPTextView getTitle() {
		return title;
	}

	public void setTitle(SPTextView title) {
		this.title = title;
	}

	public TextView getSummary() {
		return summary;
	}

	public void setSummary(TextView summary) {
		this.summary = summary;
	}
}