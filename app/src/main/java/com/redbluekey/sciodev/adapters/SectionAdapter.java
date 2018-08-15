package com.redbluekey.sciodev.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.redbluekey.sciodev.activities.MainActivity;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.activities.PostActivity;
import com.redbluekey.sciodev.enums.SectionType;
import com.redbluekey.sciodev.holders.ImagesRibbonSection;
import com.redbluekey.sciodev.holders.ImagesScrollSection;
import com.redbluekey.sciodev.holders.TextBlock;
import com.redbluekey.sciodev.holders.TextHeader;
import com.redbluekey.sciodev.holders.TextListSection;
import com.redbluekey.sciodev.holders.VideosScrollSection;
import com.redbluekey.sciodev.models.Section;
import com.redbluekey.sciodev.models.SectionFact;

import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class SectionAdapter extends BaseAdapter {

    private Context context;
    private List<Section> sectionList;
    private MainActivity _mainActivity;

    public SectionAdapter(Context context, List<Section> sectionList, final MainActivity mainActivity) {
        this.context = context;
        this.sectionList = sectionList;
        this._mainActivity = mainActivity;
    }

    @Override
    public int getCount() {
        return sectionList.size();
    }

    @Override
    public Section getItem(int position) {
        return sectionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final Section SECTION = getItem(position);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (SECTION.getType() != null) {
            if (SECTION.getType().equals(SectionType.textheader.name())) {
                view = inflater.inflate(R.layout.section_textheader, parent, false);
                TextHeader holder = new TextHeader();
                // Set views
                holder.setHeader((TextView) view.findViewById(R.id.textheaderN));
                holder.setSubHeader((TextView) view.findViewById(R.id.textheaderNS));
                // Set values
                holder.getHeader().setText(SECTION.getName());
                if (SECTION.getNameLink() != null && !SECTION.getNameLink().equals("-")
                        && !SECTION.getNameLink().equals("")) {
                    holder.getHeader().setOnClickListener(getOnClickListener(SECTION.getNameLink()));
                } else {
                    holder.getHeader().setOnClickListener(getOnClickListener(SECTION.getName()));
                }
                if (SECTION.getNameSub() == null || SECTION.getNameSub().equals("")) {
                    holder.getSubHeader().setHeight(0);
                } else {
                    holder.getSubHeader().setText(SECTION.getNameSub());
                    if (SECTION.getNameSubLink() != null && !SECTION.getNameSubLink().equals("-") &&
                            !SECTION.getNameSubLink().equals("")) {
                        holder.getSubHeader().setOnClickListener(getOnClickListener(SECTION.getNameSubLink()));
                    } else {
                        holder.getSubHeader().setOnClickListener(getOnClickListener(SECTION.getNameSub()));
                    }
                }
                view.setTag(holder);
            } else if (SECTION.getType().equals(SectionType.textblock.name())) {
                view = inflater.inflate(R.layout.section_textblock, parent, false);
                TextBlock holder = new TextBlock();
                // Set views
                holder.setTextBlock((TextView) view.findViewById(R.id.textblock));
                holder.getTextBlock().setMovementMethod(LinkMovementMethod.getInstance());
                // Set values
                holder.getTextBlock().setText(addClickablePart(SECTION.getTextBlock(), SECTION.getFacts()), TextView.BufferType.SPANNABLE);
                view.setTag(holder);


                FabSpeedDial fabSpeedDial = view.findViewById(R.id.fab_add_comment);
                fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {

                    @Override
                    public boolean onMenuItemSelected(MenuItem menuItem) {

                        int nPostMode  = 0;
                        switch (menuItem.getItemId()) {

                            case R.id.action_post_image:
                                nPostMode = 0;
                                break;

                            case R.id.action_post_link:
                                nPostMode = 1;
                                break;

                            case R.id.action_post_text:
                                nPostMode = 2;
                                break;
                        }

                        Intent intent = new Intent(context, PostActivity.class);
                        intent.putExtra("post_mode", nPostMode);
                        intent.putExtra("section_name", SECTION.getName());
                        context.startActivity(intent);

                        Toast.makeText(context, position + "; " + SECTION.getName() + "; " + menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

            } else if (SECTION.getType().equals(SectionType.imagesribbon.name())) {
                view = inflater.inflate(R.layout.section_imagesribbon, parent, false);
                ImagesRibbonSection holder = new ImagesRibbonSection();
                // Set values
                LinearLayoutManager layoutManager = new LinearLayoutManager(
                        context, LinearLayoutManager.HORIZONTAL, false);
                RecyclerView images = view.findViewById(R.id.imagesribbon_images);
                images.setLayoutManager(layoutManager);
                ImagesRibbonViewAdapter adapter = new ImagesRibbonViewAdapter(context, SECTION.getImagesRibbon());
                images.setAdapter(adapter);
                holder.setImages(images);
                view.setTag(holder);
            } else if (SECTION.getType().equals(SectionType.imagesscroll.name())) {
                view = inflater.inflate(R.layout.section_imagesscroll, parent, false);
                ImagesScrollSection holder = new ImagesScrollSection();
                // Set views
                holder.setTitle((TextView) view.findViewById(R.id.imagesscroll_header));
                holder.setPropertyAvailable((TextView) view.findViewById(R.id.imagesscroll_pa));
                // Set values
                holder.getTitle().setText(SECTION.getName());
                if (SECTION.getNameLink() != null && !SECTION.getNameLink().equals("")
                        && !SECTION.getNameLink().equals("-")) {
                    holder.getTitle().setOnClickListener(getOnClickListener(SECTION.getNameLink()));
                } else {
                    holder.getTitle().setOnClickListener(getOnClickListener(SECTION.getName()));
                }
                if (SECTION.getPropertyAvailable() != null && SECTION.getPropertyAvailable().size() > 0) {
                    holder.getPropertyAvailable()
                            .setText("(".concat(SECTION
                                    .getPropertyAvailable().get(0).getTitle()).concat(")"));
                    holder.getPropertyAvailable()
                            .setOnClickListener(getOnClickListener(SECTION
                                    .getPropertyAvailable().get(0).getLink()));
                }

                LinearLayoutManager layoutManager = new LinearLayoutManager(
                        context, LinearLayoutManager.HORIZONTAL, false);
                RecyclerView images = view.findViewById(R.id.imagesscroll_images);
                images.setLayoutManager(layoutManager);
                ImagesViewAdapter adapter = new ImagesViewAdapter(
                        context, SECTION.getImagesScroll(), _mainActivity);
                images.setAdapter(adapter);
                holder.setImages(images);
                view.setTag(holder);
            } else if (SECTION.getType().equals(SectionType.videosribbon.name())) {
                view = inflater.inflate(R.layout.section_videosribbon, parent, false);
                VideosScrollSection holder = new VideosScrollSection();
                holder.setTitle((TextView) view.findViewById(R.id.videosribbon_header));
                LinearLayoutManager layoutManager = new LinearLayoutManager(
                        context, LinearLayoutManager.HORIZONTAL, false);
                RecyclerView videos = view.findViewById(R.id.videosribbon);
                videos.setLayoutManager(layoutManager);
                VideosViewAdapter adapter = new VideosViewAdapter(context, SECTION.getVideosRibbon());
                videos.setAdapter(adapter);

                // Set values
                holder.setVideos(videos);
                holder.getTitle().setText(SECTION.getName());
                view.setTag(holder);
            } else if (SECTION.getType().equals(SectionType.textlist.name())) {
                view = inflater.inflate(R.layout.section_textlist, parent, false);
                TextListSection holder = new TextListSection();
                // Set views
                holder.setTitle((TextView) view.findViewById(R.id.textlist_header));
                // Set values
                holder.getTitle().setText(SECTION.getName());
                if (SECTION.getNameLink() != null && !SECTION.getNameLink().equals("-")
                        && !SECTION.getNameLink().equals("")) {
                    holder.getTitle().setOnClickListener(getOnClickListener(SECTION.getNameLink()));
                } else {
                    holder.getTitle().setOnClickListener(getOnClickListener(SECTION.getName()));
                }
                LinearLayoutManager layoutManager = new LinearLayoutManager(
                        context, LinearLayoutManager.VERTICAL, false);
                RecyclerView textList = view.findViewById(R.id.textlist_content);
                textList.setLayoutManager(layoutManager);
                TextListViewAdapter adapter = new TextListViewAdapter(context, SECTION.getTextList(), _mainActivity);
                textList.setAdapter(adapter);
                holder.setContent(textList);
                view.setTag(holder);
            }
        } else {
            view = inflater.inflate(R.layout.textlist_view_item, parent, false);
        }

        return view;
    }

    private View.OnClickListener getOnClickListener(final String itemName) {
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

    private SpannableStringBuilder addClickablePart(String str, List<SectionFact> facts) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(str);
        int idx1 = str.indexOf("{");
        int idx2;
        while (idx1 != -1) {
            idx2 = ssb.toString().indexOf("}", idx1);
            if (idx2 != -1) {
                final String clickString = ssb.toString().substring(idx1 + 1, idx2);
                ssb = ssb.delete(idx1, idx1 + 1);
                ssb = ssb.delete(idx2 - 1, idx2);
                ssb.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        _mainActivity.fetchInitialContent(clickString, false);
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                }, idx1, idx2 - 1, 0);
                idx1 = ssb.toString().indexOf("{", idx2 - 2);
            } else {
                idx1 = -1; //necessary to stop the parsing as sometimes the description does not have the correct ending bracket }
            }
        }

        ssb = ssb.append("\n");

        for (SectionFact fact : facts) {
            ssb = ssb.append(fact.getFact()).append(": ");
            idx1 = ssb.length();
            for (int i = 0; i < fact.getTag().size(); i++) {
                final String factTag = fact.getTag().get(i);
                ssb = ssb.append(factTag);
                ssb = ssb.append(fact.getTag().size() - i != 1 ? ", " : "");
                if (factTag.startsWith("+")) {
                    ssb = ssb.append(" more");
                } else {
                    ssb.setSpan(new ClickableSpan() {
                        @Override
                        public void onClick(View widget) {
                            _mainActivity.fetchInitialContent(factTag, false);
                        }
                        @Override
                        public void updateDrawState(TextPaint ds) {
                            ds.setColor(context.getResources().getColor(R.color.colorPrimary));
                        }
                    }, idx1, idx1 + factTag.length(), 0);
                }
                idx1 = idx1 + factTag.length() + 2;
            }
            ssb = ssb.append("\n");
        }

        return ssb;
    }
}
