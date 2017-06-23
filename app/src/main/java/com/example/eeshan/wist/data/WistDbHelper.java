package com.example.eeshan.wist.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import junit.runner.Version;

/**
 * Created by eeshan on 15/5/17.
 */

public class WistDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Wist.db";
    public static final int DATABASE_VERSION = 3;

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
        Log.v("SQLite", "updating table from " + String.valueOf(oldVersion) + " to " + String.valueOf(newVersion));
        if (oldVersion < 2) {
            String SQL_UPDATE_WIST_TABLE = "ALTER TABLE " + WistContract.PostEntry.TABLE_NAME
                    + " ADD COLUMN " + WistContract.PostEntry.COLUMN_NAME_CREATED_DATE + " INTEGER;";
            db.execSQL(SQL_UPDATE_WIST_TABLE);
        }
        if (oldVersion < 3) {
            String SQL_UPDATE_WIST_TABLE = "ALTER TABLE " + WistContract.PostEntry.TABLE_NAME
                    + " ADD COLUMN " + WistContract.PostEntry.COLUMN_NAME_POST_ID + " INTEGER NOT NULL;";
            db.execSQL(SQL_UPDATE_WIST_TABLE);
        }
    }
}
