package com.pushtorefresh.storio.contentprovider.design;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;

/**
 * Test class that represents an object stored in ContentProvider
 */
class Article {

    static final MapFunc<Cursor, Article> MAP_FROM_CURSOR = new MapFunc<Cursor, Article>() {
        @Override
        public Article map(Cursor cursor) {
            return new Article(); // parse cursor here
        }
    };

    @Nullable
    private Long id;

    @NonNull
    private String title;

    @Nullable
    public Long getId() {
        return id;
    }

    @NonNull
    public String getTitle() {
        return title;
    }
}
