package com.pushtorefresh.bamboostorage.test.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

/**
 * Pretty standard Content Provider with SQLiteDatabase as storage
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TestContentProvider extends ContentProvider {

    private TestDBOpenHelper mTestDBOpenHelper;

    @Override
    public boolean onCreate() {
        mTestDBOpenHelper = new TestDBOpenHelper(getContext());
        return true;
    }
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mTestDBOpenHelper.getReadableDatabase();

        Cursor cursor = db.query(
                uri.getLastPathSegment(),
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mTestDBOpenHelper.getWritableDatabase();

        long id = db.insert(
                uri.getLastPathSegment(),
                null,
                values
        );

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mTestDBOpenHelper.getWritableDatabase();

        return db.delete(uri.getLastPathSegment(), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mTestDBOpenHelper.getWritableDatabase();

        return db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }
}
