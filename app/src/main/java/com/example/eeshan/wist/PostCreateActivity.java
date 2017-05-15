package com.example.eeshan.wist;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eeshan.wist.data.WistContract;
import com.example.eeshan.wist.data.WistDbHelper;

import org.w3c.dom.Text;

import static com.example.eeshan.wist.R.id.body;

public class PostCreateActivity extends AppCompatActivity {
    private WistDbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_create);

        mDbHelper = new WistDbHelper(this);

        final EditText body = (EditText) findViewById(R.id.editText);
        final Button savePostButton = (Button) findViewById(R.id.save_post_button);

        savePostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InsertPost(body.getText().toString());
                displayDatabaseInfo();
            }
        });
    }

    private void InsertPost(String body) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WistContract.PostEntry.COLUMN_NAME_BODY, body);
        values.put(WistContract.PostEntry.COLUMN_NAME_USER_ID, "eeshan92");

        long newRowId = db.insert(WistContract.PostEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {
        WistDbHelper mDbHelper = new WistDbHelper(this);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WistContract.PostEntry.TABLE_NAME, null);
        try {
            Log.v("displayDatabaseInfo", "Database has " + String.valueOf(cursor.getCount()) + " entrie(s)");
        } finally {
            cursor.close();
        }
    }
}
