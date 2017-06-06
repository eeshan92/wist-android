package com.example.eeshan.wist;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

import static android.R.attr.y;

/**
 * Created by eeshan on 6/6/17.
 */

public class HttpRequest {
    private String verb;
    private String uri;
    private String body;
    private Context context;

    public HttpRequest(Context activity, String verb, String uri) {
        this.verb = verb;
        this.uri = uri;
        this.body = "{ \"body\": \"What I saw today!\" }";
        this.context = activity;

        PostAsyncTask uploadPost = new PostAsyncTask();
        uploadPost.execute();
    }

    private class PostAsyncTask extends AsyncTask<Object, Object, Void> {

        @Override
        protected Void doInBackground(Object... urls) {
            URL url = null;
            String domain = context.getString(R.string.base_url);
            try {
                url = new URL(domain + uri);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

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
            HttpURLConnection urlConnection = null;
            InputStream inputStream = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod(verb);
                if (verb != "GET") { urlConnection.setDoOutput(true); }
                urlConnection.setRequestProperty("Content-Type","application/json");
                urlConnection.setRequestProperty("Accept","application/json");
                urlConnection.setRequestProperty("X-User-Email", "eeshansim@gmail.com");
                urlConnection.setRequestProperty("X-User-Token", "jsjVj5pyxYVK3EyUmUrz");
                urlConnection.setReadTimeout(10000);
                urlConnection.setConnectTimeout(15000);
                urlConnection.connect();

                if (verb != "GET") {
                    String payload = body;
                    OutputStreamWriter writer = new OutputStreamWriter(urlConnection.getOutputStream(), "UTF-8");
                    writer.write(payload);
                    writer.close();
                }

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


