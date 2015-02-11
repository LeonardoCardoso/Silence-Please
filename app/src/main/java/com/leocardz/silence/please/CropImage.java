package com.leocardz.silence.please;

import java.io.IOException;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.view.KeyEvent;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.leocardz.silence.please.crop.CropImageView;
import com.leocardz.silence.please.crop.HighlightView;
import com.leocardz.silence.please.crop.Image;
import com.leocardz.silence.please.crop.MonitoredActivity;
import com.leocardz.silence.please.crop.RotateBitmap;
import com.leocardz.silence.please.crop.Util;
import com.leocardz.silence.please.utils.Layouts;
import com.leocardz.silence.please.utils.Medias;

/**
 * The activity can crop specific region of interest from an image.
 */
public class CropImage extends MonitoredActivity {
	// private static final String TAG = "CropImage";

	// These are various options can be specified in the intent.
	private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
	// only used with mSaveUri

	private Uri mSaveUri = null;
	private int mAspectX, mAspectY;
	private boolean mCircleCrop = false;
	private final Handler mHandler = new Handler();

	// These options specifiy the output image size and whether we should
	// scale the output to fit it (or just crop it).
	private int mOutputX, mOutputY;
	private boolean mScale;
	private boolean mScaleUp = true;

	private boolean mDoFaceDetection = true;

	private boolean mWaitingToPick; // Whether we are wait the user to pick a
									// face.
	private boolean mSaving; // Whether the "save" button is already clicked.

	private CropImageView mImageView;
	private ContentResolver mContentResolver;

	private Bitmap mBitmap;
	private HighlightView mCrop;

	private Image mImage;

	private String mImagePath;

	public ActionBar ab;
	public Context context;
	public Dialog progressDialog;

	private Menu menuCrop;
	private MenuItem cropItem;

	public final int NO_STORAGE_ERROR = -1;
	public final int CANNOT_STAT_ERROR = -2;

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		initView();
	}

	private void initView() {
		mContentResolver = getContentResolver();

		ab = getSupportActionBar();
		ab.setDisplayHomeAsUpEnabled(true);
		ab.setDisplayUseLogoEnabled(true);

		context = this;

		setTitle(R.string.crop_image);

		// requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.p_crop_image);

		mImageView = (CropImageView) findViewById(R.id.image);
		showStorageMessage(this);

		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if (extras != null) {
			if (extras.getString("circleCrop") != null) {
				mCircleCrop = true;
				mAspectX = 1;
				mAspectY = 1;
			}

			mImagePath = extras.getString("image-path");
			mSaveUri = Medias.getImageUri(mImagePath);
			mBitmap = Medias.correctOrientation(mImagePath, mContentResolver,
					extras.getInt("outputX"), extras.getInt("outputY"));

			mAspectX = extras.getInt("aspectX");
			mAspectY = extras.getInt("aspectY");
			mOutputX = extras.getInt("outputX");
			mOutputY = extras.getInt("outputY");
			mOutputY -= getResources().getDimensionPixelSize(
					R.dimen.abs__action_bar_default_height)
					+ getResources().getDimensionPixelSize(
							R.dimen.abs__action_bar_default_height) / 2;

			mScale = extras.getBoolean("scale", true);
			mScaleUp = extras.getBoolean("scaleUpIfNeeded", true);
			mScaleUp = extras.getBoolean("scaleDownIfNeeded", true);
		}

		if (mBitmap == null) {
			// Log.d(TAG, "finish!!!");
			finish();
			return;
		}

		progressDialog = new Dialog(context, R.style.CustomDialog);
		progressDialog.setContentView(R.layout.p_progress_dialog);
		// Make UI fullscreen.
		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		startFaceDetection();
	}

	public Menu getMenuCrop() {
		return menuCrop;
	}

	public void setMenuCrop(Menu menuCrop) {
		this.menuCrop = menuCrop;
	}

	public MenuItem getCropItem() {
		return cropItem;
	}

	public void setCropItem(MenuItem cropItem) {
		this.cropItem = cropItem;
	}

	private void startFaceDetection() {
		if (isFinishing()) {
			return;
		}

		mImageView.setImageBitmapResetBase(mBitmap, true);

		Util.startBackgroundJob(this, null, progressDialog, new Runnable() {
			public void run() {
				final CountDownLatch latch = new CountDownLatch(1);
				final Bitmap b = (mImage != null) ? mImage.fullSizeBitmap(
						Image.UNCONSTRAINED, 1024 * 1024) : mBitmap;
				mHandler.post(new Runnable() {
					public void run() {
						if (b != mBitmap && b != null) {
							mImageView.setImageBitmapResetBase(b, true);
							mBitmap.recycle();
							mBitmap = b;
						}
						if (mImageView.getScale() == 1F) {
							mImageView.center(true, true);
						}
						latch.countDown();
					}
				});
				try {
					latch.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				mRunFaceDetection.run();
			}
		}, mHandler);
	}

	private void onSaveClicked() {
		// step api so that we don't require that the whole (possibly large)
		// bitmap doesn't have to be read into memory
		if (isSaving())
			return;

		if (getCrop() == null) {
			return;
		}

		setSaving(true);

		Rect r = getCrop().getCropRect();

		int width = r.width();
		int height = r.height();

		// If we are circle cropping, we want alpha channel, which is the
		// third param here.
		Bitmap croppedImage = Bitmap.createBitmap(width, height,
				mCircleCrop ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);
		{
			Canvas canvas = new Canvas(croppedImage);
			Rect dstRect = new Rect(0, 0, width, height);
			canvas.drawBitmap(mBitmap, r, dstRect, null);
		}

		if (mCircleCrop) {
			// OK, so what's all this about?
			// Bitmaps are inherently rectangular but we want to return
			// something that's basically a circle. So we fill in the
			// area around the circle with alpha. Note the all important
			// PortDuff.Mode.CLEAR.
			Canvas c = new Canvas(croppedImage);
			Path p = new Path();
			p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
			c.clipPath(p, Region.Op.DIFFERENCE);
			c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
		}

		/* If the output is required to a specific size then scale or fill */
		if (mOutputX != 0 && mOutputY != 0) {
			if (mScale) {
				/* Scale the image to the required dimensions */
				Bitmap old = croppedImage;
				croppedImage = Util.transform(new Matrix(), croppedImage,
						mOutputX, mOutputY, mScaleUp);
				if (old != croppedImage) {
					old.recycle();
				}
			} else {

				/*
				 * Don't scale the image crop it to the size requested. Create
				 * an new image with the cropped image in the center and the
				 * extra space filled.
				 */

				// Don't scale the image but instead fill it so it's the
				// required dimension
				Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
						Bitmap.Config.RGB_565);
				Canvas canvas = new Canvas(b);

				Rect srcRect = getCrop().getCropRect();
				Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

				int dx = (srcRect.width() - dstRect.width()) / 2;
				int dy = (srcRect.height() - dstRect.height()) / 2;

				/* If the srcRect is too big, use the center part of it. */
				srcRect.inset(Math.max(0, dx), Math.max(0, dy));

				/* If the dstRect is too big, use the center part of it. */
				dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

				/* Draw the cropped bitmap in the center */
				canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

				/* Set the cropped bitmap as the new bitmap */
				croppedImage.recycle();
				croppedImage = b;
			}
		}

		// Return the cropped image directly or save it to the specified URI.
		Bundle myExtras = getIntent().getExtras();
		if (myExtras != null
				&& (myExtras.getParcelable("data") != null || myExtras
						.getBoolean("return-data"))) {
			Bundle extras = new Bundle();
			extras.putParcelable("data", croppedImage);
			setResult(RESULT_OK, (new Intent()).setAction("inline-data")
					.putExtras(extras));
			finish();
			return;
		} else {
			final Bitmap b = croppedImage;
			Util.startBackgroundJob(this, null, progressDialog, new Runnable() {
				public void run() {
					saveOutput(b);
				}
			}, mHandler);
		}
	}

	public void rotateLeft() {
		mBitmap = Util.rotateImage(mBitmap, -90);
		RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
		mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
		mRunFaceDetection.run();
	}

	public void rotateRight() {
		mBitmap = Util.rotateImage(mBitmap, 90);
		RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
		mImageView.setImageRotateBitmapResetBase(rotateBitmap, true);
		mRunFaceDetection.run();
	}

	private void saveOutput(Bitmap croppedImage) {
		if (mSaveUri != null) {
			OutputStream outputStream = null;
			try {
				outputStream = mContentResolver.openOutputStream(mSaveUri);
				if (outputStream != null) {
					croppedImage.compress(mOutputFormat, 75, outputStream);
				}
			} catch (IOException ex) {
				// Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
			} finally {
				Util.closeSilently(outputStream);
			}
			Bundle extras = new Bundle();
			setResult(RESULT_OK,
					new Intent(mSaveUri.toString()).putExtras(extras));
		} else {
			// Log.e(TAG, "not defined image url");
		}
		croppedImage.recycle();
		finish();
		return;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {
			mBitmap.recycle();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	Runnable mRunFaceDetection = new Runnable() {
		float mScale = 1F;
		Matrix mImageMatrix;
		FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
		int mNumFaces;

		// Create a default HightlightView if we found no face in the picture.
		private void makeDefault() {
			HighlightView hv = new HighlightView(mImageView);

			int width = mBitmap.getWidth();
			int height = mBitmap.getHeight();

			Rect imageRect = new Rect(0, 0, width, height);

			// make the default size about 4/5 of the width or height
			int cropWidth = Math.min(width, height) * 4 / 5;
			int cropHeight = cropWidth;

			if (mAspectX != 0 && mAspectY != 0) {
				if (mAspectX > mAspectY) {
					cropHeight = cropWidth * mOutputY / mOutputX;
				} else {
					cropWidth = cropHeight * mOutputX / mOutputY;
				}
			}

			int x = (width - cropWidth) / 2;
			int y = (height - cropHeight) / 2;

			RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
			hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop,
					mAspectX != 0 && mAspectY != 0);

			mImageView.getHighlightViews().clear(); // Thong added for rotate

			mImageView.add(hv);
		}

		// Scale the image down for faster face detection.
		private Bitmap prepareBitmap() {
			if (mBitmap == null) {
				return null;
			}

			// 256 pixels wide is enough.
			if (mBitmap.getWidth() > 256) {
				mScale = 256.0F / mBitmap.getWidth();
			}
			Matrix matrix = new Matrix();
			matrix.setScale(mScale, mScale);
			Bitmap faceBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
					mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
			return faceBitmap;
		}

		public void run() {
			mImageMatrix = mImageView.getImageMatrix();
			Bitmap faceBitmap = prepareBitmap();

			mScale = 1.0F / mScale;
			if (faceBitmap != null && mDoFaceDetection) {
				FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
						faceBitmap.getHeight(), mFaces.length);
				mNumFaces = detector.findFaces(faceBitmap, mFaces);
			}

			if (faceBitmap != null && faceBitmap != mBitmap) {
				faceBitmap.recycle();
			}

			mHandler.post(new Runnable() {
				public void run() {
					makeDefault();
					mImageView.invalidate();
					if (mImageView.getHighlightViews().size() == 1) {
						setCrop(mImageView.getHighlightViews().get(0));
						getCrop().setFocus(true);
					}

					if (mNumFaces > 1) {
						Toast t = Toast.makeText(CropImage.this,
								"Multi face crop help", Toast.LENGTH_SHORT);
						t.show();
					}
				}
			});
		}
	};

	public void showStorageMessage(SherlockActivity activity) {
		showStorageMessage(activity, calculatePicturesRemaining());
	}

	public void showStorageMessage(SherlockActivity activity, int remaining) {
		int noStorageText = -1;

		if (remaining == NO_STORAGE_ERROR) {
			String state = Environment.getExternalStorageState();
			if (state == Environment.MEDIA_CHECKING) {
				noStorageText = R.string.preparing_card;
			} else {
				noStorageText = R.string.no_storage_card;
			}
		} else if (remaining < 1) {
			noStorageText = R.string.not_enough_space;
		}

		if (noStorageText != -1) {
			Layouts.showDialogBoxSingle(context, R.string.dialog_title_whoops,
					noStorageText);
		}
	}

	@SuppressWarnings("deprecation")
	public int calculatePicturesRemaining() {
		try {
			/*
			 * if (!ImageManager.hasStorage()) { return NO_STORAGE_ERROR; } else
			 * {
			 */
			String storageDirectory = Environment.getExternalStorageDirectory()
					.toString();
			StatFs stat = new StatFs(storageDirectory);
			float remaining = ((float) stat.getAvailableBlocks() * (float) stat
					.getBlockSize()) / 400000F;
			return (int) remaining;
			// }
		} catch (Exception ex) {
			// if we can't stat the filesystem then we don't know how many
			// pictures are remaining. it might be zero but just leave it
			// blank since we really don't know.
			return CANNOT_STAT_ERROR;
		}
	}

	public boolean isSaving() {
		return mSaving;
	}

	public void setSaving(boolean mSaving) {
		this.mSaving = mSaving;
	}

	public boolean ismWaitingToPick() {
		return mWaitingToPick;
	}

	public void setWaitingToPick(boolean mWaitingToPick) {
		this.mWaitingToPick = mWaitingToPick;
	}

	public HighlightView getCrop() {
		return mCrop;
	}

	public void setCrop(HighlightView mCrop) {
		this.mCrop = mCrop;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.p_crop_image_menu, menu);

		setMenuCrop(menu);

		MenuItem crop = (MenuItem) getMenuCrop().findItem(R.id.item_save);
		setCropItem(crop);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.item_save:
			onSaveClicked();
			return true;
		case R.id.item_turn_left:
			rotateLeft();
			return true;
		case R.id.item_turn_right:
			rotateRight();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
		}
		return true;
	}

}
