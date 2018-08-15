package com.redbluekey.sciodev.cameraGalleryPicker;

import android.net.Uri;

/**
 * Created by Mickael on 7/23/2017.
 */

public class GalleryImage {
    private Uri bitmapUri;
    private String imagePath;

    public GalleryImage(Uri bitmapUri, String imagePath) {
        this.bitmapUri = bitmapUri;
        this.imagePath = imagePath;
    }

    public Uri getBitmapUri() {
        return bitmapUri;
    }

    public void setBitmapUri(Uri bitmapUri) {
        this.bitmapUri = bitmapUri;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
