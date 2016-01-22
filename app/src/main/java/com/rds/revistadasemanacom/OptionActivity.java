package com.rds.revistadasemanacom;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OptionActivity extends AppCompatActivity {

    public static final String MENU_HELP = "menuHelp";
    public static final String MENU_ABOUT = "menuAbout";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        String menu = intent.getStringExtra("menu");

        FragmentTransaction ft = getFragmentManager().beginTransaction();

        switch (menu) {
            case MENU_HELP:
                HelpFragment helpFragment = new HelpFragment();
                ft.replace(R.id.fragment_container, helpFragment);
                break;
            case MENU_ABOUT:
                AboutFragment aboutFragment = new AboutFragment();
                ft.replace(R.id.fragment_container, aboutFragment);
        }

        ft.addToBackStack(null);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        ft.commit();

        setContentView(R.layout.activity_option);
    }
}
