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
import android.app.Fragment;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivateKey;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class PostDataFragment extends Fragment {

    public static boolean ISDEBUG = false;

    private PostDataAdapter listAdapter;
    private ArrayList<PostData> listAdapterContent = new ArrayList<PostData>();
    private ArrayList<PostData> oldListAdapterContent = new ArrayList<PostData>();
    private ListView listView;
    private ProgressDialog mProgressDialog;

    //Navigation Drawer Variables
    private int currentCategorie;

    //Service variable
    private GetPostService postService;
    private boolean bound = false;

    //BroadCast receiver
    private BroadcastReceiver receiver;

    public PostDataFragment() {
        // Required empty public constructor
    }


    //Creating service connection
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            GetPostService.GetPostBinder getPostBinder = (GetPostService.GetPostBinder) service;
            postService = getPostBinder.getPost();
            bound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bound = false;
        }
    };

    public void setCurrentCategorie(int cat) {
        currentCategorie = cat;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_post_data, container, false);

        //Create an OnItemClickListener
        AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> listView, View view, int position, long id) {

                String selectedItem = ((TextView) view.findViewById(R.id.listView_label)).getText().toString();

                if (selectedItem.equals(RevistaDaSemanaDatabaseHelper.FIRST_POST) || selectedItem.equals(RevistaDaSemanaDatabaseHelper.SECOND_POST)) {
                    Intent intentHelp = new Intent(inflater.getContext(), OptionActivity.class);
                    intentHelp.putExtra("menu", OptionActivity.MENU_HELP);
                    startActivity(intentHelp);
                } else {

                    Intent intent = new Intent(inflater.getContext(), WebPostActivity.class);
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATATITLE, listAdapterContent.get(position).getTitle());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATALINK, listAdapterContent.get(position).getLink());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATACONTENT, listAdapterContent.get(position).getContent());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATACATEGORY, listAdapterContent.get(position).getCategory());
                    intent.putExtra(WebPostActivity.EXTRA_POSTDATAREAD, listAdapterContent.get(position).getRead());

                    startActivity(intent);
                }
            }
        };
        //Add listener to ListView
        listView = (ListView) view.findViewById(R.id.listViewPosts);
        listView.setOnItemClickListener(itemClickListener);

        //Add an ListView Adapter
        ArrayList<PostData> listAdapterContentRAW = getPostDataFromDb();
        listAdapterContent = getPostDataFromDb();
        listAdapter = new PostDataAdapter(this.getActivity(),listAdapterContent);
        listView.setAdapter(listAdapter);

        //Create BroadCast Listener to update ListView e display ProgressDialog
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra(GetPostService.GETPOST_MESSAGE);
                switch (message) {
                    case GetPostService.SERVICE_INITIATED:
                        //Create a ProgressDialog
                        mProgressDialog = new ProgressDialog(inflater.getContext());
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
                            getActivity().unbindService(connection);
                            bound = false;
                        }
                        //Unregister broadcast
                        LocalBroadcastManager.getInstance(inflater.getContext()).unregisterReceiver(receiver);
                        break;
                    case GetPostService.SERVICE_ERROR:
                        mProgressDialog.dismiss();
                        AlertDialog.Builder builder = new AlertDialog.Builder(inflater.getContext());
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
                            getActivity().unbindService(connection);
                            bound = false;
                        }
                        //Unregister broadcast
                        LocalBroadcastManager.getInstance(inflater.getContext()).unregisterReceiver(receiver);
                        break;

                }

            }
        };

        return view;
    }

    @Override
    public void onResume() {
        setCurrentCategorie(((MainActivity) getActivity()).currentPosition);
        listAdapterContent = getPostDataFromDb();
        updateListView();
        super.onResume();
    }

    private void updateAdapterContentFromService () {
        final Handler handler = new Handler();

        handler.post(new Runnable() {
            @Override
            public void run() {
                if (postService != null) {
                    listAdapterContent = new ArrayList<PostData>(postService.getPostDatasToView());
                    updatePostDataDb(listAdapterContent);
                    updateListView();
                } else {
                }
            }
        });

    }

    public void updateListView () {
        synchronized (this) {
            setCurrentCategorie(((MainActivity) getActivity()).currentPosition);
            listAdapter.clear();
            listAdapter.addAll(getPostDataFromDb());
            listView.invalidateViews();
        }
    }

    void updatePosts() {
        if (checkInternetConnection()) {
            Intent intent = new Intent(getActivity(), GetPostService.class);
            getActivity().bindService(intent, connection, Context.BIND_AUTO_CREATE);

            //Register BroadCast to get know when the service is done
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver((receiver), new IntentFilter(GetPostService.GETPOST_RESULT));
        } else {
            Toast.makeText(getActivity(), "Não há conexão com a internet", Toast.LENGTH_SHORT).show();
        }
    }

    private ArrayList<PostData> getPostDataFromDb() {

        ArrayList<PostData> entries = new ArrayList<PostData>();

        //Create a Cursor to access DB
        try {
            SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(getActivity());
            SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getReadableDatabase();
            Cursor cursor;
            if (currentCategorie == 0) {
                cursor = db.query("POSTDATA",new String[] {"TITLE", "LINK", "CATEGORY", "CONTENT", "READ"}, null,null,null,null,null);
            } else {
                cursor = db.query("POSTDATA",new String[] {"TITLE", "LINK", "CATEGORY", "CONTENT", "READ"}, "CATEGORY = ?",new String[] {CategoryMenu.categoryMenu[currentCategorie].getCatName()},null,null,null);
            }

            //Move to the first record in the cursor
            while (cursor.moveToNext()) {
                PostData postData = new PostData(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
                entries.add(postData);
            }
            cursor.close();
            db.close();

        } catch (SQLiteException e) {
            Toast.makeText(getActivity(), "Erro ao acessar Banco de dados", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        return entries;
    }

    private void updatePostDataDb(ArrayList<PostData> newList) {
        ArrayList<PostData> oldList = new ArrayList<PostData>();

        //Number of posts on XML file
        int nPosts = 30;

        //Get actual PostDatas on DB
        SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(getActivity());
        SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getWritableDatabase();
        Cursor cursor;
        cursor = db.query("POSTDATA",new String[] {"TITLE", "LINK", "CATEGORY", "CONTENT", "READ"}, null,null,null,null,null);
        while (cursor.moveToNext()) {
            PostData postData = new PostData(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4));
            oldList.add(postData);
        }
        cursor.close();

        ////Merge DB with just new PostDatas comparing titles
        ArrayList<PostData> newestList;

        //Remove old PostData from new ArrayList
        newList.removeAll(oldList);
        //Move old PostData to the and of the list
        newestList = newList;
        if (newestList.size() < nPosts) {
            newestList.addAll(oldList.subList(0,(nPosts-newestList.size())));
        }

        //Clear the database
        db.delete("POSTDATA", null, null);
        //Insert new PostData
        for (PostData pd : newestList) {
            ContentValues postDataValues = new ContentValues();
            postDataValues.put("TITLE",pd.getTitle());
            postDataValues.put("LINK",pd.getLink());
            postDataValues.put("CATEGORY", pd.getCategory());
            postDataValues.put("CONTENT",pd.getContent());
            postDataValues.put("READ",pd.getRead());
            db.insert("POSTDATA", null, postDataValues);
        }
        db.close();

    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();

    }

}
