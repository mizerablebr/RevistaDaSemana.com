package com.rds.revistadasemanacom;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.TextView;

public class WebPostActivity extends Activity {

    public static final String EXTRA_POSTDATANO = "postDataNo";
    public static final String EXTRA_POSTDATATITLE = "postDataTitle";
    public static final String EXTRA_POSTDATALINK = "postDataLink";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_post);

        //Get data from Intent
        String titleStr = (String) getIntent().getExtras().get(EXTRA_POSTDATATITLE);
        String linkStr = (String) getIntent().getExtras().get(EXTRA_POSTDATALINK);


        //Populate the view
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(titleStr);
        TextView link = (TextView) findViewById(R.id.linkTextView);
        link.setText(linkStr);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl(linkStr);

    }
}
