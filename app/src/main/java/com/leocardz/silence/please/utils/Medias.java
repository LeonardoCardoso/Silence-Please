package com.leocardz.silence.please.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;

public class Medias implements MediaScannerConnectionClient {

	private MediaScannerConnection mMs;
	private File mFile;

	public Medias(Context context, File f) {
		mFile = f;
		mMs = new MediaScannerConnection(context, this);
		mMs.connect();

	}

	@Override
	public void onMediaScannerConnected() {
		mMs.scanFile(mFile.getAbsolutePath(), null);
	}

	@Override
	public void onScanCompleted(String path, Uri uri) {
		mMs.disconnect();
	}

	public static void createPicturesDirectories() {
		String createFolders = Constants.ROOT_PICTURE_DIRECTORY;
		File root = new File(createFolders);
		if (root.exists() == false)
			root.mkdir();

		createFolders = Constants.SP_PICTURE_DIRECTORY;
		root = new File(createFolders);
		if (root.exists() == false)
			root.mkdir();

	}

	public static String getDataDir(Context context) throws Exception {
		return context.getPackageManager().getPackageInfo(
				context.getPackageName(), 0).applicationInfo.dataDir;
	}

	public static void deleteRecursive(File fileOrDirectory) {
		if (fileOrDirectory.isDirectory()) {
			for (File child : fileOrDirectory.listFiles()) {
				deleteRecursive(child);
			}
		}
		fileOrDirectory.delete();
	}

	@SuppressWarnings("resource")
	public static void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
	}

	public static Bitmap correctOrientation(String path,
			ContentResolver contentResolver, int width, int height) {
		// orietantion = 3, rotate 180
		// orietantion = 6, rotate 90
		// orietantion = 8, rotate 270

		int orientation = 0;
		int rotate = 0;

		try {
			ExifInterface exif = new ExifInterface(path);
			orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
		} catch (IOException e) {
			e.printStackTrace();
		}

		File f = new File(path);
		Bitmap bitmap = decodeFile(f, width, height);
		Bitmap correctBitmap = null;

		Matrix matrix = new Matrix();
		rotate = exifOrientationToDegrees(orientation);
		matrix.postRotate(rotate);
		correctBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);

		return correctBitmap;
	}

	private static int exifOrientationToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	public static Uri getImageUri(String path) {
		return Uri.fromFile(new File(path));
	}

	public static Bitmap decodeFile(File f, int width, int height) {
		try {

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			final int requiredWidth = width;
			final int requiredHeight = height;

			int scale = 1;
			while (o.outWidth / scale / 2 >= requiredWidth
					&& o.outHeight / scale / 2 >= requiredHeight)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

}