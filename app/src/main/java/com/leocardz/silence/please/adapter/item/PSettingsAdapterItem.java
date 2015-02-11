package com.leocardz.silence.please.adapter.item;

public class PSettingsAdapterItem {

	private int title;
	private String summary;
	private int type;

	public PSettingsAdapterItem(int title, String summary, int type) {
		this.title = title;
		this.summary = summary;
		this.type = type;
	}

	public int getItemViewType() {
		return this.getType();
	}

	public int getTitle() {
		return title;
	}

	public void setTitle(int title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
