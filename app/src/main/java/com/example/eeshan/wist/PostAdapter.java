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
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        ImageView userIcon  = (ImageView) postListView.findViewById(R.id.user_icon);

        bodyTextView.setText(String.valueOf(currentPost.getId()));
        usernameTextView.setText(currentPost.getUsername());
        try {
            createdDateTextView.setText(currentPost.getCreatedDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (position % 2 == 1) {
            postListView.setBackgroundColor(Color.parseColor("#E6E9EA"));
        } else {
            postListView.setBackgroundColor(Color.WHITE);
        }

        int icon_id = position%16;
        HashMap<Integer, Integer> iconMap = new HashMap<Integer, Integer>();
        iconMap.put(1,R.drawable.antenna);
        iconMap.put(2,R.drawable.apartment);
        iconMap.put(3,R.drawable.arch);
        iconMap.put(4,R.drawable.barrier);
        iconMap.put(5,R.drawable.car);
        iconMap.put(6,R.drawable.circus_tent);
        iconMap.put(7,R.drawable.ferris_wheel);
        iconMap.put(8,R.drawable.fountain);
        iconMap.put(9,R.drawable.house);
        iconMap.put(10,R.drawable.lightbox);
        iconMap.put(11,R.drawable.school_bus);
        iconMap.put(12,R.drawable.traffic_light);
        iconMap.put(13,R.drawable.skyscrapper);
        iconMap.put(14,R.drawable.subway);
        iconMap.put(15,R.drawable.carousel);
        iconMap.put(0,R.drawable.cart);
        userIcon.setImageResource(iconMap.get(icon_id));

        return postListView;
    }


    public void sort() {
        this.sort(new Comparator<Post>() {
            @Override
            public int compare(Post o1, Post o2) {
                return o2.getId().compareTo(o1.getId());
            }
        });
    }
}
