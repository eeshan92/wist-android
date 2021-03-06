package com.example.eeshan.wist;

import android.content.ContentValues;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.R.attr.y;

/**
 * Created by eeshan on 6/6/17.
 */

public class HttpRequest {
    private String verb;
    private String uri;
    private String body;
    private Context context;
    private Map<String, String> params;
    private Map<String, String> credentials;

    public HttpRequest(Context activity, String verb, String uri,
                       HashMap<String, String> credentials, HashMap<String, String> params,
                       OnTaskCompleted listener) {
        this.verb = verb;
        this.uri = uri;
        this.context = activity;
        this.params = params;
        this.credentials = credentials;

        PostAsyncTask uploadPost = new PostAsyncTask(listener);
        uploadPost.execute();
    }

    private JSONObject parseJSONObject(String result) throws JSONException {
        return new JSONObject(result);
    }

    private class PostAsyncTask extends AsyncTask<Object, Object, JSONObject> {
        private OnTaskCompleted listener;

        public PostAsyncTask(OnTaskCompleted listener){
            this.listener = listener;
        }

        @Override
        protected JSONObject doInBackground(Object... urls) {
            URL url = null;
            String domain = context.getString(R.string.base_url);
            String queryString = null;
            String jsonResponse = "";

            if (verb == "GET" && params != null) {
                ContentValues paramsList = new ContentValues();
                for (Map.Entry<String, String> entry: params.entrySet()) {
                    paramsList.put(entry.getKey(), entry.getValue());
                }
                try {
                    queryString =  "?" + getQuery(paramsList);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

            if (queryString == null) { queryString = ""; }

            try {
                url = new URL(domain + uri + queryString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            try {
                jsonResponse = makeHttpRequest(url);
            } catch (IOException e) {
                Log.e("IOException", e.toString());
            }

            JSONObject jsonObjectResponse = null;
            try {
                if (jsonResponse != null )
                    jsonObjectResponse = parseJSONObject(jsonResponse);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObjectResponse;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            listener.onTaskCompleted(result);
        }

        private String makeHttpRequest(URL url) throws IOException {
            String jsonResponse = "";
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(verb);
                if (verb != "GET") { urlConnection.setDoOutput(true); }
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                if (credentials != null) {
                    urlConnection.setRequestProperty("X-User-Email", credentials.get(SessionManager.KEY_EMAIL));
                    urlConnection.setRequestProperty("X-User-Token", credentials.get(SessionManager.KEY_ACCESS_TOKEN));
                }

                urlConnection.setReadTimeout(15000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (verb != "GET") {
                    String payload = new JSONObject(params).toString();
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                    writer.write(payload);
                    writer.close();
                }
                if (urlConnection.getResponseCode() == 401) { return null; }
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } catch (IOException e) {
                e.printStackTrace();
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

        private String getQuery(ContentValues params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (Map.Entry<String, Object> pair : params.valueSet()) {
                if (first) {
                    first = false;
                } else {
                    result.append("&");
                }

                result.append(URLEncoder.encode(pair.getKey(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue().toString(), "UTF-8"));
            }
            return result.toString();
        }
    }
}
