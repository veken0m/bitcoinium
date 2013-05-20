
package com.veken0m.bitcoinium;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class WebViewerActivity extends Activity {
    static WebView mWebView;
    final static CharSequence cBitcoinium = "Bitcoinium.com";
    final static CharSequence cMtGox = "MtGox";
    final static CharSequence cVirtEx = "VirtEx";
    final static CharSequence cBTCE = "BTC-E";
    final static CharSequence cBitstamp = "Bitstamp";
    final static CharSequence cCampBX = "CampBX";
    final static CharSequence cmtGoxLive = "MtGoxLive";
    final static CharSequence cClose = "Close";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        if (savedInstanceState != null) {
            ((WebView) findViewById(R.id.webviewer))
                    .restoreState(savedInstanceState);
        } else {
            mWebView = (WebView) findViewById(R.id.webviewer);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mWebView.saveState(outState);
    }

    private void displayMenu() {
        final CharSequence[] items = {
                cBitcoinium, cMtGox, cVirtEx, cBTCE,
                cBitstamp, cCampBX, cmtGoxLive, cClose
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select an option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                mWebView.getSettings().setPluginState(
                        WebSettings.PluginState.ON);
                mWebView.getSettings().setJavaScriptEnabled(true);
                mWebView.getSettings().setSupportZoom(true);
                mWebView.getSettings().setBuiltInZoomControls(true);

                if (items[item] == cBitcoinium) {
                    dialog.cancel();
                    mWebView.loadUrl("http://bitcoinium.com/");
                } else if (items[item] == cVirtEx) {
                    dialog.cancel();
                    mWebView.loadUrl("https://www.cavirtex.com/orderbook");
                } else if (items[item] == cmtGoxLive) {
                    dialog.cancel();
                    mWebView.loadUrl("http://mtgoxlive.com/orders");
                } else if (items[item] == cMtGox) {
                    dialog.cancel();
                    mWebView.loadUrl("https://mtgox.com");
                } else if (items[item] == cBTCE) {
                    dialog.cancel();
                    mWebView.loadUrl("https://btc-e.com/");
                } else if (items[item] == cBitstamp) {
                    dialog.cancel();
                    mWebView.loadUrl("https://www.bitstamp.net/");
                } else if (items[item] == cCampBX) {
                    dialog.cancel();
                    mWebView.loadUrl("https://campbx.com/mktexplorer.php");
                } else {
                    dialog.cancel();

                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        displayMenu();
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        displayMenu();
        return false;
    }
}
