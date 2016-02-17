package com.rds.revistadasemanacom;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Bruno on 16/01/16.
 */
public class RevistaDaSemanaDatabaseHelper extends SQLiteOpenHelper {

    public static final String FIRST_POST = "Precisa de ajuda? Clique aqui!";
    public static final String SECOND_POST = "Segunda notícia";
    private static final String DB_NAME = "revistadasemana";
    private static final int DB_VERSION = 2;

    public RevistaDaSemanaDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updataMyDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updataMyDatabase(db, oldVersion, newVersion);
    }

    private static void insertPostData(SQLiteDatabase db, String title, String link, String category, String content, String readed) {
        ContentValues postDataValues = new ContentValues();
        postDataValues.put("TITLE", title);
        postDataValues.put("LINK", link);
        postDataValues.put("CATEGORY", category);
        postDataValues.put("CONTENT", content);
        postDataValues.put("READ", readed);
        db.insert("POSTDATA", null, postDataValues);

    }

    private void updataMyDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            db.execSQL("CREATE TABLE POSTDATA (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "TITLE TEXT, "
                    + "LINK TEXT, "
                    + "CATEGORY TEXT, "
                    + "CONTENT TEXT, "
                    + "READ TEXT DEFAULT 'not');");
            insertPostData(db, FIRST_POST, "www.miz.com.br", "Nacionais", "blá blá blá", "not");
            insertPostData(db, SECOND_POST, "http://www.google.com.br", "Blog", "blu blu blu", "not");
        }

        if (oldVersion == 1) {
            db.execSQL("ALTER TABLE POSTDATA ADD COLUMN READ TEXT DEFAULT \'not\'");
        }
    }
}
