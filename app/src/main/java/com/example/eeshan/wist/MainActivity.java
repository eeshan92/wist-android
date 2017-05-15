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

        ArrayList<Post> posts = new ArrayList<Post>();

        posts.add(new Post("I saw a cat walking around on a purple line train", "David_sim"));
        posts.add(new Post("I saw a dog running around on a circle line train", "rrrrwts"));
        posts.add(new Post("I saw a fish swimming around on a green line train", "92skater"));
        posts.add(new Post("I saw a monkey loose on a red line train", "test_user"));
        posts.add(new Post("I saw a cat walking around on a purple line train", "David_sim"));
        posts.add(new Post("I saw a dog running around on a circle line train", "rrrrwts"));
        posts.add(new Post("I saw a fish swimming around on a green line train", "92skater"));
        posts.add(new Post("I saw a monkey loose on a red line train", "test_user"));
        posts.add(new Post("I saw a cat walking around on a purple line train", "David_sim"));
        posts.add(new Post("I saw a dog running around on a circle line train", "rrrrwts"));
        posts.add(new Post("I saw a fish swimming around on a green line train", "92skater"));
        posts.add(new Post("I saw a monkey loose on a red line train", "test_user"));

        PostAdapter postsAdapter = new PostAdapter(this, posts);

        ListView listView = (ListView) findViewById(R.id.post_list);
        listView.setAdapter(postsAdapter);
    }
}
