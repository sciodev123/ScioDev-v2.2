package com.redbluekey.sciodev.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.activities.MainActivity;
import com.redbluekey.sciodev.adapters.SectionAdapter;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.UriBuilder;
import com.redbluekey.sciodev.models.Section;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SectionsFragment extends Fragment implements ObservableScrollViewCallbacks {

    private static final String TAG = "SectionsFragment";

    public static final String PAGE_INDEX_ARG = "pageIndex";
    public static final String INITIAL_SECTIONS_ARG = "initialSections";
    public static final String ITEM_NAME = "itemName";

    private static final RestTemplate restTemplate = new RestTemplate();

    private SectionAdapter sectionAdapter;
    private List<Section> sectionList;
    private ObservableListView sectionListView;

    private MainActivity _mainActivity;

    private View view;
    int firstItem;
    private RelativeLayout bottomLayout;

    private int mPageIndex;
    private int mCurrentPage;
    private String itemName;
    private boolean mLoading = false;
    private boolean mLastPage;
    private boolean userScrolled = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        view = inflater.inflate(R.layout.overview_fragment, container, false);
        Bundle args = getArguments();
        List<Section> initialSections = args.getParcelableArrayList(INITIAL_SECTIONS_ARG);
        _mainActivity = args.getParcelable("MAIN");
        if (!_mainActivity.getActionBar().isShowing()) {
            _mainActivity.getActionBar().show();
        }
        mPageIndex = args.getInt(PAGE_INDEX_ARG);
        itemName = args.getString(ITEM_NAME);
        mCurrentPage = initialSections.size();
        mLastPage = false;
        init(initialSections);
        implementScrollListener();
        return view;
    }

    private void implementScrollListener() {
        sectionListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView arg0, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    userScrolled = true;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                mLastFirstVisibleItem = firstVisibleItem;
                firstItem = firstVisibleItem;
                if (!mLastPage && userScrolled && !mLoading
                        && (firstVisibleItem + visibleItemCount + 4 > totalItemCount)) { //+8 is to start loading sections earlier than the complete bottom of the page
                    userScrolled = false;
                    mLoading = true;
                    if (ConectivityStatus.hasInternetConnection(getActivity().getApplicationContext())) {
                        new Runnable() {
                            @Override
                            public void run() {
                                new HttpRequestGetSections().execute();
                            }
                        }.run();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Please check your internet connection", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    private void init(final List<Section> initialSections) {

        bottomLayout = view.findViewById(R.id.loadItemsLayout_listView);
        sectionListView = view.findViewById(R.id.listView);
        sectionListView.setScrollViewCallbacks(this);
        populatRecyclerView(initialSections);
        if (ConectivityStatus.hasInternetConnection(this.getContext())) {
            for (int i = 0; i < 1; i++) {
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                new HttpRequestGetSections().execute();
                            }
                        }, (TimeUnit.MILLISECONDS.toMillis(350)));
            }
        } else {
            Toast.makeText(this.getContext(), "Please check your internet connection",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void populatRecyclerView(List<Section> initialSections) {
        sectionList = new ArrayList<>();
        sectionList.addAll(initialSections);
        sectionAdapter = new SectionAdapter(getActivity(), sectionList, (MainActivity) getActivity());
        sectionListView.setAdapter(sectionAdapter);
        sectionAdapter.notifyDataSetChanged();
        bottomLayout.setVisibility(View.GONE);

    }

    private void updateListView(final List<Section> sections) {
        sectionList.addAll(sections);
        sectionAdapter.notifyDataSetChanged();
        mLoading = false;
    }

    // Observable List View scroll listeners
    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
        //Hide the action bar if the user scrolls down. Has been disabled as padding needs to be
        //added to the top of the items to prevent them being hidden by the ActionBar.
        /*ActionBar ab = _mainActivity.getActionBar();
        if (ab == null) {
            return;
        }
        if (scrollState == ScrollState.UP) {
            if (ab.isShowing()) {
                ab.hide();
            }
        } else if (scrollState == ScrollState.DOWN) {
            if (!ab.isShowing()) {
                ab.show();
            }
        }*/
    }

    private class HttpRequestGetSections extends AsyncTask<Void, Void, List<Section>> {
        @Override
        protected List<Section> doInBackground(Void... params) {
            try {
                restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
                Section[] response = restTemplate
                        .getForObject(UriBuilder.getRequestUrl(
                                itemName, mPageIndex-1, mCurrentPage, mCurrentPage + 4), Section[].class);
                if (response.length > 0) {
                    mCurrentPage = mCurrentPage + 5;
                } else {
                    mLastPage = true;
                }
                return Arrays.asList(response);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            bottomLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(List<Section> sections) {
            bottomLayout.setVisibility(View.GONE);
            updateListView(sections);
        }
    }
}
