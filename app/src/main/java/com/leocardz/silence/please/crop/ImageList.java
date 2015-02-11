package com.leocardz.silence.please.crop;

import java.io.IOException;
import java.util.HashMap;

import android.content.ContentResolver;
import android.net.Uri;
import android.os.Parcelable;

//
// ImageList and Image classes have one-to-one correspondence.
// The class hierarchy (* = abstract class):
//
//    ImageList
//    - BaseImageList (*)
//      - VideoList
//      - ImageList
//        - DrmImageList
//      - SingleImageList (contains UrImage)
//    - ImageListUber
//
//    Image
//    - BaseImage (*)
//      - VideoObject
//      - Image
//        - DrmImage
//    - UrImage
//

/**
 * The interface of all image collections used in gallery.
 */
public interface ImageList extends Parcelable {
	public HashMap<String, String> getBucketIds();

	public void deactivate();

	/**
	 * Returns the count of image objects.
	 * 
	 * @return the number of images
	 */
	public int getCount();

	/**
	 * @return true if the count of image objects is zero.
	 */
	public boolean isEmpty();

	/**
	 * Returns the image at the ith position.
	 * 
	 * @param i
	 *            the position
	 * @return the image at the ith position
	 */
	public Image getImageAt(int i);

	/**
	 * Returns the image with a particular Uri.
	 * 
	 * @param uri
	 * @return the image with a particular Uri. null if not found.
	 */
	public Image getImageForUri(Uri uri);

	/**
	 * 
	 * @param image
	 * @return true if the image was removed.
	 */
	public boolean removeImage(Image image);

	/**
	 * Removes the image at the ith position.
	 * 
	 * @param i
	 *            the position
	 */
	public boolean removeImageAt(int i);

	public int getImageIndex(Image image);

	/**
	 * Generate thumbnail for the image (if it has not been generated.)
	 * 
	 * @param index
	 *            the position of the image
	 */
	public void checkThumbnail(int index) throws IOException;

	/**
	 * Opens this list for operation.
	 */
	public void open(ContentResolver resolver);

	/**
	 * Closes this list to release resources, no further operation is allowed.
	 */
	public void close();
}
