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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;

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
        PostAsyncTask uploadPost = new PostAsyncTask();
        uploadPost.execute();

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

    private class PostAsyncTask extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... urls) {
            URL url = createUrl("https://powerful-castle-67767.herokuapp.com/api/v1/posts");

            String jsonResponse = "";
            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            }
            return null;
        }

        private URL createUrl(String stringUrl) {
            URL url = null;
            try {
                url = new URL(stringUrl);
            } catch (MalformedURLException exception) {
                Log.e("Error with creating URL", exception.toString());
                return null;
            }
            return url;
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            String payload = "{ \"body\": \"What I saw today!\" }";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("X-User-Email", "eeshansim@gmail.com");
                urlConnection.setRequestProperty("X-User-Token", "jsjVj5pyxYVK3EyUmUrz");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                writer.write(payload);
                writer.close();

                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return jsonResponse;
        }

        private String readFromStream(InputStream inputStream) throws IOException {
            StringBuilder output = new StringBuilder();
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
            return output.toString();
        }
    }
}
