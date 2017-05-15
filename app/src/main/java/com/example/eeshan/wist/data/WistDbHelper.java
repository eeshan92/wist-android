package com.example.eeshan.wist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by eeshan on 15/5/17.
 */

public class WistDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Wist.db";
    public static final int DATABASE_VERSION = 1;

    public WistDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_WIST_TABLE =  "CREATE TABLE " + WistContract.PostEntry.TABLE_NAME + " ("
                + WistContract.PostEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + WistContract.PostEntry.COLUMN_NAME_BODY + " TEXT NOT NULL, "
                + WistContract.PostEntry.COLUMN_NAME_USER_ID + " TEXT)";
        db.execSQL(SQL_CREATE_WIST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
