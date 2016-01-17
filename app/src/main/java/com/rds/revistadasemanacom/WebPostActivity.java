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

    public static final String EXTRA_POSTDATANO = "postDataNo";
    public static final String EXTRA_POSTDATATITLE = "postDataTitle";
    public static final String EXTRA_POSTDATALINK = "postDataLink";
    public static final String EXTRA_POSTDATACONTENT = "postDataContent";

    String titleStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_post);

        //Get data from Intent
        titleStr = (String) getIntent().getExtras().get(EXTRA_POSTDATATITLE);
        String contentStr = (String) getIntent().getExtras().get(EXTRA_POSTDATACONTENT);



        //Populate the view
        TextView title = (TextView) findViewById(R.id.titleTextView);
        title.setText(titleStr);


        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setBackgroundColor(Color.TRANSPARENT);
        webView.loadData(contentStr, "text/html; charset=UTF-8", null);

        //Set PostData as Readed
        new SetPostDataReaded().execute(titleStr);

        //Populate AdView
        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }


    //Change the PostData Category to Readed
    private class SetPostDataReaded extends AsyncTask<String, Void, Void> {
        String title;

        @Override
        protected Void doInBackground(String... params) {
            title = params[0];
            ContentValues postDataValues = new ContentValues();
            postDataValues.put("CATEGORY", "Readed");

            try {
                SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(WebPostActivity.this);
                SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getReadableDatabase();
                db.update("POSTDATA", postDataValues, "TITLE = ?", new String[]{title});
                db.close();

            } catch (SQLiteException e) {
                Log.d("SetPostDataReaded", "Error updating database entrie");
            }


            return null;
        }
    }

}
