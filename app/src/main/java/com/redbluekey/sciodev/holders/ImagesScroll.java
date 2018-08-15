package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redbluekey.sciodev.R;

import lombok.Data;

@Data
public class ImagesScroll extends RecyclerView.ViewHolder {

    public ImagesScroll(View itemView) {
        super(itemView);
        header = itemView.findViewById(R.id.image_view_item_header);
        imageView = itemView.findViewById(R.id.image_view_item);
    }

    private TextView header;
    private ImageView imageView;
}
