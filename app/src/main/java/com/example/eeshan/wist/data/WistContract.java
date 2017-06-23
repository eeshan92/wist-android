package com.example.eeshan.wist.data;

import android.provider.BaseColumns;

/**
 * Created by eeshan on 15/5/17.
 */

public final class WistContract {
    private WistContract() {}

    public static class PostEntry implements BaseColumns {
        public static final String TABLE_NAME = "post";
        public static final String COLUMN_NAME_BODY = "body";
        public static final String COLUMN_NAME_USER_ID = "user_id";
        public static final String COLUMN_NAME_CREATED_DATE = "created_date";
        public static final String COLUMN_NAME_POST_ID = "post_id";
    }
}
