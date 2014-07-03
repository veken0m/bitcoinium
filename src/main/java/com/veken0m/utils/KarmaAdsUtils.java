package com.veken0m.utils;

import android.app.Activity;
import android.webkit.WebView;

import com.veken0m.cavirtex.R;

public class KarmaAdsUtils {

    public static void initAd(Activity activity) {
        WebView mWebView = (WebView) activity.findViewById(R.id.karma_ad);
        //mWebView.getSettings().setSupportMultipleWindows(true);
        mWebView.loadUrl("https://karma-ads.com/service/advert/5000");
    }
}
