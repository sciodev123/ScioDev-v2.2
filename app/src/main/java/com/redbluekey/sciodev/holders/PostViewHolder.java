package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redbluekey.sciodev.R;

import lombok.Data;

@Data
public class PostViewHolder extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView title;

    public PostViewHolder(View itemView) {
        super(itemView);
        this.image = itemView.findViewById(R.id.search_item_image);
        this.title = itemView.findViewById(R.id.search_item_title);
    }
}
