package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class TestContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.pushtorefresh.storio.content_resolver.test";
    private static final int URI_MATCHER_TABLE_CODE = 1;

    private static final UriMatcher USER_URI_MATCHER = new UriMatcher(1);
    private static final UriMatcher TWEETS_URI_MATCHER = new UriMatcher(1);

    static {
        USER_URI_MATCHER.addURI(AUTHORITY, UserMeta.TABLE, URI_MATCHER_TABLE_CODE);
        TWEETS_URI_MATCHER.addURI(AUTHORITY, TweetMeta.TABLE, URI_MATCHER_TABLE_CODE);
    }

    @NonNull
    private TestSQLiteOpenHelper sqLiteOpenHelper;

    private String getTableNameForUri(@Nullable final Uri uri) {
        if (uri != null) {
            if ((USER_URI_MATCHER.match(uri) == URI_MATCHER_TABLE_CODE)) {
                return UserMeta.TABLE;
            }
            if ((TWEETS_URI_MATCHER.match(uri) == URI_MATCHER_TABLE_CODE)) {
                return TweetMeta.TABLE;
            }
        }
        return null;
    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new TestSQLiteOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            return sqLiteOpenHelper.getWritableDatabase().query(
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
            final long insertedId = sqLiteOpenHelper.getWritableDatabase().insert(
                    tableName,
                    null,
                    values
            );

            getContext().getContentResolver().notifyChange(uri, null);

            return ContentUris.withAppendedId(uri, insertedId);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            final int numberOfRowsDeleted = sqLiteOpenHelper.getWritableDatabase().delete(
                    tableName,
                    selection,
                    selectionArgs
            );

            getContext().getContentResolver().notifyChange(uri, null);

            return numberOfRowsDeleted;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final String tableName = getTableNameForUri(uri);
        if (tableName != null) {
            final int numberOfRowsUpdated = sqLiteOpenHelper.getWritableDatabase().update(
                    tableName,
                    values,
                    selection,
                    selectionArgs
            );

            getContext().getContentResolver().notifyChange(uri, null);

            return numberOfRowsUpdated;
        }

        return 0;
    }
}
