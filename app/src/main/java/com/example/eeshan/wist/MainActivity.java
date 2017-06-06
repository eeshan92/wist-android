package com.example.eeshan.wist;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.eeshan.wist.data.WistContract;
import com.example.eeshan.wist.data.WistDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        HttpRequest jsonResponse = new HttpRequest(this, "GET", "/posts");

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

//        String sortOrder = WistContract.PostEntry.COLUMN_NAME_CREATED_DATE + " DESC";

        return db.query(
            WistContract.PostEntry.TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            null);
    }
}
