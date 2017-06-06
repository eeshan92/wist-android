package com.example.eeshan.wist;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eeshan on 3/4/17.
 */

public class PostAdapter extends ArrayAdapter<Post> {

    public PostAdapter(@NonNull Context context, ArrayList<Post> posts) {
        super(context, 0, posts);
    }

    @Override
    public View getView(int position, @Nullable View postListView, @NonNull ViewGroup parent) {
        if (postListView == null) {
            postListView = LayoutInflater.from(getContext()).inflate(R.layout.post, parent, false);
        }

        Post currentPost = getItem(position);
        assert currentPost != null;

        TextView bodyTextView = (TextView) postListView.findViewById(R.id.body);
        TextView usernameTextView = (TextView) postListView.findViewById(R.id.username);
        TextView createdDateTextView = (TextView) postListView.findViewById(R.id.created_date);

        bodyTextView.setText(currentPost.getBody());
        usernameTextView.setText(currentPost.getUsername());
        createdDateTextView.setText("5 min ago");

        if (position % 2 == 1) {
            postListView.setBackgroundColor(Color.parseColor("#E6E9EA"));
        } else {
            postListView.setBackgroundColor(Color.WHITE);
        }

        return postListView;
    }
}
