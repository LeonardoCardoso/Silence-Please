package com.leocardz.silence.please;

import java.io.File;
import java.io.IOException;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ImageView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.leocardz.silence.please.fragment.PSilenceFragment;
import com.leocardz.silence.please.utils.Constants;
import com.leocardz.silence.please.utils.Medias;

@SuppressWarnings({ "unused" })
public class ImageSelection extends SherlockActivity {

	private boolean isInFront = true;
	private Dialog progressDialog;

	private SharedPreferences settings;
	private Context context;
	private String imagePath = Constants.SP_PICTURE_DIRECTORY;
	private String imagePref;
	private DisplayMetrics displaymetrics;
	private int height;
	private int width;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = this;

		setInFront(true);

		Medias.createPicturesDirectories();

		setContentView(R.layout.image_selection);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		progressDialog = new Dialog(context, R.style.CustomDialog);
		progressDialog.setContentView(R.layout.p_progress_dialog);
		progressDialog.setCancelable(true);

		displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		height = displaymetrics.heightPixels;
		width = displaymetrics.widthPixels;

		settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);

		imagePref = settings.getString("image", "nurse");

		if (!imagePref.equals("nurse") && !imagePref.equals("")) {

			File imgFile = new File(imagePref);
			if (imgFile.exists()) {
				setImage();
			}

		}

	}

	private void setImage() {
		File imgFile = new File(imagePref);
		if (imgFile.exists()) {
			Bitmap myBitmap = Medias.decodeFile(imgFile, width, height);

			ImageView myImage = (ImageView) findViewById(R.id.image);
			myImage.setImageBitmap(myBitmap);
			settings.edit().putString("image", imagePref).commit();
		}
		try {
			PSilenceFragment.setImage();
		} catch (Exception e) {
		}
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		switch (requestCode) {
		case Constants.SELECT_PICTURE:
			if (resultCode == RESULT_OK && data != null) {

				Uri imageUri = data.getData();
				String path = "";

				if (imageUri != null) {

					Cursor cursor = getContentResolver()
							.query(imageUri,
									new String[] { android.provider.MediaStore.Images.ImageColumns.DATA },
									null, null, null);
					cursor.moveToFirst();

					path = cursor.getString(0);
					cursor.close();

					if (path != null && !path.equals("")) {

						File imageSource = new File(path);

						String destiny = Constants.SP_PICTURE_DIRECTORY
								+ "noise.jpg";
						File imageDestiny = new File(destiny);

						try {
							Medias.copyFile(imageSource, imageDestiny);
						} catch (IOException e) {
							e.printStackTrace();
						}

						if (imageDestiny.exists()) {
							Intent intent = new Intent(this, CropImage.class);
							intent.putExtra("image-path", destiny);
							intent.putExtra("outputX", width);
							intent.putExtra("outputY", height);
							intent.putExtra("aspectX", 1);
							intent.putExtra("aspectY", 1);
							intent.putExtra("scale", true);
							startActivityForResult(intent,
									Constants.CROP_PICTURE);
						}
					}

				}
			}
			break;
		case Constants.CROP_PICTURE:
			if (resultCode == RESULT_OK) {
				imagePref = Constants.SP_PICTURE_DIRECTORY + "noise.jpg";
				setImage();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.activity_image_selection, menu);
		return true;
	}

	private void callDialog() {
		if (isInFront() && !progressDialog.isShowing())
			progressDialog.show();
	}

	private void dismissDialog() {
		if (progressDialog.isShowing())
			progressDialog.dismiss();
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			break;
		case R.id.new_image:
			Intent intent = new Intent(Intent.ACTION_PICK);
			intent.setType("image/*");
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, Constants.SELECT_PICTURE);
			break;
		case R.id.restore_image:
			imagePref = "nurse";
			settings.edit().putString("image", "nurse").commit();
			ImageView myImage = (ImageView) findViewById(R.id.image);
			myImage.setImageResource(R.drawable.silence);
			setImage();
			break;
		default:
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	public boolean isInFront() {
		return isInFront;
	}

	public void setInFront(boolean isInFront) {
		this.isInFront = isInFront;

	}

	@Override
	protected void onResume() {
		setInFront(true);
		super.onResume();
	}

	@Override
	protected void onPause() {
		setInFront(false);
		super.onPause();
	}

	@Override
	protected void onStop() {
		setInFront(false);
		super.onStop();
	}

}
