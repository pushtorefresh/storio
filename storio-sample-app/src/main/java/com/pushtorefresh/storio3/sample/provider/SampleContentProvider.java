package com.pushtorefresh.storio3.sample.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.sample.SampleApp;
import com.pushtorefresh.storio3.sample.db.tables.TweetsTable;

import javax.inject.Inject;

import androidx.sqlite.db.SupportSQLiteOpenHelper;
import androidx.sqlite.db.SupportSQLiteQueryBuilder;

public class SampleContentProvider extends ContentProvider {

    @NonNull
    public static final String AUTHORITY = "com.pushtorefresh.storio3.sample_provider";

    private static final String PATH_TWEETS = "tweets";
    private static final int URI_MATCHER_CODE_TWEETS = 1;

    private static final UriMatcher URI_MATCHER = new UriMatcher(1);

    static {
        URI_MATCHER.addURI(AUTHORITY, PATH_TWEETS, URI_MATCHER_CODE_TWEETS);
    }

    @Inject
    SupportSQLiteOpenHelper sqLiteOpenHelper;

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
                        .query(SupportSQLiteQueryBuilder.builder(TweetsTable.TABLE)
                                .columns(projection)
                                .selection(selection, selectionArgs)
                                .orderBy(sortOrder)
                                .create()
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
        final long insertedId;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                insertedId = sqLiteOpenHelper
                        .getWritableDatabase()
                        .insert(
                                TweetsTable.TABLE,
                                SQLiteDatabase.CONFLICT_NONE,
                                values
                        );
                break;

            default:
                return null;
        }

        if (insertedId != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, insertedId);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final int numberOfRowsAffected;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                numberOfRowsAffected = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(
                                TweetsTable.TABLE,
                                SQLiteDatabase.CONFLICT_NONE,
                                values,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsAffected > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final int numberOfRowsDeleted;

        switch (URI_MATCHER.match(uri)) {
            case URI_MATCHER_CODE_TWEETS:
                numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(
                                TweetsTable.TABLE,
                                selection,
                                selectionArgs
                        );
                break;

            default:
                return 0;
        }

        if (numberOfRowsDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numberOfRowsDeleted;
    }
}
