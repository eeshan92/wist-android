package com.example.eeshan.wist;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by eeshan on 29/6/17.
 */

public class Track {
    private double longitude;
    private double latitude;
    private HashMap<String, String> user;
    private Context context;

    public Track(double longitude, double latitude, HashMap<String,String> user, Context context) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.user = user;
        this.context = context;
        uploadTrack();
    }

    private void uploadTrack() {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("lat", String.valueOf(longitude));
        payload.put("lng", String.valueOf(latitude));

        new HttpRequest(context, "POST", "/tracks", user, payload, new OnTaskCompleted() {
            @Override
            public void onTaskCompleted(JSONObject object) {
                Log.v("Tracked", object.toString());
            }
        });
    }
}
