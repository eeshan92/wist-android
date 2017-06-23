package com.example.eeshan.wist;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.app.AlertDialog.Builder;
import android.widget.Toast;

import com.example.eeshan.wist.data.WistContract;
import com.example.eeshan.wist.data.WistDbHelper;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

import static android.R.attr.direction;
import static com.example.eeshan.wist.R.layout.post;

public class MainActivity extends AppCompatActivity {
    private LocationManager locationManager;
    private SessionManager session;
    private PostAdapter postsAdapter;
    private HashMap<String, String> user;
    private SwipyRefreshLayout swipeRefreshLayout;
    private final int pageSize = 20;

    AlertDialogManager alert = new AlertDialogManager();

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            final double longitude = location.getLongitude();
            final double latitude = location.getLatitude();
            Log.v("onLocationChanged", "Lat: " + String.valueOf(latitude) + ". Lng: " + String.valueOf(longitude));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        postsAdapter = new PostAdapter(this, new ArrayList<Post>());

//        if (isNotConnected()) {
//            showAlert("internet");
//        }
//
//        if (isNotLocationEnabled()) {
//            showAlert("location");
//        }

        session = new SessionManager(getApplicationContext());
        Boolean isLoggedIn = session.checkLogin();

        if (isLoggedIn) {
            user = session.getUserDetails();

            Button createPostButton = (Button) findViewById(R.id.post_button);
            createPostButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, PostCreateActivity.class);
                    startActivity(intent);
                }
            });

            final ListView listView = (ListView) findViewById(R.id.post_list);
            swipeRefreshLayout = (SwipyRefreshLayout) findViewById(R.id.swiperefresh);
            swipeRefreshLayout.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh(SwipyRefreshLayoutDirection direction) {
                    Log.d("MainActivity", "Refresh triggered at "
                            + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
                    if (direction == SwipyRefreshLayoutDirection.TOP) {
                        getPosts(true);
                    } else {
                        getPosts(false);
                    }
                }
            });

            // Posts DB
//            WistDbHelper mDbHelper = new WistDbHelper(this);
//            SQLiteDatabase db = mDbHelper.getReadableDatabase();
//            Cursor cursor = fetchDbPosts(db);

            getPosts(false);
            listView.setAdapter(postsAdapter);
        } else {
            finish();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                session.logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populatePostsAdapter(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject post = array.getJSONObject(i);
            postsAdapter.add(new Post(
                    post.getString("body"),
                    post.getJSONObject("user").getString("username"),
                    post.getString("created_at"),
                    post.getInt("id")
            ));
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("onPause()", "Activity pausing");
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume()", "Activity resuming");
        if (isLocationEnabled()) {
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 5 * 1000, 10, locationListener);
        } else {
            showAlert("location");
        }
    }

    private void getPosts(Boolean refresh) {
        final Boolean refreshing = refresh;
        Integer size, firstId, lastId;
        Post firstItem = null;
        swipeRefreshLayout.setRefreshing(true);

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("per_page", String.valueOf(pageSize));

        size = postsAdapter.getCount();

        if (size > 0) {
            if (refreshing) {
                firstItem = postsAdapter.getItem(0);
                if (firstItem != null) {
                    firstId = firstItem.getId();
                    params.put("after", String.valueOf(firstId));
                }
            } else {
                lastId = postsAdapter.getItem(size - 1).getId();
                params.put("before", String.valueOf(lastId));
            }
        }
        getPostsRequest(refresh, params, 1);
    }

    public void getPostsRequest(Boolean refresh, HashMap<String,String> oldParams, Integer nextPage) {
        final Boolean refreshing = refresh;
        final Integer page = nextPage;
        final HashMap<String,String> params = oldParams;
        params.put("page", String.valueOf(page));

        HttpRequest httpRequest = new HttpRequest(this, "GET", "/posts", user, params, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject object) {
                if (object != null) {
                    try {
                        JSONArray postsResponse = object.getJSONArray("posts");
                        JSONObject pagination = object.getJSONObject("pagination");
                        populatePostsAdapter(postsResponse);

                        if (refreshing && pagination.getInt("page") != pagination.getInt("total")) {
                                getPostsRequest(refreshing, params, page + 1);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                        postsAdapter.sort();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private boolean isLocationEnabled() {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public boolean isNotConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo == null || !networkInfo.isConnected();
    }
}