package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.redbluekey.sciodev.R;

import lombok.Data;

@Data
public class ImagesRibbon extends RecyclerView.ViewHolder {

    public ImagesRibbon(View itemView) {
        super(itemView);
        imageView = itemView.findViewById(R.id.image_ribbon_view_item);
    }

    private ImageView imageView;
}
