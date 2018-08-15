package com.redbluekey.sciodev.holders;

import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import lombok.Data;

@Data
public class VideosScrollSection {
    private TextView title;
    private RecyclerView videos;
}
