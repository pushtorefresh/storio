package com.pushtorefresh.storio.content_resolver.impl;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.test.mock.MockContentProvider;

public class TestContentProvider extends MockContentProvider {

    public static final String AUTHORITY = "com.pushtorefresh.storio.content_resolver.test";
    private static final int URI_MATCHER_TABLE_CODE = 1;

    private static final UriMatcher USER_URI_MATCHER = new UriMatcher(1);
    private static final UriMatcher TWEETS_URI_MATCHER = new UriMatcher(1);

    static {
        USER_URI_MATCHER.addURI(AUTHORITY, User.TABLE, URI_MATCHER_TABLE_CODE);
        TWEETS_URI_MATCHER.addURI(AUTHORITY, Tweet.TABLE, URI_MATCHER_TABLE_CODE);
    }

    SQLiteDatabase db = new TestSQLiteOpenHelper(InstrumentationRegistry.getContext()).getWritableDatabase();

    private String getTableNameForUri(@Nullable final Uri uri) {
        if (uri != null) {
            if ((USER_URI_MATCHER.match(uri) == URI_MATCHER_TABLE_CODE)) {
                return User.TABLE;
            }
            if ((TWEETS_URI_MATCHER.match(uri) == URI_MATCHER_TABLE_CODE)) {
                return Tweet.TABLE;
            }
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            return db.query(
                    tableName,
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder
            );
        }
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            final long insertedId = db.insert(
                    tableName,
                    null,
                    values
            );

            return ContentUris.withAppendedId(uri, insertedId);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            return db.delete(
                    tableName,
                    selection,
                    selectionArgs
            );
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            return db.update(
                    tableName,
                    values,
                    selection,
                    selectionArgs
            );
        }

        return 0;
    }
}
