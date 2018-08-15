package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redbluekey.sciodev.R;

import lombok.Data;

@Data
public class VideosScroll extends RecyclerView.ViewHolder {

    public VideosScroll(View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.video_view_item_header);
        channel = itemView.findViewById(R.id.video_view_item_channel);
        imageView = itemView.findViewById(R.id.video_view_item);
    }

    private TextView header;
    private TextView channel;
    private ImageView imageView;
}
