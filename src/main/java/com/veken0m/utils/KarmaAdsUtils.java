package com.veken0m.utils;

import android.app.Activity;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.veken0m.bitcoinium.R;

public class KarmaAdsUtils
{
    public static void initAd(Activity activity)
    {
        initAd(activity, Constants.DEFAULT_PUBLISHER_ID);
    }

    public static void initAd(Activity activity, String sPublisherId)
    {
        WebView mWebView = (WebView) activity.findViewById(R.id.karma_ad);
        if (mWebView != null)
        {
            //mWebView.getSettings().setSupportMultipleWindows(true);
            if (Constants.adSupported)
                mWebView.loadUrl("https://karma-ads.com/service/advert/" + sPublisherId);
            else
                ((LinearLayout) mWebView.getParent()).removeView(mWebView); // this works for Layouts that extend LinearLayout
        }
    }
}
