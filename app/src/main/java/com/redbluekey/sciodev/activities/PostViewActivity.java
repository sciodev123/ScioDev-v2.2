package com.redbluekey.sciodev.activities;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.adapters.PostViewAdapter;
import com.redbluekey.sciodev.fragments.PostFragment;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.models.SearchItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class PostViewActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "SearchActivity";

    private static final String
            SEARCH_URL = "http://api.redbluekey.com/api/Item/GetTagsByStartingLetter?letter=%s";

    public static final String QUERY = "QUERY";
    public static final int QUERY_REQUEST_CODE = 8;
    private static final int MIN_TIME_BETWEEN_SEARCHES = 600;

    private List<SearchItem> searchItems = new ArrayList<>();
    private List<SearchItem> searchItems2 = new ArrayList<>();
    RecyclerView postViewList;
    RecyclerView postViewItem;
    PostViewAdapter postViewAdapter;
    PostViewAdapter postViewAdapter2;

    private ViewPager mViewPager;
    private List<Fragment> fragmentList = new ArrayList<>();

    ImageButton backButton;
    TextInputLayout searchInput;
    ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        backButton = findViewById(R.id.btn_search_back);
        backButton.setOnClickListener(this);
        progressBar = findViewById(R.id.search_progress_bar);
        //InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

        initSearchInput();
        initSearchResultList();
        String lastQuery = getIntent().getStringExtra(QUERY);
        if (!lastQuery.equals("")) {
            searchInput.getEditText().setText(lastQuery);
        }
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void initSearchInput() {
        searchInput = findViewById(R.id.search_input);
        searchInput.getEditText().addTextChangedListener(new TextWatcher() {
            Timer timer;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (timer != null) {
                    timer.cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                final String query = s.toString();
                if (query.length() >= 1) {
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            search(query);
                        }
                    }, MIN_TIME_BETWEEN_SEARCHES);
                }
            }
        });
    }

    private void showProgressbar(final boolean show) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show) {
                    postViewList.setVisibility(View.GONE);
                    postViewItem.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                } else {
                    progressBar.setVisibility(View.GONE);
                    postViewList.setVisibility(View.VISIBLE);
                    postViewItem.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initSearchResultList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        postViewList = findViewById(R.id.search_item_list);
        postViewList.setLayoutManager(layoutManager);
        postViewAdapter = new PostViewAdapter(getApplicationContext(), searchItems, this);
        postViewList.setAdapter(postViewAdapter);


        LinearLayoutManager layoutManager2 = new LinearLayoutManager(
                getApplicationContext(), LinearLayoutManager.VERTICAL, false);

        postViewItem = findViewById(R.id.search_item_item);
        postViewItem.setLayoutManager(layoutManager2);
        postViewAdapter2 = new PostViewAdapter(getApplicationContext(), searchItems, this);
        postViewItem.setAdapter(postViewAdapter2);
    }

    public void onSearchItemClick(String query) {
        Intent intent = new Intent();
        intent.putExtra(QUERY, query);
        setResult(QUERY_REQUEST_CODE, intent);
        finish();
    }

    private void updateSearchResult(List<SearchItem> response) {
        //load the post comments
        searchItems = response;
        postViewAdapter = new PostViewAdapter(getApplicationContext(), searchItems, this);
        postViewList.setAdapter(postViewAdapter);

        //load the post
        searchItems2 = response;
        postViewAdapter2 = new PostViewAdapter(getApplicationContext(), searchItems, this);
        postViewItem.setAdapter(postViewAdapter2);

        showProgressbar(false);
    }



    @Override
    public void onClick(View v) {
        goBack();
    }

    private void goBack() {
        Intent intentData = new Intent();
        intentData.putExtra(QUERY, searchInput.getEditText().getText().toString());
        setResult(-1, intentData);
        finish();
    }

    private void search(final String query) {
        if (ConectivityStatus.hasInternetConnection(this)) {
            new Runnable() {
                @Override
                public void run() {
                    new HttpRequestSearch().execute(query);
                }
            }.run();
        } else {
            Toast.makeText(this, "Please check your internet connection",
                    Toast.LENGTH_SHORT).show();
        }
    }

    class HttpRequestSearch extends AsyncTask<String, Void, SearchItem[]> {
            @Override
            protected SearchItem[] doInBackground(String... params) {
                try {
                    return HttpClient.client.getForObject(String.format(SEARCH_URL, params[0]), SearchItem[].class);
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage(), e);
                }
                return null;
            }

        @Override
        protected void onPreExecute() {
            showProgressbar(true);
        }

        @Override
        protected void onPostExecute(SearchItem[] response) {
            updateSearchResult(Arrays.asList(response));
        }
    }
}
