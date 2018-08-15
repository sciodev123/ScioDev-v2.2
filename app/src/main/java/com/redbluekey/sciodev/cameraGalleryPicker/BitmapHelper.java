package com.redbluekey.sciodev.cameraGalleryPicker;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class BitmapHelper {

    /**
     * Checks image size and downscales it, if one of sizes is greater than required
     * @param src - source image
     * @param maxSize - maximum size of the greater side
     * @return resized image
     */
    public static Bitmap resizeImage(Bitmap src, int maxSize) {
        int width = src.getWidth();
        int height = src.getHeight();

        if (Math.max(width, height)<=maxSize)
        {
            Log.i("MainActivity", "Bitmap has good size.");
            return src;
        }
      //  Log.i("MainActivity","Bitmap will be downscaled.");

        int newWidth, newHeight;
        if(width > height) {
            newWidth = maxSize;
            newHeight = height * maxSize / width;
        }
        else {
            newWidth = width * maxSize / height;
            newHeight = maxSize;
        }

        Bitmap retImage = Bitmap.createScaledBitmap(src, newWidth, newHeight, true);

        src.recycle();
        return retImage;
    }

    public static Bitmap decodeUriFixRotation(Activity activity,Uri imageUri, String imagePath)
    {
        Bitmap bitmap = BitmapHelper.decodeUri(activity, imageUri);
        return BitmapHelper.fixRotation(imagePath, bitmap);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight * 0.8 && (halfWidth / inSampleSize) >= reqWidth * 0.8) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    /**
     * Loads a Bitmap by Uri
     * @param imageUri
     * @return Bitmap or null if failed to load
     */
    public static Bitmap decodeUri(Activity activity, Uri imageUri, int maxSize, int height, int width) {
        // Force GC to collect memory ??
        System.gc();

        Bitmap resBitmap = null;
        long maxMemory = Runtime.getRuntime().maxMemory() / 1024;
        long usedMemory = Runtime.getRuntime().totalMemory() / 1024;
        Log.i("decodeUri", "before decodeUri Memory Use :" + usedMemory + "KB/" + maxMemory + "KB");

        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri), null, options);

//            final DisplayMetrics displayMetrics = new DisplayMetrics();
//            activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//            final int height = displayMetrics.heightPixels;
//            final int width = displayMetrics.widthPixels;

            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, width, height);
            options.inJustDecodeBounds = false;
            options.inScaled = false;
            options.inMutable = true;
            options.inDither = false;
            options.inPreferredConfig=Bitmap.Config.ARGB_8888;

            //options.inPurgeable
            Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(imageUri), null, options);
            resBitmap = BitmapHelper.resizeImage(bitmap, maxSize);

            maxMemory = Runtime.getRuntime().maxMemory() / 1024;
            usedMemory =  Runtime.getRuntime().totalMemory() / 1024;
            Log.i("decodeUri", "after resize Memory Use :" + usedMemory + "KB/" + maxMemory + "KB");

        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            maxMemory = Runtime.getRuntime().maxMemory() / 1024;
            usedMemory =  Runtime.getRuntime().totalMemory() / 1024;
            Log.i("decodeUri", "Out of memory: Memory Use :" + usedMemory + "KB/" + maxMemory + "KB");
            Toast.makeText(activity, "Cannot open image, not enough memory", Toast.LENGTH_LONG).show();
        }

        // Force GC to collect memory ??
        System.gc();


        return resBitmap;
    }

    public static Bitmap decodeUri(Activity activity, Uri imageUri) {
        final DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        final int height = (int)(displayMetrics.heightPixels);
        final int width = (int)(displayMetrics.widthPixels);
        return decodeUri(activity,imageUri, 2148, height, width);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public static String getPath(Context context, Uri uri) {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];
                    if ("image".equals(type)) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }
                    selection = "_id=?";
                    selectionArgs = new String[]{ split[1] };
                }
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { MediaStore.Images.Media.DATA };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri,  proj, null, null, null);
            if (cursor == null)
                return "";
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static Bitmap fixRotation(String imagePath, Bitmap bitmap)
    {
        if (imagePath == null)
            return null;
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(imagePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                    matrix, true);
        } catch (OutOfMemoryError ex) {
            throw new OutOfMemoryError("Low Memory");
        }
    }




    public static final String insertImage(ContentResolver cr,
                                           Bitmap source,
                                           String title,
                                           String description) {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, title);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
        values.put(MediaStore.Images.Media.DESCRIPTION, description);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

        Uri url = null;
        String stringUrl = null;    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


            Log.e("Pinky", url.toString());

            if (source != null) {
                OutputStream imageOut = cr.openOutputStream(url);
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut);
                } finally {
                    imageOut.close();
                }

                long id = ContentUris.parseId(url);
                // Wait until MINI_KIND thumbnail is generated.
                Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
                // This is for backward compatibility.
                storeThumbnail(cr, miniThumb, id, 50F, 50F, MediaStore.Images.Thumbnails.MICRO_KIND);
            } else {
                cr.delete(url, null, null);
                url = null;
            }
        } catch (Exception e) {
            if (url != null) {
                cr.delete(url, null, null);
                url = null;
            }
        }

        if (url != null) {
            stringUrl = url.toString();
        }

        return stringUrl;
    }

    private static final Bitmap storeThumbnail(ContentResolver cr, Bitmap source, long id, float width, float height, int kind)
    {

        // create the matrix to scale it
        Matrix matrix = new Matrix();

        float scaleX = width / source.getWidth();
        float scaleY = height / source.getHeight();

        matrix.setScale(scaleX, scaleY);

        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );

        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND,kind);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID,(int)id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT,thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH,thumb.getWidth());

        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, thumbOut);
            thumbOut.close();
            return thumb;
        } catch (FileNotFoundException ex) {
            return null;
        } catch (IOException ex) {
            return null;
        }
    }


    /**
     * Adds a watermark on the given image.
     */
    public static Bitmap addWhiteBorder(Bitmap source, Context context) {
        int w, h;
        Canvas canvas;
        Paint paint;
        Bitmap bmp;

        w = source.getWidth();
        h = source.getHeight();
        RectF rectf = new RectF(0, 0, w + 40, h + 40);

    // Create the new bitmap
        try {
            bmp = Bitmap.createBitmap(w + 40, h + 40, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError ex) {
            throw new IllegalStateException("Low Memory");
        }

        paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG);
        canvas = new Canvas(bmp);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.WHITE);
//        canvas.drawPaint(paint);
        canvas.drawRoundRect(rectf, 10.f, 10.f, paint);
        canvas.drawBitmap(source, 20, 20, paint);
        return bmp;

    }





    public static boolean compareBitmaps(Bitmap bitmap1, Bitmap bitmap2)
    {
        if(bitmap1 == null || bitmap2 == null)
            return false;

        if (Build.VERSION.SDK_INT > 11)
        {
            return bitmap1.sameAs(bitmap2);
        }

        int chunkNumbers = 10;
        int rows, cols;
        int chunkHeight, chunkWidth;
        rows = cols = (int) Math.sqrt(chunkNumbers);
        chunkHeight = bitmap1.getHeight() / rows;
        chunkWidth = bitmap1.getWidth() / cols;

        int yCoord = 0;
        for (int x = 0; x < rows; x++)
        {
            int xCoord = 0;
            for (int y = 0; y < cols; y++)
            {
                try
                {
                    Bitmap bitmapChunk1 = Bitmap.createBitmap(bitmap1, xCoord, yCoord, chunkWidth, chunkHeight);
                    Bitmap bitmapChunk2 = Bitmap.createBitmap(bitmap2, xCoord, yCoord, chunkWidth, chunkHeight);

                    if (!sameAs(bitmapChunk1, bitmapChunk2))
                    {
                        recycleBitmaps(bitmapChunk1, bitmapChunk2);
                        return false;
                    }

                    recycleBitmaps(bitmapChunk1, bitmapChunk2);

                    xCoord += chunkWidth;
                }
                catch (Exception e)
                {
                    return false;
                }
            }
            yCoord += chunkHeight;
        }

        return true;
    }

    private static boolean sameAs(Bitmap bitmap1, Bitmap bitmap2)
    {
        // Different types of image
        if (bitmap1.getConfig() != bitmap2.getConfig())
            return false;

        // Different sizes
        if (bitmap1.getWidth() != bitmap2.getWidth())
            return false;

        if (bitmap1.getHeight() != bitmap2.getHeight())
            return false;

        int w = bitmap1.getWidth();
        int h = bitmap1.getHeight();

        int[] argbA = new int[w * h];
        int[] argbB = new int[w * h];

        bitmap1.getPixels(argbA, 0, w, 0, 0, w, h);
        bitmap2.getPixels(argbB, 0, w, 0, 0, w, h);

        // Alpha channel special check
        if (bitmap1.getConfig() == Bitmap.Config.ALPHA_8)
        {
            final int length = w * h;
            for (int i = 0; i < length; i++)
            {
                if ((argbA[i] & 0xFF000000) != (argbB[i] & 0xFF000000))
                {
                    return false;
                }
            }
            return true;
        }

        return Arrays.equals(argbA, argbB);
    }

    private static void recycleBitmaps(Bitmap bitmap1, Bitmap bitmap2)
    {
        bitmap1.recycle();
        bitmap2.recycle();
        bitmap1 = null;
        bitmap2 = null;
    }

}
