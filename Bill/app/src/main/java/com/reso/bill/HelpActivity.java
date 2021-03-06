package com.reso.bill;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.WebView;

import com.reso.bill.components.MyBrowser;

import util.Utility;

public class HelpActivity extends AppCompatActivity {

    private WebView webView;

    String intentUrl;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);

        Intent intent = getIntent();
        intentUrl = intent.getStringExtra("URL");
        title = intent.getStringExtra("ACTIVITY_TITLE");

        if (getSupportActionBar() != null) {
            Utility.setActionBar(title, getSupportActionBar());
        }

        webView = findViewById(R.id.web_help_page);
        webView.setWebViewClient(new MyBrowser());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.loadUrl(intentUrl);

    }


}
