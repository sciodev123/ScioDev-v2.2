package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.redbluekey.sciodev.R;

import lombok.Data;

@Data
public class TextList extends RecyclerView.ViewHolder {

    private ImageView image;
    private TextView description;
    private TextView title;

    public TextList(View itemView) {
        super(itemView);
        image = itemView.findViewById(R.id.textlist_view_item_image);
        description = itemView.findViewById(R.id.textlist_view_item_description);
        title = itemView.findViewById(R.id.textlist_view_item_title);
    }
}
