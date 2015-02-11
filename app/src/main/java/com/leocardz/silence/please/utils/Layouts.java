package com.leocardz.silence.please.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.leocardz.silence.please.R;

public class Layouts {

	public static void showDialogBoxSingle(Context ctx, int title, int text) {
		final Dialog dialog = new Dialog(ctx, R.style.CustomDialogICS);
		dialog.setContentView(R.layout.p_dialog_box_message_single);
		dialog.setCancelable(true);

		TextView dialogTitle = (TextView) dialog
				.findViewById(R.id.dialog_title);
		dialogTitle.setText(title);

		TextView textView = (TextView) dialog
				.findViewById(R.id.dialog_box_text);
		textView.setText(text);

		Button firstButton = (Button) dialog.findViewById(R.id.first_button);
		firstButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		dialog.show();
	}

}
