package com.example.eeshan.wist;

import java.text.DateFormat;

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

    public String getCreatedDate() { return this.createdDate; }

}
