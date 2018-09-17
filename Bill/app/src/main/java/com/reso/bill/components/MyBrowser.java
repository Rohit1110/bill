package com.reso.bill.components;

import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Admin on 17/09/2018.
 */

public class MyBrowser extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }
}