package com.rds.revistadasemanacom;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.WebView;
import android.widget.TextView;


import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class WebPostActivity extends AppCompatActivity {

    public static final String EXTRA_POSTDATATITLE = "postDataTitle";
    public static final String EXTRA_POSTDATALINK = "postDataLink";
    public static final String EXTRA_POSTDATACONTENT = "postDataContent";
    public static final String EXTRA_POSTDATACATEGORY = "postDataCategory";
    public static final String EXTRA_POSTDATAREAD = "postDataRead";

    String titleStr;
    String linkStr;
    String contentStr;
    String categoryStr;
    String readStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_post);

        //Get data from Intent
        titleStr = (String) getIntent().getStringExtra(EXTRA_POSTDATATITLE);
        linkStr = (String) getIntent().getStringExtra(EXTRA_POSTDATALINK);
        contentStr = (String) getIntent().getStringExtra(EXTRA_POSTDATACONTENT);
        categoryStr = (String) getIntent().getStringExtra(EXTRA_POSTDATACATEGORY);
        readStr = (String) getIntent().getStringExtra(EXTRA_POSTDATAREAD);

        //Populate the view
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(titleStr);
        TextView category = (TextView) findViewById(R.id.webView_category_label);
        category.setText(categoryStr);

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(contentStr, "text/html; charset=UTF-8", null);

        //Set PostData as Read
        new SetPostDataRead().execute(titleStr);

        //Populate AdView
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    //Change the PostData Category to Read
    private class SetPostDataRead extends AsyncTask<String, Void, Void> {
        String title;

        @Override
        protected Void doInBackground(String... params) {
            title = params[0];
            ContentValues postDataValues = new ContentValues();
            postDataValues.put("READ", "yes");

            try {
                SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(WebPostActivity.this);
                SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getReadableDatabase();
                db.update("POSTDATA", postDataValues, "TITLE = ?", new String[]{title});
                db.close();

            } catch (SQLiteException e) {
                e.printStackTrace();
            }


            return null;
        }
    }

}
