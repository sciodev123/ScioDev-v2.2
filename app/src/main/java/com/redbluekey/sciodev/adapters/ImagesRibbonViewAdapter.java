package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.activities.ImagePreviewActivity;
import com.redbluekey.sciodev.holders.ImagesRibbon;
import com.redbluekey.sciodev.models.SectionImageRibbon;

import java.util.List;

public class ImagesRibbonViewAdapter extends RecyclerView.Adapter<ImagesRibbon> {

    private static final String IMAGES_URL = "http://redbluekey.com/images";

    private Context context;
    private List<SectionImageRibbon> images;

    public ImagesRibbonViewAdapter(Context context, List<SectionImageRibbon> images) {
        this.context = context;
        this.images = images;
    }


    @Override
    public void onBindViewHolder(@NonNull ImagesRibbon holder, final int position) {
        String thumbnailUrl = images.get(position).getThumbnail();
        if (thumbnailUrl != null && !thumbnailUrl.equals("")) {
            if (thumbnailUrl.startsWith("/")) {
                thumbnailUrl = IMAGES_URL.concat(thumbnailUrl);
            }
            Glide.with(context)
                    .asBitmap()
                    .load(thumbnailUrl)
                    .into(holder.getImageView());
            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ImagePreviewActivity.class);
                    String[] imageUrls = new String[images.size()];
                    for (int i = 0; i < images.size(); i++) {
                        imageUrls[i] = images.get(i).getImage();
                    }
                            intent.putExtra(ImagePreviewActivity.PREVIEW_IMAGE_LINK, imageUrls);
                    intent.putExtra(ImagePreviewActivity.START_POSITION, position);
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @NonNull
    @Override
    public ImagesRibbon onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.images_ribbon_view_item, parent, false);

        return new ImagesRibbon(view);
    }
}
