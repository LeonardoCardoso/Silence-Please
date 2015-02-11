package com.leocardz.silence.please.adapter.item;

public class PNavigationListAdapterItem {
	private String title;
	private int selectedVisibility;

	public PNavigationListAdapterItem(String title, int selectedVisibility) {
		super();
		this.title = title;
		this.selectedVisibility = selectedVisibility;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getSelectedVisibility() {
		return selectedVisibility;
	}

	public void setSelectedVisibility(int selectedVisibility) {
		this.selectedVisibility = selectedVisibility;
	}

}
