package com.pushtorefresh.storio.contentprovider.design;

import android.database.Cursor;

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
}
