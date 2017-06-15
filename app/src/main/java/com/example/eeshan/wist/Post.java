package com.example.eeshan.wist;

import android.icu.util.DateInterval;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static android.R.string.no;

/**
 * Created by eeshan on 2/4/17.
 */

public class Post {
    private String body;
    private String username;
    private String createdDate;

    public Post(String bodyText, String currentUsername, String createdDate) {
        this.body = bodyText;
        this.username = currentUsername;
        this.createdDate = createdDate;
    }

    public String getBody() { return this.body; }

    public String getUsername() { return this.username; }

    public String getCreatedDate() throws ParseException {
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date createdDate = formatter.parse(this.createdDate);
        long diff = now.getTime() - createdDate.getTime();

        long seconds = diff/(1000);
        long minutes = diff/(1000*60);
        long hours = diff/(1000*60*60);
        long days = diff/(1000*60*60*24);

        if (seconds < 120) {
           return "moments ago";
        } else if (minutes < 60) {
            return minutes + " mins ago";
        } else if (hours < 24) {
            return hours + " hour ago";
        } else if (days < 365) {
            SimpleDateFormat dayMonth = new SimpleDateFormat("d MMM");
            dayMonth.setTimeZone(TimeZone.getTimeZone("Singapore"));
            return dayMonth.format(createdDate);
        } else {
            SimpleDateFormat monthYear = new SimpleDateFormat("MMM yy");
            monthYear.setTimeZone(TimeZone.getTimeZone("Singapore"));
            return monthYear.format(createdDate);
        }
    }
}
