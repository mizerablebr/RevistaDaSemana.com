package com.rds.revistadasemanacom;



import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.util.ArrayList;
import java.util.Arrays;



public class MainActivity extends AppCompatActivity {

    public static boolean ISDEBUG = false;

    private PostDataAdapter listAdapter;
    private ArrayList<PostData> listAdapterContent = new ArrayList<PostData>();
    private ArrayList<PostData> oldListAdapterContent = new ArrayList<PostData>();
    private ListView listView;
    private ProgressDialog mProgressDialog;

    //Navigation Drawer Variables
    private String[] categories;
    private ListView drawerList;
    private DrawerLayout drawerLayout;

    //Service variable
    private GetPostService postService;
    private boolean bound = false;

    //BroadCast receiver
    private BroadcastReceiver receiver;

    // Main Fragment
    private Fragment fragment;


    private class DrawerItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d("DrawerItemClick", "Categoria nº: " + position);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Setup Navigation Drawer
        categories = getResources().getStringArray(R.array.categories);
        drawerList = (ListView) findViewById(R.id.drawer);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
           //Populate de ListView
        drawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_activated_1, categories));
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        //getActionBar().setDisplayHomeAsUpEnabled(true);
        //getActionBar().setHomeButtonEnabled(true);

        // -- END Setup Navigation Drawer

        // Setup MainFragment
        fragment = new PostDataFragment();
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        // -- END Setup MainFragment



    }

    @Override
    protected void onResume() {

        super.onResume();
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
                ((PostDataFragment)fragment).updatePosts();
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
            case R.id.openRevistaSite:
                String url = getString(R.string.url_revista_da_Semana);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
                break;
            case R.id.about:
                Intent intentAbout = new Intent(this, OptionActivity.class);
                intentAbout.putExtra("menu", OptionActivity.MENU_ABOUT);
                startActivity(intentAbout);
                break;

        }
        return super.onOptionsItemSelected(item);
    }


}
