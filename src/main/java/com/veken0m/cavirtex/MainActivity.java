package com.veken0m.cavirtex;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.analytics.tracking.android.EasyTracker;
import com.veken0m.cavirtex.fragments.HomeMenuFragment;
import com.veken0m.cavirtex.preferences.PreferencesActivity;
import com.veken0m.cavirtex.preferences.PriceAlertPreferencesActivity;

import java.util.ArrayList;
import com.veken0m.utils.KarmaAdsUtils;

/**
 * @author Michael Lagacé a.k.a. veken0m
 * @version 1.9.1 Jan 12 2014
 */
public class MainActivity extends ActionBarActivity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTabbedActionBar();
        // Some hack to make widgets appear on devices without rebooting
        sendBroadcast(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));

        KarmaAdsUtils.initAd(this);
    }

    void initTabbedActionBar() {
        ViewPager mViewPager = (ViewPager) findViewById(R.id.pager);

        // ActionBar gets initiated
        ActionBar actionbar = getSupportActionBar();

        Bundle extras = getIntent().getExtras();
        String exchange = (extras != null) ? extras.getString("exchangeKey") : "";

        TabsAdapter tabsAdapter = new TabsAdapter(this, actionbar, mViewPager);
        addTab(actionbar, tabsAdapter, exchange, HomeMenuFragment.class);

        actionbar.show();
    }

    private void addTab(ActionBar actionbar, TabsAdapter tabsAdapter, String identity, Class<? extends Fragment> viewFragment) {

        ActionBar.Tab tab = actionbar.newTab();//.setText(identity);

        Bundle args = new Bundle();
        args.putString("exchange", identity);

        tabsAdapter.addTab(tab, viewFragment, args, identity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                return true;
            case R.id.action_alarm_preferences:
                startActivity(new Intent(this, PriceAlertPreferencesActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onStart() {
        super.onStart();
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean("googleAnalyticsPref", false))
            EasyTracker.getInstance(this).activityStart(this);
    }

    public void onPause() {
        super.onPause();
        // clear the extra
        getIntent().removeExtra("exchangeKey");
    }

    @Override
    public void onStop() {
        super.onStop();
        EasyTracker.getInstance(this).activityStop(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        setIntent(intent);
    }

    /**
     * Obtained from: https://gist.github.com/2424383 This is a helper class
     * that implements the management of tabs and all details of connecting a
     * ViewPager with associated TabHost. It relies on a trick. Normally a tab
     * host has a simple API for supplying a View or Intent that each tab will
     * show. This is not sufficient for switching between pages. So instead we
     * make the content part of the tab host 0dp high (it is not shown) and the
     * TabsAdapter supplies its own dummy view to show as the tab content. It
     * listens to changes in tabs, and takes care of switch to the correct paged
     * in the ViewPager whenever the selected tab changes.
     */
    public static class TabsAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener, ActionBar.TabListener {

        private final Context mContext;
        private final ActionBar mBar;
        private final ViewPager mViewPager;
        private final ArrayList<TabInfo> mTabs = new ArrayList<TabInfo>();

        public TabsAdapter(ActionBarActivity activity, ActionBar bar, ViewPager pager) {

            super(activity.getSupportFragmentManager());
            mContext = activity;
            mBar = bar;
            mViewPager = pager;
            mViewPager.setAdapter(this);
            mViewPager.setOnPageChangeListener(this);
        }

        public void addTab(ActionBar.Tab tab, Class<? extends Fragment> clss, Bundle args, String ident) {
            TabInfo info = new TabInfo(clss, args, ident);
            tab.setTag(info);
            tab.setTabListener(this);
            mTabs.add(info);
            mBar.addTab(tab);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTabs.size();
        }

        @Override
        public Fragment getItem(int position) {
            TabInfo info = mTabs.get(position);
            return Fragment.instantiate(mContext, info.clss.getName(),
                    info.args);
        }

        public int getIndexForIdentity(String identity) {
            for (int i = 0; i < mTabs.size(); i++) {
                TabInfo info = mTabs.get(i);
                if (identity.equals(info.ident))
                    return i;
            }
            return -1;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mBar.setSelectedNavigationItem(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            Object tag = tab.getTag();
            for (int i = 0; i < mTabs.size(); i++) {
                if (mTabs.get(i) == tag)
                    mViewPager.setCurrentItem(i);
            }
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
        }

        static final class TabInfo {
            private final Class<?> clss;
            private final Bundle args;
            private final String ident;

            TabInfo(Class<?> _class, Bundle _args, String _ident) {
                clss = _class;
                args = _args;
                ident = _ident;
            }
        }
    }

        /*
    private void selectTab() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(this);

        try {
            String preferredExchange = prefs.getString("favExchangePref", "bitstamp");
            //Check if moving from integer index
            if (preferredExchange.matches("\\d+")) {
                int preferredExchangeNum = Integer.parseInt(preferredExchange);
                actionbar.setSelectedNavigationItem(preferredExchangeNum);

                //Migrate to the newer tag index
                String[] exchangeMap = getResources().getStringArray(R.array.exchangeMigration);
                Editor editor = prefs.edit();
                editor.putString("favExchangePref", exchangeMap[preferredExchangeNum]);
                editor.commit();
            } else {
                selectTab(preferredExchange);
            }
        } catch (Exception e) {
            // If preference is not set a valid integer set to "0"
            Editor editor = prefs.edit();
            editor.putString("favExchangePref", "bitstamp");
            editor.commit();
        }
    }
    */

    /*
    private void selectTab(String key) {
        try {
            int tabIndex = tabsAdapter.getIndexForIdentity(key);
            if (tabIndex >= 0)
                actionbar.setSelectedNavigationItem(tabIndex);
            else
                actionbar.setSelectedNavigationItem(0);
        } catch (Exception e) {
            selectTab();
        }
    }
    */
}
