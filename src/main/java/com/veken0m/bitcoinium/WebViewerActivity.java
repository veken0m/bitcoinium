package com.veken0m.bitcoinium;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.Tab;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.veken0m.compatibility.WebViewSherlockFragment;
// import com.veken0m.utils.KarmaAdsUtils;

@SuppressLint("SetJavaScriptEnabled")
public class WebViewerActivity extends BaseActivity
{
    final static LayoutParams MATCH_PARENT = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        initTabbedActionBar();
        // KarmaAdsUtils.initAd(this);
    }

    private void initTabbedActionBar()
    {
        // ActionBar gets initiated
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);

        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab BitcoiniumTab = actionbar.newTab().setIcon(R.drawable.bitcoiniumwebicon);
        ActionBar.Tab BitcoinityTab = actionbar.newTab().setText("Bitcoinity");
        BitcoiniumTab.setText("itcoinium");

        BitcoiniumTab.setTabListener(new WebTabsListener(new BitcoiniumFragment()));
        BitcoinityTab.setTabListener(new WebTabsListener(new BitcoinityFragment()));

        actionbar.addTab(BitcoiniumTab);
        actionbar.addTab(BitcoinityTab);

        actionbar.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        return true;
    }

    static public class BitcoiniumFragment extends WebViewSherlockFragment
    {
        public BitcoiniumFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            if (mWebView != null)
                mWebView.destroy();

            mWebView = new WebView(getActivity());
            mWebView.setInitialScale(100);
            mWebView.clearCache(true);
            mWebView.setLayoutParams(MATCH_PARENT);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setSupportZoom(true);
            mWebView.setWebViewClient(new WebViewClient()
            {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url)
                {
                    view.loadUrl(url);
                    return true;
                }
            });
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.loadUrl("https://bitcoinium.com");
            mIsWebViewAvailable = true;

            return mWebView;
        }
    }

    static public class BitcoinityFragment extends WebViewSherlockFragment
    {
        public BitcoinityFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            if (mWebView != null)
                mWebView.destroy();

            mWebView = new WebView(getActivity());
            mWebView.setInitialScale(100);
            mWebView.setLayoutParams(MATCH_PARENT);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mIsWebViewAvailable = true;
            mWebView.loadUrl("https://bitcoinity.org/markets");

            return mWebView;
        }
    }

    class WebTabsListener implements ActionBar.TabListener
    {
        public final WebViewSherlockFragment fragment;

        public WebTabsListener(WebViewSherlockFragment fragment)
        {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft)
        {
            ft.replace(R.id.webfragment_container, fragment);
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft)
        {
            ft.replace(R.id.webfragment_container, fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft)
        {
            ft.remove(fragment);
        }
    }
}
