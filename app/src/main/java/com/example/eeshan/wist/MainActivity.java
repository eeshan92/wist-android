package com.example.eeshan.wist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;

import com.example.eeshan.wist.data.WistContract;
import com.example.eeshan.wist.data.WistDbHelper;

import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

//        if (isNotConnected()) {
//            showAlert("internet");
//        }
//
//        if (isNotLocationEnabled()) {
//            showAlert("location");
//        }

        Button createPostButton = (Button) findViewById(R.id.post_button);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostCreateActivity.class);
                startActivity(intent);
            }
        });

        WistDbHelper mDbHelper = new WistDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        Cursor cursor = fetchDbPosts(db);

        PostAdapter postsAdapter = updatePostsAdapter(cursor);

        HttpRequest httpRequest = new HttpRequest(this, "GET", "/posts");
        JSONObject JSONResponse = httpRequest.getJSONObject();

        ListView listView = (ListView) findViewById(R.id.post_list);
        listView.setAdapter(postsAdapter);
    }

    private PostAdapter updatePostsAdapter(Cursor cursor) {
        ArrayList<Post> posts = new ArrayList<Post>();

        while(cursor.moveToNext()) {
//            String createdDate = cursor.getString(
//                    cursor.getColumnIndexOrThrow(WistContract.PostEntry.COLUMN_NAME_CREATED_DATE)
//            );

//            Log.v("Post: ", createdDate);
            posts.add(0,
                new Post(
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(WistContract.PostEntry.COLUMN_NAME_BODY)
                    ),
                    cursor.getString(
                        cursor.getColumnIndexOrThrow(WistContract.PostEntry.COLUMN_NAME_USER_ID)
                    ),
                    "5 mins ago"
                )
            );
        }
        cursor.close();

        return new PostAdapter(this, posts);
    }

    private Cursor fetchDbPosts(SQLiteDatabase db) {
        String[] projection = {
                WistContract.PostEntry.COLUMN_NAME_BODY,
                WistContract.PostEntry.COLUMN_NAME_USER_ID
        };

        return db.query(
            WistContract.PostEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null);
    }

    private void showAlert(String alert) {
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        String message;
        String title;

        switch (alert) {
            case "location":
                message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app";
                title = "Enable Location";
            case "internet":
                message = "No network connection\nPlease make sure you are connected.";
                title = "Enable network connection";
            default:
                message = "Your Locations Settings is set to 'Off'.\nPlease Enable Location to use this app";
                title = "Enable settings";
        }

        dialog.setTitle(title)
                .setMessage(message)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    }
                });

        if (alert == "location") {
            dialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(myIntent);
                }
            });
        }

        dialog.show();
    }

    private boolean isNotLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean isNotConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }
}
