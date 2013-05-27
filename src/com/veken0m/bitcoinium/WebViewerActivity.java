
package com.veken0m.bitcoinium;

import android.app.ActionBar.LayoutParams;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.veken0m.compatibility.WebViewSherlockFragment;

public class WebViewerActivity extends SherlockFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        // ActionBar gets initiated
        ActionBar actionbar = getSupportActionBar();
        actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        ActionBar.Tab BitcoiniumTab = actionbar.newTab().setIcon(
                R.drawable.bitcoiniumwebicon);
        ActionBar.Tab MtGoxLiveTab = actionbar.newTab().setIcon(
                R.drawable.mtgoxlogo);
        ActionBar.Tab BitcoinityTab = actionbar.newTab().setText("Bitcoinity");
        BitcoiniumTab.setText("itcoinium");

        WebViewSherlockFragment BitcoiniumFragment = new BitcoiniumFragment();
        WebViewSherlockFragment BitcoinityFragment = new BitcoinityFragment();
        WebViewSherlockFragment MtGoxLiveFragment = new MtGoxLiveFragment();

        BitcoiniumTab.setTabListener(new WebTabsListener(BitcoiniumFragment));
        BitcoinityTab.setTabListener(new WebTabsListener(BitcoinityFragment));
        MtGoxLiveTab.setTabListener(new WebTabsListener(MtGoxLiveFragment));

        actionbar.addTab(BitcoiniumTab);
        actionbar.addTab(BitcoinityTab);
        actionbar.addTab(MtGoxLiveTab);

        actionbar.show();

        // if (savedInstanceState != null) {
        // ((WebView) findViewById(R.id.webviewer))
        // .restoreState(savedInstanceState);
        // } else {
        // }
    }

    static public class BitcoiniumFragment extends WebViewSherlockFragment {

        public BitcoiniumFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            if (mWebView != null) {
                mWebView.destroy();
            }

            mWebView = new WebView(getActivity());
            mWebView.setInitialScale(100);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            mWebView.setLayoutParams(p);
            mWebView.getSettings().setPluginState(
                    WebSettings.PluginState.ON);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            mWebView.loadUrl("http://bitcoinium.com/");
            mIsWebViewAvailable = true;

            return mWebView;
        }
    }

    static public class MtGoxLiveFragment extends WebViewSherlockFragment {

        public MtGoxLiveFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            if (mWebView != null) {
                mWebView.destroy();
            }

            mWebView = new WebView(getActivity());
            mWebView.setInitialScale(100);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            mWebView.setLayoutParams(p);
            WebSettings webSettings = mWebView.getSettings();
            webSettings.setPluginState(
                    WebSettings.PluginState.ON);
            webSettings.setJavaScriptEnabled(true);
            webSettings.setSupportZoom(true);
            webSettings.setBuiltInZoomControls(true);
            mIsWebViewAvailable = true;
            mWebView.loadUrl("http://mtgoxlive.com/orders");

            return mWebView;
        }
    }

    static public class BitcoinityFragment extends WebViewSherlockFragment {

        public BitcoinityFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {

            if (mWebView != null) {
                mWebView.destroy();
            }

            mWebView = new WebView(getActivity());
            mWebView.setInitialScale(100);
            RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
            mWebView.setLayoutParams(p);
            mWebView.getSettings().setPluginState(
                    WebSettings.PluginState.ON);
            mWebView.getSettings().setJavaScriptEnabled(true);
            // mWebView.getSettings().setSupportZoom(true);
            // mWebView.getSettings().setBuiltInZoomControls(true);
            mIsWebViewAvailable = true;
            mWebView.loadUrl("http://bitcoinity.org/markets");

            return mWebView;
        }
    }

    class WebTabsListener implements ActionBar.TabListener {
        public WebViewSherlockFragment fragment;

        public WebTabsListener(WebViewSherlockFragment fragment) {
            this.fragment = fragment;
        }

        @Override
        public void onTabReselected(Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.webfragment_container, fragment);
        }

        @Override
        public void onTabSelected(Tab tab, FragmentTransaction ft) {
            ft.replace(R.id.webfragment_container, fragment);
        }

        @Override
        public void onTabUnselected(Tab tab, FragmentTransaction ft) {
            ft.remove(fragment);
        }

    }
}
