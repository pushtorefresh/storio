package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static android.content.ContentUris.withAppendedId;

public class IntegrationContentProvider extends ContentProvider {

    @NonNull
    static final String AUTHORITY = "com.pushtorefresh.storio.contentresolver.integration";

    @NonNull // Initialized in onCreate()
    private SQLiteOpenHelper sqLiteOpenHelper;

    @NonNull
    private final UriMatcher uriMatcher = new UriMatcher(1);

    private static final int CODE_TEST_ITEM_MATCH = 1;

    {
        uriMatcher.addURI(AUTHORITY, TestItem.CONTENT_PATH, CODE_TEST_ITEM_MATCH);
    }

    @Override
    public boolean onCreate() {
        sqLiteOpenHelper = new IntegrationSQLiteOpenHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case CODE_TEST_ITEM_MATCH:
                return sqLiteOpenHelper
                        .getReadableDatabase()
                        .query(IntegrationSQLiteOpenHelper.TABLE_TEST_ITEMS,
                                projection,
                                selection,
                                selectionArgs,
                                null,
                                null,
                                sortOrder
                        );
        }


        throw new IllegalArgumentException("Unknown uri = " + uri);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Override
    public Uri insert(@NonNull Uri uri, @NonNull ContentValues values) {
        switch (uriMatcher.match(uri)) {
            case CODE_TEST_ITEM_MATCH:
                final Uri insertedUri = withAppendedId(uri,
                        sqLiteOpenHelper
                                .getWritableDatabase()
                                .insert(IntegrationSQLiteOpenHelper.TABLE_TEST_ITEMS,
                                        null,
                                        values
                                )
                );

                getContext().getContentResolver().notifyChange(uri, null);

                return insertedUri;
        }

        throw new IllegalArgumentException("Unknown uri = " + uri);
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        switch (uriMatcher.match(uri)) {
            case CODE_TEST_ITEM_MATCH:
                SQLiteDatabase sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
                try {
                    sqLiteDatabase.beginTransaction();

                    for (ContentValues value : values) {
                        sqLiteDatabase.insert(
                                IntegrationSQLiteOpenHelper.TABLE_TEST_ITEMS,
                                null,
                                value);
                    }
                    sqLiteDatabase.setTransactionSuccessful();
                } finally {
                    sqLiteDatabase.endTransaction();
                }

                getContext().getContentResolver().notifyChange(uri, null);

                return values.length;
        }
        throw new IllegalArgumentException("Unknown uri = " + uri);
    }

    @Override
    public int update(@NonNull Uri uri, @NonNull ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_TEST_ITEM_MATCH:
                final int numberOfRowsUpdated = sqLiteOpenHelper
                        .getWritableDatabase()
                        .update(IntegrationSQLiteOpenHelper.TABLE_TEST_ITEMS,
                                values,
                                selection,
                                selectionArgs
                        );

                getContext().getContentResolver().notifyChange(uri, null);

                return numberOfRowsUpdated;
        }

        throw new IllegalArgumentException("Unknown uri = " + uri);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch (uriMatcher.match(uri)) {
            case CODE_TEST_ITEM_MATCH:
                final int numberOfRowsDeleted = sqLiteOpenHelper
                        .getWritableDatabase()
                        .delete(IntegrationSQLiteOpenHelper.TABLE_TEST_ITEMS,
                                selection,
                                selectionArgs
                        );

                getContext().getContentResolver().notifyChange(uri, null);

                return numberOfRowsDeleted;
        }

        throw new IllegalArgumentException("Unknown uri = " + uri);
    }
}
