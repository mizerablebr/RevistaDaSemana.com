package com.rds.revistadasemanacom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruno on 16/01/16.
 */
public class RevistaDaSemanaDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "revistadasemana";
    private static final int DB_VERSION = 1;

    public RevistaDaSemanaDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE POSTDATA (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "TITLE TEXT, "
                + "LINK TEXT, "
                + "CATEGORY TEXT);");
        insertPostData(db, "Primeira notícia do banco de dados", "www.miz.com.br", "Nacional");
        insertPostData(db, "Segunda notícia do banco de dados", "http://www.google.com.br", "Blog");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private static void insertPostData(SQLiteDatabase db, String title, String link, String category) {
        ContentValues postDataValues = new ContentValues();
        postDataValues.put("TITLE", title);
        postDataValues.put("LINK", link);
        postDataValues.put("CATEGORY", category);
        db.insert("POSTDATA", null, postDataValues);

    }
}
