package com.pushtorefresh.bamboostorage;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Pretty standard implementation of ContentProvider for SQLiteOpenHelper
 * Its main purpose — help you to easily use BambooStorage with existing SQLiteOpenHelper
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public abstract class BambooSQLiteOpenHelperContentProvider extends ContentProvider {

    /**
     * User's SQLiteOpenHelper
     */
    private SQLiteOpenHelper mSQLiteOpenHelper;

    /**
     * Your class should implement this method
     * @return your SQLiteOpenHelper for working with database as storage of ContentProvider
     */
    @NonNull
    protected abstract SQLiteOpenHelper provideSQLiteOpenHelper();

    @Override
    public boolean onCreate() {
        mSQLiteOpenHelper = provideSQLiteOpenHelper();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mSQLiteOpenHelper.getReadableDatabase();

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
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();

        long id = db.insert(
                uri.getLastPathSegment(),
                null,
                values
        );

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        return db.delete(uri.getLastPathSegment(), selection, selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mSQLiteOpenHelper.getWritableDatabase();
        return db.update(uri.getLastPathSegment(), values, selection, selectionArgs);
    }
}
