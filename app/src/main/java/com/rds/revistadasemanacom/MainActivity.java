package com.rds.revistadasemanacom;



import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import java.security.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


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
    private ActionBarDrawerToggle drawerToggle;
    private ArrayList<CategoryMenu> categoryMenus;
    private CategoryMenuAdapter menuAdapter;
    public static final String PREFS_NAME = "MyPrefsFile";
        //Variable that control the displayed categories
    protected int currentPosition = 0;

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
            selectItem(position);
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
        categoryMenus = countPostDataInCategory(new ArrayList<CategoryMenu>(Arrays.asList(CategoryMenu.categoryMenu)));
        categoryMenus = countPostDataInCategory(categoryMenus);
        menuAdapter = new CategoryMenuAdapter(this, categoryMenus);
        drawerList.setAdapter(menuAdapter);
        drawerList.setOnItemClickListener(new DrawerItemClickListener());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(false);

        //Check if the category selection is old
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (categoriesTimeStampIsOld()) {
            currentPosition = 0;
        } else {
            currentPosition = settings.getInt("categories", 0);
        }



        // -- END Setup Navigation Drawer

        // Setup MainFragment
        fragment =  new PostDataFragment();
        ((PostDataFragment) fragment).setCurrentCategorie(currentPosition);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragment);
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        // -- END Setup MainFragment

        //Create the ActionBarDrawerToggle
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_drawer, R.string.close_drawer) {
            //Called when a drawer has settle in a completely closed state

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu();
            }

            //Called when a drawer has settle in a completely open state

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
        //Display the correct category selected

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("categories", currentPosition);
        Date date = new Date();
        editor.putLong("categoriesTime", date.getTime());
        editor.apply();
        Log.d("OnPouseposition", "CurrentPostion: " + currentPosition + "categoriesTime: " + String.valueOf(date.getTime()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

    //Navigation Drawer Selected Item
    private void selectItem(int position) {
        Log.d("DrawerItemClick", "Categoria nº: " + position);
        currentPosition = position;
        drawerLayout.closeDrawer(drawerList);
        ((PostDataFragment) fragment).setCurrentCategorie(currentPosition);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.detach(fragment);
        ft.attach(fragment);
        ft.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateDrawerMenuList();
        //Check if the category selection is old
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (categoriesTimeStampIsOld()) {
            currentPosition = 0;
        } else {
            currentPosition = settings.getInt("categories", 0);
        }
        drawerList.setItemChecked(currentPosition, true);
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
        //Let the ActionBarDrawerToggle handle bein clicked
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

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

    //Count PostData from each category
    public ArrayList<CategoryMenu> countPostDataInCategory(ArrayList<CategoryMenu> oldMenu) {
        ArrayList<CategoryMenu> updatedMenu = new ArrayList<>();

        SQLiteOpenHelper revistaDaSemanaDatabaseHelper = new RevistaDaSemanaDatabaseHelper(this);
        SQLiteDatabase db = revistaDaSemanaDatabaseHelper.getReadableDatabase();

        for (CategoryMenu menu : oldMenu) {
            long counter;
            if (menu.getCatName().equals(CategoryMenu.categoryMenu[0].getCatName())) {
                counter = DatabaseUtils.queryNumEntries(db, "POSTDATA", "READ != ?", new String[] {"yes"});
            } else {
                counter = DatabaseUtils.queryNumEntries(db, "POSTDATA", "CATEGORY = ? AND READ != ?", new String[] {menu.getCatName(), "yes"});
            }
            menu.setQuantity((int) (long) counter);
            Log.d("countPostData", "Category: " + menu.getCatName() + " - nº: " + menu.getQuantity());
            updatedMenu.add(menu);
        }

        db.close();
        return updatedMenu;
    }

    private void updateDrawerMenuList() {
        synchronized (this) {
            categoryMenus = countPostDataInCategory(new ArrayList<CategoryMenu>(Arrays.asList(CategoryMenu.categoryMenu)));
            menuAdapter.clear();
            menuAdapter.addAll(countPostDataInCategory(categoryMenus));
            drawerList.invalidateViews();
        }
    }

    private boolean categoriesTimeStampIsOld() {
        int minutesToBeOld = 20;

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        long timeToCheck = settings.getLong("categoriesTime", 0);

        Date dateToCheck = new Date(timeToCheck + (minutesToBeOld * 60 * 1000));
        Date actualDate = new Date();

        if (actualDate.after(dateToCheck)) {
            return true;
        } else {
            return false;
        }

    }


}
