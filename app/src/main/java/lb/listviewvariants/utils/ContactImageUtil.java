package lb.listviewvariants.utils;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;

import lb.listviewvariants.BuildConfig;

public class ContactImageUtil
  {
  @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
  public static Bitmap loadContactPhoto(Context context,Uri contactUri,int imageSize)
    {
    // Instantiates a ContentResolver for retrieving the Uri of the image
    final ContentResolver contentResolver=context.getContentResolver();

    // Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
    // ContentResolver can return an AssetFileDescriptor for the file.
    AssetFileDescriptor afd=null;
    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.ICE_CREAM_SANDWICH)
      {
      // On platforms running Android 4.0 (API version 14) and later, a high resolution image
      // is available from Photo.DISPLAY_PHOTO.
      try
        {
        // Constructs the content Uri for the image
        Uri displayImageUri=Uri.withAppendedPath(contactUri,ContactsContract.Contacts.Photo.DISPLAY_PHOTO);

        // Retrieves an AssetFileDescriptor from the Contacts Provider, using the
        // constructed Uri
        afd=contentResolver.openAssetFileDescriptor(displayImageUri,"r");
        // If the file exists
        if(afd!=null)
          {
          // Reads and decodes the file to a Bitmap and scales it to the desired size
          return decodeSampledBitmapFromDescriptor(
              afd.getFileDescriptor(),imageSize,imageSize);
          }
        }
      catch(FileNotFoundException e)
        {
        // Catches file not found exceptions
        if(BuildConfig.DEBUG)
          {
          // Log debug message, this is not an error message as this exception is thrown
          // when a contact is legitimately missing a contact photo (which will be quite
          // frequently in a long contacts list).
//        Log.d(TAG,"Contact photo not found for contact "+contactUri.toString()
//            +": "+e.toString());
          }
        }
      finally
        {
        // Once the decode is complete, this closes the file. You must do this each time
        // you access an AssetFileDescriptor; otherwise, every image load you do will open
        // a new descriptor.
        if(afd!=null)
          {
          try
            {
            afd.close();
            }
          catch(IOException e)
            {
            // Closing a file descriptor might cause an IOException if the file is
            // already closed. Nothing extra is needed to handle this.
            }
          }
        }
      }

    // If the platform version is less than Android 4.0 (API Level 14), use the only available
    // image URI, which points to a normal-sized image.
    try
      {
      // Constructs the image Uri from the contact Uri and the directory twig from the
      // Contacts.Photo table
      Uri imageUri=Uri.withAppendedPath(contactUri,ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);

      // Retrieves an AssetFileDescriptor from the Contacts Provider, using the constructed
      // Uri
      afd=contentResolver.openAssetFileDescriptor(imageUri,"r");

      // If the file exists
      if(afd!=null)
        {
        // Reads the image from the file, decodes it, and scales it to the available screen
        // area
        return decodeSampledBitmapFromDescriptor(
            afd.getFileDescriptor(),imageSize,imageSize);
        }
      }
    catch(FileNotFoundException e)
      {
      // Catches file not found exceptions
      if(BuildConfig.DEBUG)
        {
//        Log.d(TAG,"Contact photo not found for contact "+contactUri.toString()
//            +": "+e.toString());
        }
      }
    finally
      {
      // Once the decode is complete, this closes the file. You must do this each time you
      // access an AssetFileDescriptor; otherwise, every image load you do will open a new
      // descriptor.
      if(afd!=null)
        {
        try
          {
          afd.close();
          }
        catch(IOException e)
          {
          // Closing a file descriptor might cause an IOException if the file is
          // already closed. Ignore this.
          }
        }
      }

    // If none of the case selectors match, returns null.
    return null;
    }

  /**
   * Decodes and scales a contact's image from a file pointed to by a Uri in the contact's data,
   * and returns the result as a Bitmap. The column that contains the Uri varies according to the
   * platform version.
   *
   * @param photoData For platforms prior to Android 3.0, provide the Contact._ID column value.
   *                  For Android 3.0 and later, provide the Contact.PHOTO_THUMBNAIL_URI value.
   * @param imageSize The desired target width and height of the output image in pixels.
   * @return A Bitmap containing the contact's image, resized to fit the provided image size. If
   * no thumbnail exists, returns null.
   */
  public static Bitmap loadContactPhotoThumbnail(Context context,String photoData,int imageSize)
    {
    // Instantiates an AssetFileDescriptor. Given a content Uri pointing to an image file, the
    // ContentResolver can return an AssetFileDescriptor for the file.
    AssetFileDescriptor afd=null;
    // This "try" block catches an Exception if the file descriptor returned from the Contacts
    // Provider doesn't point to an existing file.
    try
      {
      Uri thumbUri;
      // If Android 3.0 or later, converts the Uri passed as a string to a Uri object.
      if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.HONEYCOMB)
        thumbUri=Uri.parse(photoData);
      else
        {
        // For versions prior to Android 3.0, appends the string argument to the content
        // Uri for the Contacts table.
        final Uri contactUri=Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,photoData);
        // Appends the content Uri for the Contacts.Photo table to the previously
        // constructed contact Uri to yield a content URI for the thumbnail image
        thumbUri=Uri.withAppendedPath(contactUri,ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        }
      // Retrieves a file descriptor from the Contacts Provider. To learn more about this
      // feature, read the reference documentation for
      // ContentResolver#openAssetFileDescriptor.
      afd=context.getContentResolver().openAssetFileDescriptor(thumbUri,"r");

      // Gets a FileDescriptor from the AssetFileDescriptor. A BitmapFactory object can
      // decode the contents of a file pointed to by a FileDescriptor into a Bitmap.
      FileDescriptor fileDescriptor=afd.getFileDescriptor();

      if(fileDescriptor!=null)
        {
        // Decodes a Bitmap from the image pointed to by the FileDescriptor, and scales it
        // to the specified width and height
        return decodeSampledBitmapFromDescriptor(
            fileDescriptor,imageSize,imageSize);
        }
      }
    catch(FileNotFoundException e)
      {
      // If the file pointed to by the thumbnail URI doesn't exist, or the file can't be
      // opened in "read" mode, ContentResolver.openAssetFileDescriptor throws a
      // FileNotFoundException.
//      if(BuildConfig.DEBUG)
//        {
//        Log.d(TAG,"Contact photo thumbnail not found for contact "+photoData
//            +": "+e.toString());
//        }
      }
    finally
      {
      // If an AssetFileDescriptor was returned, try to close it
      if(afd!=null)
        {
        try
          {
          afd.close();
          }
        catch(IOException e)
          {
          // Closing a file descriptor might cause an IOException if the file is
          // already closed. Nothing extra is needed to handle this.
          }
        }
      }

    // If the decoding failed, returns null
    return null;
    }


  public static Bitmap decodeSampledBitmapFromDescriptor(
      FileDescriptor fileDescriptor,int reqWidth,int reqHeight)
    {

    // First decode with inJustDecodeBounds=true to check dimensions
    final BitmapFactory.Options options=new BitmapFactory.Options();
    options.inJustDecodeBounds=true;
    BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);

    // Calculate inSampleSize
    options.inSampleSize=calculateInSampleSize(options,reqWidth,reqHeight);

    // Decode bitmap with inSampleSize set
    options.inJustDecodeBounds=false;
    return BitmapFactory.decodeFileDescriptor(fileDescriptor,null,options);
    }

  /**
   * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
   * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
   * the closest inSampleSize that will result in the final decoded bitmap having a width and
   * height equal to or larger than the requested width and height. This implementation does not
   * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
   * results in a larger bitmap which isn't as useful for caching purposes.
   *
   * @param options   An options object with out* params already populated (run through a decode*
   *                  method with inJustDecodeBounds==true
   * @param reqWidth  The requested width of the resulting bitmap
   * @param reqHeight The requested height of the resulting bitmap
   * @return The value to be used for inSampleSize
   */
  public static int calculateInSampleSize(BitmapFactory.Options options,
                                          int reqWidth,int reqHeight)
    {
    // Raw height and width of image
    final int height=options.outHeight;
    final int width=options.outWidth;
    int inSampleSize=1;

    if(height>reqHeight||width>reqWidth)
      {

      // Calculate ratios of height and width to requested height and width
      final int heightRatio=Math.round((float)height/(float)reqHeight);
      final int widthRatio=Math.round((float)width/(float)reqWidth);

      // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
      // with both dimensions larger than or equal to the requested height and width.
      inSampleSize=heightRatio<widthRatio?heightRatio:widthRatio;

      // This offers some additional logic in case the image has a strange
      // aspect ratio. For example, a panorama may have a much larger
      // width than height. In these cases the total pixels might still
      // end up being too large to fit comfortably in memory, so we should
      // be more aggressive with sample down the image (=larger inSampleSize).

      final float totalPixels=width*height;

      // Anything more than 2x the requested pixels we'll sample down further
      final float totalReqPixelsCap=reqWidth*reqHeight*2;

      while(totalPixels/(inSampleSize*inSampleSize)>totalReqPixelsCap)
        {
        inSampleSize++;
        }
      }
    return inSampleSize;
    }
  }
