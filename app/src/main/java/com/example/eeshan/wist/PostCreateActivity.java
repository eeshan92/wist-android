package com.example.eeshan.wist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.eeshan.wist.data.WistContract;
import com.example.eeshan.wist.data.WistDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;

import static java.util.Calendar.SECOND;

public class PostCreateActivity extends AppCompatActivity {
    private WistDbHelper mDbHelper;
    private Activity currentActivity = this;

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
                try {
                    InsertPost(body.getText().toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                displayDatabaseInfo();
                returnToMain();
            }
        });
    }

    private void returnToMain() {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    private void InsertPost(String body) throws IOException {
        HashMap<String, String> payload = new HashMap<>();
        HttpRequest httpRequest= new HttpRequest(this, "POST", "/posts", payload, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject object) {
                Log.v("JSON Response", object.toString()); // Add to postsAdapter
            }
        });

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(WistContract.PostEntry.COLUMN_NAME_BODY, body);
        values.put(WistContract.PostEntry.COLUMN_NAME_USER_ID, "eeshan92");
        values.put(WistContract.PostEntry.COLUMN_NAME_CREATED_DATE, Calendar.getInstance().get(SECOND));

        db.insert(WistContract.PostEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {
        WistDbHelper mDbHelper = new WistDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WistContract.PostEntry.TABLE_NAME, null);
        try {
            Log.v("displayDatabaseInfo", "Database has " + String.valueOf(cursor.getCount()) + " entries");
        } finally {
            cursor.close();
        }
    }
}
