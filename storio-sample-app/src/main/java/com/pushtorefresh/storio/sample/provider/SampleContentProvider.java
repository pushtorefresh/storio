package com.pushtorefresh.storio.sample.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.pushtorefresh.storio.sample.SampleApp;
import com.pushtorefresh.storio.sample.db.table.TweetSqliteTableMeta;

import javax.inject.Inject;

public class SampleContentProvider extends ContentProvider {

    public static final String AUTHORITY = "com.pushtorefresh.storio.sample_provider";

    private static final String PATH_TWEETS = "tweets";
    private static final int URI_MATCHER_CODE_TWEETS = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_TWEETS, URI_MATCHER_CODE_TWEETS);
    }

    @Inject
    SQLiteOpenHelper sqLiteOpenHelper;

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCreate() {
        SampleApp.get(getContext()).appComponent().inject(this);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(
                                TweetSqliteTableMeta.TABLE,
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
                final long insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                TweetSqliteTableMeta.TABLE,
                                null,
                                values
                        );
                notifyChanges(uri);
                return ContentUris.withAppendedId(uri, insertedId);
        }

        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                final int rows = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                TweetSqliteTableMeta.TABLE,
                                selection,
                                selectionArgs
                        );
                notifyChanges(uri);
                return rows;
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                final int rows = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                TweetSqliteTableMeta.TABLE,
                                values,
                                selection,
                                selectionArgs
                        );
                notifyChanges(uri);
                return rows;
        }

        return 0;
    }

    private void notifyChanges(final Uri uri) {
        getContext().getContentResolver().notifyChange(uri, null);
    }
}
