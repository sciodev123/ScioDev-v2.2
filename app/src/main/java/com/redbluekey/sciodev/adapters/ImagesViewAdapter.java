package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.redbluekey.sciodev.activities.MainActivity;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.holders.ImagesScroll;
import com.redbluekey.sciodev.models.SectionImageScroll;

import java.util.List;

public class ImagesViewAdapter extends RecyclerView.Adapter<ImagesScroll> {

    private static final String IMAGES_URL = "http://redbluekey.com/images";

    private Context context;
    private List<SectionImageScroll> images;
    private MainActivity _mainActivity;

    public ImagesViewAdapter(Context context, List<SectionImageScroll> images, final MainActivity mainActivity) {
        this.context = context;
        this.images = images;
        this._mainActivity = mainActivity;
    }


    @Override
    public void onBindViewHolder(@NonNull ImagesScroll holder, final int position) {
        if (images.get(position).getImageUri() != null
                && !images.get(position).getImageUri() .equals("")) {
            Glide.with(context)
                    .asBitmap()
                    .load(IMAGES_URL + images.get(position).getImageUri())
                    .into(holder.getImageView());
            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new Runnable() {
                        @Override
                        public void run() {
                            _mainActivity.fetchInitialContent(images.get(position).getTag(), false);
                        }
                    }.run();
                }
            });
        }
        String imageTitle = images.get(position).getTitle();
        holder.getHeader().setText(imageTitle
                .length() > 20 ? imageTitle.substring(0, 20).concat("...") : imageTitle);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    @NonNull
    @Override
    public ImagesScroll onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.images_view_item, parent, false);

        return new ImagesScroll(view);
    }
}
