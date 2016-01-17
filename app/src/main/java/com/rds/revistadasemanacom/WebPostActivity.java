package com.rds.revistadasemanacom;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class WebPostActivity extends AppCompatActivity {

    public static final String EXTRA_POSTDATANO = "postDataNo";
    public static final String EXTRA_POSTDATATITLE = "postDataTitle";
    public static final String EXTRA_POSTDATALINK = "postDataLink";
    public static final String EXTRA_POSTDATACONTENT = "postDataContent";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_post);

        //Get data from Intent
        String titleStr = (String) getIntent().getExtras().get(EXTRA_POSTDATATITLE);
        String contentStr = (String) getIntent().getExtras().get(EXTRA_POSTDATACONTENT);



        //Populate the view
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(titleStr);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(contentStr,"text/html; charset=UTF-8",null);

        //Populate AdView
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }
}
