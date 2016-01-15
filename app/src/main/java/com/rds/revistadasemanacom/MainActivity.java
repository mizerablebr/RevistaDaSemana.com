package com.rds.revistadasemanacom;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.os.Handler;


import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    private PostDataAdapter listAdapter;
    private ArrayList<PostData> listAdapterContent = new ArrayList<PostData>(Arrays.asList(PostData.postData));
    private ListView listView;
    private ProgressDialog mProgressDialog;

    //Service variable
    private GetPostService postService;
    private boolean bound = false;

    //BroadCast receiver
    private BroadcastReceiver receiver;


    //Creating service connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GetPostService.GetPostBinder getPostBinder = (GetPostService.GetPostBinder) service;
            postService = getPostBinder.getPost();
            Log.d("onServiceConnected", postService.toString());
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create an OnItemClickListener
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                    Intent intent = new Intent(MainActivity.this, WebPostActivity.class);
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATATITLE, listAdapterContent.get(position).getTitle());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATALINK, listAdapterContent.get(position).getLink());
                    startActivity(intent);

            }
        };
        //Add listener to ListView
        listView = (ListView) findViewById(R.id.listViewPosts);
        listView.setOnItemClickListener(itemClickListener);

        //Add an ListView Adapter
        listAdapter = new PostDataAdapter(this,listAdapterContent);
        listView.setAdapter(listAdapter);

        //Create BroadCast Listener to update ListView e display ProgressDialog
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d("BroadCastReceiver", "OnReceive executed");
                String message = intent.getStringExtra(GetPostService.GETPOST_MESSAGE);
                switch (message) {
                    case GetPostService.SERVICE_INITIATED:
                        //Create a ProgressDialog
                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setTitle(getResources().getString(R.string.updateInProgress));
                        mProgressDialog.setMessage(getResources().getString(R.string.baixando));
                        mProgressDialog.setIndeterminate(false);
                        mProgressDialog.show();
                        break;
                    case GetPostService.SERVICE_FINICHED:
                        //Update adapter content from service then dismiss the ProgressDialog
                        updateAdapterContentFromService();
                        mProgressDialog.dismiss();
                }

            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();


        //Register BroadCast to get know when the service is done
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(GetPostService.GETPOST_RESULT));

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (bound) {
            unbindService(connection);
            bound = false;
        }
        //Unregister broadcast
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private void updateAdapterContentFromService () {
        final Handler handler = new Handler();
        //TODO - Test internet connection

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("handler.post","postService: " + postService.toString());
                if (postService != null) {
                    listAdapterContent = new ArrayList<PostData>(postService.getPostDatasToView());
                    Log.d("Result - Service", listAdapterContent.toString());
                    updateListView();
                } else {
                    Log.d("MainActivity", "postService == null");
                }
            }
        });

    }

    public void updateListView () {
        synchronized (this) {
            listAdapter.clear();
            listAdapter.addAll(listAdapterContent);
            listView.invalidateViews();
            listView.refreshDrawableState();
        }


    }

    //Setup Menu
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.updatePosts).setVisible(true);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updatePosts:
                Intent intent = new Intent(this, GetPostService.class);
                bindService(intent, connection, Context.BIND_AUTO_CREATE);
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
