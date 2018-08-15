package com.redbluekey.sciodev.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ProgressBar;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.adapters.FullScreenImageAdapter;
import com.redbluekey.sciodev.swipe.SwipeBackActivity;
import com.redbluekey.sciodev.swipe.SwipeBackLayout;

public class ImagePreviewActivity extends SwipeBackActivity {

    public static final String PREVIEW_IMAGE_LINK = "fullSizePhotoUrls";
    public static final String START_POSITION = "startPos";
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        setDragEdge(SwipeBackLayout.DragEdge.TOP);


        viewPager = (ViewPager) findViewById(R.id.pager);


        Intent i = getIntent();
        int position = i.getIntExtra(START_POSITION, 0);
        String[] urls = i.getStringArrayExtra(PREVIEW_IMAGE_LINK);

        adapter = new FullScreenImageAdapter(this,
                urls);

        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

    }

    public void close(View view) {
        finish();
    }
}