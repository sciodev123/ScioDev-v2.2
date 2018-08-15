package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.activities.SearchActivity;
import com.redbluekey.sciodev.holders.SearchItemHolder;
import com.redbluekey.sciodev.models.SearchItem;

import java.util.List;

public class SearchItemsAdapter extends RecyclerView.Adapter<SearchItemHolder> {

    private static final String IMAGES_URL = "http://redbluekey.com/images/";

    private Context context;
    private List<SearchItem> searchItems;
    private SearchActivity _searchActivity;

    public SearchItemsAdapter(Context context, List<SearchItem> searchItems, SearchActivity _searchActivity) {
        this.context = context;
        this.searchItems = searchItems;
        this._searchActivity = _searchActivity;
    }


    @Override
    public void onBindViewHolder(@NonNull SearchItemHolder holder, int position) {
        String imageUrl = searchItems.get(position).getImage();
        final String title = searchItems.get(position).getTitle();
        if (imageUrl != null && !imageUrl.equals("")) {
            Glide.with(context)
                    .asBitmap()
                    .load(IMAGES_URL.concat(imageUrl))
                    .into(holder.getImage());
        }
        holder.getTitle().setText(title);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _searchActivity.onSearchItemClick(title);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchItems.size();
    }

    @NonNull
    @Override
    public SearchItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent
                .getContext()).inflate(R.layout.search_item, parent, false);

        return new SearchItemHolder(view);
    }
}
