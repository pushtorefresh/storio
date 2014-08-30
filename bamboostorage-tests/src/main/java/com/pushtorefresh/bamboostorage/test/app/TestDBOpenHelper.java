package com.pushtorefresh.bamboostorage.test.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
public class TestDBOpenHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "integration_test_db";

    public TestDBOpenHelper(Context context) {
        super(context.getApplicationContext(), DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TestStorableItem.TableInfo.CREATE_TABLE_QUERY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // not used
    }
}
