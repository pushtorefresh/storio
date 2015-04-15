package com.pushtorefresh.storio.sample.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.pushtorefresh.storio.sample.SampleApp;
import com.pushtorefresh.storio.sample.db.table.TweetTableMeta;

import javax.inject.Inject;

public class SampleContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.pushtorefresh.storio.sample_provider";

    private static final String PATH_TWEETS = "tweets";
    private static final int URI_MATCHER_CODE_TWEETS = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_TWEETS, URI_MATCHER_CODE_TWEETS);
    }

    @Inject
    SQLiteDatabase db;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        SampleApp.get(getContext()).getAppComponent().inject(this);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                return db.query(
                        TweetTableMeta.TABLE,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );

            default:
                return null;
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                final long insertedId = db.insert(
                        TweetTableMeta.TABLE,
                        null,
                        values
                );

                return ContentUris.withAppendedId(uri, insertedId);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                return db.delete(
                        TweetTableMeta.TABLE,
                        selection,
                        selectionArgs
                );
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                return db.update(
                        TweetTableMeta.TABLE,
                        values,
                        selection,
                        selectionArgs
                );
        }

        return 0;
    }
}
