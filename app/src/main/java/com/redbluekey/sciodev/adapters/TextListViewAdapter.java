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
import com.redbluekey.sciodev.holders.TextList;
import com.redbluekey.sciodev.models.SectionTextList;

import java.util.List;

public class TextListViewAdapter extends RecyclerView.Adapter<TextList> {

    private static final String IMAGES_URL = "http://redbluekey.com/images";

    private Context context;
    private List<SectionTextList> textList;
    private MainActivity _mainActivity;

    public TextListViewAdapter(Context context, List<SectionTextList> textList, final MainActivity mainActivity) {
        this.context = context;
        this.textList = textList;
        this._mainActivity = mainActivity;
    }


    @Override
    public void onBindViewHolder(@NonNull TextList holder, final int position) {
        String imageUrl = textList.get(position).getImage();
        if (imageUrl != null && !imageUrl.equals("")) {
            if (imageUrl.startsWith("/")) {
                imageUrl = IMAGES_URL.concat(imageUrl);
            }
            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .into(holder.getImage());
            holder.getImage().setOnClickListener(getOnClickItemListener(textList.get(position).getTitle()));
        }
        String imageDescription = textList.get(position).getText();
        holder.getDescription().setText(imageDescription
                .length() > 140 ? imageDescription.substring(0, 140).concat("...") : imageDescription);
        holder.getTitle().setText(textList.get(position).getTitle());
        holder.getTitle().setOnClickListener(getOnClickItemListener(textList.get(position).getTitle()));
    }

    @Override
    public int getItemCount() {
        return textList.size();
    }

    @NonNull
    @Override
    public TextList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent
                .getContext()).inflate(R.layout.textlist_view_item, parent, false);

        return new TextList(view);
    }

    private View.OnClickListener getOnClickItemListener(final String itemName) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Runnable() {
                    @Override
                    public void run() {
                        _mainActivity.fetchInitialContent(itemName, false);
                    }
                }.run();
            }
        };
    }
}
