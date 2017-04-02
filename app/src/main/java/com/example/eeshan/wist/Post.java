package com.example.eeshan.wist;

import java.text.DateFormat;

/**
 * Created by eeshan on 2/4/17.
 */

public class Post {
    private String body;
    private String username;
    private DateFormat createdDate;

    public Post(String bodyText, String currentUsername) {
        this.body = bodyText;
        this.username = currentUsername;
        this.createdDate = DateFormat.getDateInstance();
    }

    public String getBody() { return this.body; }

    public String getUsername() { return this.username; }

    public DateFormat getCreatedDate() { return this.createdDate; }
}
