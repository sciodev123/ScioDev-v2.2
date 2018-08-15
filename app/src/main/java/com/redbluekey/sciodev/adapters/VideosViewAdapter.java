package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.holders.VideosScroll;
import com.redbluekey.sciodev.models.SectionVideosRibbon;

import java.util.List;

public class VideosViewAdapter extends RecyclerView.Adapter<VideosScroll> {

    private static final String YOUTUBE_URL = "https://www.youtube.com/watch?v=";

    private Context context;
    private List<SectionVideosRibbon> videos;

    public VideosViewAdapter(Context context, List<SectionVideosRibbon> videos) {
        this.context = context;
        this.videos = videos;
    }


    @Override
    public void onBindViewHolder(@NonNull VideosScroll holder, final int position) {
        Glide.with(context)
                .asBitmap()
                .load(videos.get(position).getThumbnail())
                .into(holder.getImageView());
        final String videoTitle = videos.get(position).getTitle();
        holder.getHeader().setText(videoTitle
                .length() > 40 ? videoTitle.substring(0, 40).concat("...") : videoTitle);
        holder.getChannel().setText(videos.get(position).getChannel());

        final View.OnClickListener onClickVideoListener =
                getOnClickVideoListener(videos.get(position).getVideoUrl());

        // Set click listeners
        holder.getImageView().setOnClickListener(onClickVideoListener);
        holder.getHeader().setOnClickListener(onClickVideoListener);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    @NonNull
    @Override
    public VideosScroll onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater
                .from(parent.getContext()).inflate(R.layout.videos_view_item, parent, false);

        return new VideosScroll(view);
    }

    private View.OnClickListener getOnClickVideoListener(final String videoUrl) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_URL + videoUrl));
                context.startActivity(intent);
            }
        };
    }
}
