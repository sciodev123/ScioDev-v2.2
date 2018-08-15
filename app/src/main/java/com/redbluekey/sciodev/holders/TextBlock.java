package com.redbluekey.sciodev.holders;

import android.widget.TextView;

import com.redbluekey.sciodev.models.SectionFact;

import java.util.List;

import lombok.Data;

@Data
public class TextBlock {

    private TextView textBlock;
    private List<SectionFact> facts;
}
