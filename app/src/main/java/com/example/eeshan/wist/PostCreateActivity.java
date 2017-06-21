package com.example.eeshan.wist;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Looper;
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
    private LocationManager locationManager;
    private double lat;
    private double lng;

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            final double longitude = location.getLongitude();
            final double latitude = location.getLatitude();
            setLocation(latitude, longitude);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onProviderDisabled(String provider) {}
    };

    private void setLocation(double latitude, double longitude) {
        Log.v("setLocation", "Lat: " + String.valueOf(latitude) + ". Lng: " + String.valueOf(longitude));
        this.lat = latitude;
        this.lng = longitude;
    }

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
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("onResume()", "Activity resuming");
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5 * 1000, 10, locationListener);
    }

    private void returnToMain() {
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    private void InsertPost(String body) throws IOException {
        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        double lat = this.lat;
        double lng = this.lng;

        SessionManager session = new SessionManager(getApplicationContext());
        session.checkLogin();
        HashMap<String, String> user = session.getUserDetails();

        HashMap<String, String> payload = new HashMap<>();
        payload.put("body", body);
        payload.put("lat", String.valueOf(lat));
        payload.put("lng", String.valueOf(lng));
        HttpRequest httpRequest= new HttpRequest(this, "POST", "/posts", user, payload, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject object) {
                Log.v("JSON Response", object.toString());
            }
        });

//        SQLiteDatabase db = mDbHelper.getWritableDatabase();
//
//        ContentValues values = new ContentValues();
//        values.put(WistContract.PostEntry.COLUMN_NAME_BODY, body);
//        values.put(WistContract.PostEntry.COLUMN_NAME_USER_ID, user.get(SessionManager.KEY_NAME));
//        values.put(WistContract.PostEntry.COLUMN_NAME_CREATED_DATE, Calendar.getInstance().get(SECOND));
//
//        db.insert(WistContract.PostEntry.TABLE_NAME, null, values);
    }

    private void updateLocation() {
    }

    private void displayDatabaseInfo() {
        WistDbHelper mDbHelper = new WistDbHelper(this);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + WistContract.PostEntry.TABLE_NAME, null);
        cursor.close();
    }
}
