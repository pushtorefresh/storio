package com.pushtorefresh.bamboostorage.test.app;

import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.pushtorefresh.bamboostorage.ABambooSQLiteOpenHelperContentProvider;

/**
 * Test content provider for BambooStorage tests
 *
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TestContentProvider extends ABambooSQLiteOpenHelperContentProvider {

    @NonNull @Override protected SQLiteOpenHelper provideSQLiteOpenHelper() {
        return new TestDBOpenHelper(getContext());
    }
}
