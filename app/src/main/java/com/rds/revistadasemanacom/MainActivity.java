package com.rds.revistadasemanacom;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.TextView;
import android.widget.Toast;


import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    private PostDataAdapter listAdapter;
    private ArrayList<PostData> listAdapterContent = new ArrayList<PostData>();
    private ArrayList<PostData> oldListAdapterContent = new ArrayList<PostData>();
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

                String selectedItem = ((TextView) view.findViewById(R.id.listView_label)).getText().toString();

                if (selectedItem.equals(RevistaDaSemanaDatabaseHelper.FIRST_POST) || selectedItem.equals(RevistaDaSemanaDatabaseHelper.SECOND_POST)) {
                    Intent intentHelp = new Intent(MainActivity.this, OptionActivity.class);
                    intentHelp.putExtra("menu", OptionActivity.MENU_HELP);
                    startActivity(intentHelp);
                } else {

                    Intent intent = new Intent(MainActivity.this, WebPostActivity.class);
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATATITLE, listAdapterContent.get(position).getTitle());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATALINK, listAdapterContent.get(position).getLink());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATACONTENT, listAdapterContent.get(position).getContent());

                    startActivity(intent);
                }
            }
        };
        //Add listener to ListView
        listView = (ListView) findViewById(R.id.listViewPosts);
        listView.setOnItemClickListener(itemClickListener);

        //Add an ListView Adapter
        listAdapterContent = getPostDataFromDb();
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

                        //UnbindService and Unregister BroadCast Receiver
                        if (bound) {
                            unbindService(connection);
                            bound = false;
                        }
                        //Unregister broadcast
                        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
                        break;
                    case GetPostService.SERVICE_ERROR:
                        mProgressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage(R.string.errorMessage)
                                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //Ok
                                    }
                                })
                        .setNegativeButton(R.string.retry, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updatePosts();
                            }
                        });
                        builder.show();
                        //TODO - Duplicated Code, fix it.
                        //UnbindService and Unregister BroadCast Receiver
                        if (bound) {
                            unbindService(connection);
                            bound = false;
                        }
                        //Unregister broadcast
                        LocalBroadcastManager.getInstance(MainActivity.this).unregisterReceiver(receiver);
                        break;

                }

            }
        };

    }

    @Override
    protected void onResume() {
        listAdapterContent = getPostDataFromDb();
        updateListView();
        super.onResume();
    }

    private void updateAdapterContentFromService () {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d("handler.post", "postService: " + postService.toString());
                if (postService != null) {
                    oldListAdapterContent = listAdapterContent;
                    listAdapterContent = new ArrayList<PostData>(postService.getPostDatasToView());
                    updatePostDataDb(oldListAdapterContent, listAdapterContent);
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

    //Handle click on actionBar and Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.updatePosts:
                updatePosts();
                break;
            case R.id.help:
                Intent intentHelp = new Intent(this, OptionActivity.class);
                intentHelp.putExtra("menu", OptionActivity.MENU_HELP);
                startActivity(intentHelp);
                break;
            case R.id.contact:
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + "revistadasemana@uol.com.br"));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Contato do Aplicativo RevistaDaSemana");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "Mensagem enviada do aplicativo Revista da Semana:");
                startActivity(Intent.createChooser(emailIntent, "Contato Revista da Semana"));
                break;
            case R.id.about:
                Intent intentAbout = new Intent(this, OptionActivity.class);
                intentAbout.putExtra("menu", OptionActivity.MENU_ABOUT);
                startActivity(intentAbout);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void updatePosts() {
        if (checkInternetConnection()) {
            Intent intent = new Intent(this, GetPostService.class);
            bindService(intent, connection, Context.BIND_AUTO_CREATE);

            //Register BroadCast to get know when the service is done
            LocalBroadcastManager.getInstance(this).registerReceiver((receiver), new IntentFilter(GetPostService.GETPOST_RESULT));
        } else {
            Toast.makeText(MainActivity.this, "Não há conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<PostData> getPostDataFromDb() {

        ArrayList<PostData> entries = new ArrayList<PostData>();

        //Create a Cursor to access DB
        try {
            SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(this);
            SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getReadableDatabase();
            Cursor cursor = db.query("POSTDATA",new String[] {"TITLE", "LINK", "CATEGORY", "CONTENT"},null,null,null,null,null);

            //Move to the first record in the cursor
            while (cursor.moveToNext()) {
                PostData postData = new PostData(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3));
                entries.add(postData);
            }
            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Toast.makeText(MainActivity.this, "Erro ao acessar Banco de dados", Toast.LENGTH_SHORT).show();
        }

        return entries;
    }

    private void updatePostDataDb(ArrayList<PostData> oldList, ArrayList<PostData> newList) {

        ////Merge DB with just new PostDatas comparing titles
        ArrayList<PostData> newestList;

        //Remove old PostData from new ArrayList
        Log.d("MergeList", "newList: " + newList.toString());
        Log.d("MergeList", "oldList: " + oldList.toString());

        newList.removeAll(oldList);
        Log.d("MergeList", "newList after removeAll(oldList: " + newList.toString());
        //Move old PostData to the and of the list opening space for the new PostData
        newestList = newList;
        if (newestList.size() < 10) {
            newestList.addAll(oldList.subList(0,(10-newestList.size())));
        }


        SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(this);
        SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getWritableDatabase();
        //Create the database
        db.delete("POSTDATA", null, null);
        //Insert new PostData
        for (PostData pd : newestList) {
            ContentValues postDataValues = new ContentValues();
            postDataValues.put("TITLE",pd.getTitle());
            postDataValues.put("LINK",pd.getLink());
            postDataValues.put("CATEGORY", pd.getCategory());
            postDataValues.put("CONTENT",pd.getContent());
            db.insert("POSTDATA", null, postDataValues);
        }
        db.close();

    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}
