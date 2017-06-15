package com.example.eeshan.wist;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private final int pageSize = 100;
    private String username;
    private String email;
    private String token;

    AlertDialogManager alert = new AlertDialogManager();
    SessionManager session;
    Button btnLogout;

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

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();
        this.username = user.get(SessionManager.KEY_NAME);
        this.email = user.get(SessionManager.KEY_EMAIL);
        this.token = user.get(SessionManager.KEY_ACCESS_TOKEN);

//        Button btnLogout;
//        btnLogout = (Button) findViewById(R.id.btnLogout);
//        btnLogout.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // Clear the session data
//                // This will clear all session data and
//                // redirect user to LoginActivity
//                session.logoutUser();
//            }
//        });

        Button createPostButton = (Button) findViewById(R.id.post_button);
        createPostButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostCreateActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.post_list);

        // Posts DB
        WistDbHelper mDbHelper = new WistDbHelper(this);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = fetchDbPosts(db);
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("limit", String.valueOf(pageSize));

        HttpRequest httpRequest = new HttpRequest(this, "GET", "/posts", params, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject object) {
                if (object != null) {
                    try {
                        JSONArray postsResponse = object.getJSONArray("posts");
                        PostAdapter postsJSONAdapter = populatePostsAdapter(postsResponse);
                        listView.setAdapter(postsJSONAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private PostAdapter populatePostsAdapter(JSONArray array) throws JSONException {
        ArrayList<Post> posts = new ArrayList<Post>();

        for (int i = 0; i < array.length(); i++) {
            posts.add(0,
                    new Post(
                            array.getJSONObject(i).getString("body"),
                            array.getJSONObject(i).getJSONObject("user").getString("username"),
                            array.getJSONObject(i).getString("created_at")
                    )
            );
        }

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

    public boolean isNotConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }
}