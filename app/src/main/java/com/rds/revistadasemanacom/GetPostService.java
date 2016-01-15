package com.rds.revistadasemanacom;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class GetPostService extends Service {

    private final IBinder binder = new GetPostBinder();
    private List<PostData> postDatasToView = null;
    private static final String URL = "http://revistadasemana.com/v3/feed/";
    //Broadcast Strings
    public static final String GETPOST_RESULT = "com.rds.revistadasemanacom.GetPostService.POSTDATAREADY";
    public static final String GETPOST_MESSAGE = "com.rds.revistadasemanacom.GetPostService.MESSAGE";
    public static final String SERVICE_INITIATED = "serviceInitiated";
    public static final String SERVICE_FINICHED = "serviceFinished";


    //Setup Broadcast
    private LocalBroadcastManager broadcaster;

    //Binder inner class
    public class GetPostBinder extends Binder {
        GetPostService getPost() {
            return GetPostService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //TODO - Code to get PostData
        //Setup LocalBroadCast
        broadcaster = LocalBroadcastManager.getInstance(this);

        //Execute tasks
        new DownloadXmlTask().execute(URL);

    }

    //BroadCast method
    public void sendResult(String message) {
        Intent intent = new Intent(GETPOST_RESULT);
        if (message != null) {
            intent.putExtra(GETPOST_MESSAGE, message);
        }
        broadcaster.sendBroadcast(intent);
    }

    // Implementation of AsyncTask used to download XML feed from revistadasemana.com
    private class DownloadXmlTask extends AsyncTask<String, Void, List<PostData>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendResult(SERVICE_INITIATED);
        }

        @Override
        protected List<PostData> doInBackground(String... urls) {
            try {
                Log.d("doInBackgroud URL", urls[0]);
                List<PostData> result = loadXmlFromNetwork(urls[0]);
                return result;
            } catch (IOException e) {
                Log.d("doInBackgroud", "erro executando loadXmlFromNetwork - IO");
                return null;
            } catch (XmlPullParserException e) {
                Log.d("doInBackgroud", "erro executando loadXmlFromNetwork - XML");
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<PostData> postDatas) {
            postDatasToView = postDatas;
            //Broadcast that the results are ready
            sendResult(SERVICE_FINICHED);
            Log.d("Result", postDatasToView.toString());
        }
    }

    //Download XML from revistadasemana.com, parses it, and return

    private List<PostData> loadXmlFromNetwork(String urlString) throws XmlPullParserException, IOException {
        InputStream stream = null;
        RevistaDaSemanaXmlParser revistaDaSemanaXmlParser = new RevistaDaSemanaXmlParser();
        List<PostData> entries = null;
        String title = null;
        String url = null;

        try {
            Log.d("downloadUrl", urlString);
            stream = downloadUrl(urlString);
            entries = revistaDaSemanaXmlParser.parse(stream);
            //Makes sure that the InputStream is closed after the app is finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
        return entries;
    }

    //Given a string representation of a URL, sets up a connection and gets an input stream.
    private InputStream downloadUrl(String urlString) throws  IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setReadTimeout(10 * 1000);
        conn.setConnectTimeout(15 * 1000);
        conn.setRequestMethod("GET");
        conn.setDoInput(true);
        //Starts the query
        conn.connect();
        Log.d("downloadUrlCodeResponse", Integer.toString(conn.getResponseCode()));
        return conn.getInputStream();
    }

    public List<PostData> getPostDatasToView() {return this.postDatasToView;}

}
