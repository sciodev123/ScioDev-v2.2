package com.redbluekey.sciodev.cameraGalleryPicker;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;


import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.cameraGalleryPicker.GalleryImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Mickael on 7/23/2017.
 */

public class ImagesPickerManager {
    private static final int SELECT_REAL_IMAGE_REQUEST = 500;

    public static void startPicker(Activity activity)
    {
        Matisse.from(activity)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(false)
                .capture(false)
                .theme(R.style.Matisse_Dracula)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .gridExpectedSize(
                        activity.getResources().getDimensionPixelSize(R.dimen.preview_image_size))
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(SELECT_REAL_IMAGE_REQUEST);
    }

    public static void startPicker(Fragment fragment)
    {
        Matisse.from(fragment)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG))
                .countable(false)
                .capture(false)
                .theme(R.style.Matisse_Dracula)
                .showSingleMediaType(true)
                .maxSelectable(1)
                .gridExpectedSize(
                        fragment.getResources().getDimensionPixelSize(R.dimen.preview_image_size))
                .thumbnailScale(0.85f)
                .imageEngine(new PicassoEngine())
                .forResult(SELECT_REAL_IMAGE_REQUEST);
    }

    public static GalleryImage handlePickerResult(int requestCode, int resultCode, Intent data)
    {

        if (requestCode != SELECT_REAL_IMAGE_REQUEST || resultCode != RESULT_OK || data == null)
            return null;

        List<Uri> results;
        List<String> pathResults;
        if( (results = Matisse.obtainResult(data)) == null || results.get(0) == null ||
                (pathResults = Matisse.obtainPathResult(data)) == null || pathResults.get(0) == null)
            return null;

        return new GalleryImage(results.get(0), pathResults.get(0));

    }


}
