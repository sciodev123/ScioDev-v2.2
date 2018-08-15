package com.redbluekey.sciodev.activities;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.TextView;

import com.redbluekey.sciodev.R;
import com.redbluekey.sciodev.fragments.PostFragment;
import com.redbluekey.sciodev.fragments.SectionsFragment;
import com.redbluekey.sciodev.helpers.ConectivityStatus;
import com.redbluekey.sciodev.helpers.HttpClient;
import com.redbluekey.sciodev.models.SiteContent;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends FragmentActivity implements ActionBar.TabListener, Parcelable {

    private static final String ITEM_NAME = "itemName";
    private static final String
            GET_ITEMS_URL = "http://api.redbluekey.com/api/item/GetItems?name=%s&type=Partial";

    private ActionBar actionBar;
    private String itemName = "";
    private SiteContent initialContent = new SiteContent();
    private ViewPager mViewPager;
    private String lastSearchQuery = "";

    Deque<String> itemNames = new LinkedList<>();

    private RelativeLayout loaderLayout;

    private List<Fragment> fragmentList = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTheme(R.style.MainTheme);

        if (savedInstanceState == null) {
            actionBar = getActionBar();
            if (actionBar != null) {
                actionBar.setHomeButtonEnabled(false);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
            }
            loaderLayout = findViewById(R.id.loadItemsLayout_listView);
            mViewPager = findViewById(R.id.pager);
            mViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                @Override
                public void onPageSelected(int position) {
                    actionBar.setSelectedNavigationItem(position);
                }
            });

            //display the search activity when the user first logs in
            Intent search = new Intent(this, SearchActivity.class);
            search.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            search.putExtra(SearchActivity.QUERY, lastSearchQuery);
            this.startActivityForResult(search, SearchActivity.QUERY_REQUEST_CODE);

            //fetchInitialContent("Jupiter", false);
        } else {
            fetchInitialContent(savedInstanceState.getString(ITEM_NAME), false);
        }

        mViewPager.setOffscreenPageLimit(3); //prevent the off screen pages from being destroyed so that a user can navigate away and back.
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equals("MenuBuilder")) {
                try {
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "onMenuOpened...unable to set icons for overflow menu", e);
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings: {
                Intent settings = new Intent(this, SettingsScreen.class);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(settings);
                return true;
            }
            case R.id.search: {
                Intent search = new Intent(this, SearchActivity.class);
                search.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                search.putExtra(SearchActivity.QUERY, lastSearchQuery);
                this.startActivityForResult(search, SearchActivity.QUERY_REQUEST_CODE);
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == resultCode) {
            searchItems(data.getStringExtra(SearchActivity.QUERY));
        } else if (resultCode == -1 && data.hasExtra(SearchActivity.QUERY)) {
            lastSearchQuery = data.getStringExtra(SearchActivity.QUERY);
        }
    }

    public void searchItems(String query) {
        fetchInitialContent(query, false);
    }

    @Override
    public void onBackPressed() {
        if (itemNames.size() > 0 && itemNames.peekLast().equals(itemName)) {
            itemNames.removeLast();
        }
        if (itemNames.size() == 0) {
            finishAffinity();
        } else {
            fetchInitialContent(itemNames.pollLast(), true);
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    private class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        private AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragmentList.get(i);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putString(ITEM_NAME, itemName);
        super.onSaveInstanceState(savedInstanceState);
    }


    public void fetchInitialContent(String name, boolean isBack) {
        if (!isBack) {
            itemNames.add(name);
        }
        if (!name.equals(itemName)) {
            itemName = name;
            if (ConectivityStatus.hasInternetConnection(getApplicationContext())) {
                new Runnable() {
                    @Override
                    public void run() {
                        new HttpRequestGetLandingPage().execute();
                    }
                }.run();
            } else {
                Toast.makeText(getApplicationContext(), "Please check your internet connection",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setTabsTitles() {

        actionBar.addTab(
                actionBar.newTab()
                        .setText("POST")
                        .setTabListener(this));
        Fragment postFragment = new PostFragment();
        Bundle postArgs = new Bundle();
        postArgs.putString(PostFragment.ITEM_NAME, itemName);
        postFragment.setArguments(postArgs);
        fragmentList.add(postFragment);


        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("POST");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, 0, android.R.drawable.arrow_down_float, 0);
        actionBar.getTabAt(0).setCustomView(tabOne);



        for (int i = 0; i < initialContent.getPages().size(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(initialContent.getPages().get(i).getName())
                            .setTabListener(this));
            Fragment overviewFragment = new SectionsFragment();
            Bundle overviewArgs = new Bundle();
            overviewArgs.putInt(SectionsFragment.PAGE_INDEX_ARG, i + 1);
            overviewArgs.putString(SectionsFragment.ITEM_NAME, itemName);
            overviewArgs.putParcelableArrayList(SectionsFragment.INITIAL_SECTIONS_ARG,
                    (ArrayList) initialContent.getPages().get(i).getSections());
            overviewArgs.putParcelable("MAIN", this);
            overviewFragment.setArguments(overviewArgs);

            fragmentList.add(overviewFragment);
        }

        //set padding for the top of the view so that the action bar doesn't hide content
        mViewPager.setPadding(0, actionBar.getHeight(), 0, 0);
    }

    private void setInitialContent() {
        AppSectionsPagerAdapter mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mAppSectionsPagerAdapter.notifyDataSetChanged();
    }

    private class HttpRequestGetLandingPage extends AsyncTask<Void, Void, SiteContent> {
        @Override
        protected SiteContent doInBackground(Void... params) {
            try {
                return HttpClient.client
                        .getForObject(String.format(GET_ITEMS_URL, itemName), SiteContent.class);
            } catch (Exception e) {
                Log.e("MainActivity", e.getMessage(), e);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            fragmentList.clear();
            actionBar.removeAllTabs();
            loaderLayout.setVisibility(View.VISIBLE);
            setInitialContent();
        }

        @Override
        protected void onPostExecute(SiteContent siteContent) {
            initialContent = siteContent;
            setTabsTitles();
            setInitialContent();
            loaderLayout.setVisibility(View.GONE);

            //Set focus to the second tab. "Overview", not "Posts".
            mViewPager.setCurrentItem(1);
        }
    }
}

