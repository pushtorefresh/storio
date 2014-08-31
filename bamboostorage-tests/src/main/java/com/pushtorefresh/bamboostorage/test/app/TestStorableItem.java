package com.pushtorefresh.bamboostorage.test.app;

import android.content.ContentValues;
import android.content.res.Resources;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;

import com.pushtorefresh.bamboostorage.ABambooStorableItem;
import com.pushtorefresh.bamboostorage.ContentPathForContentResolver;

/**
 * @author Artem Zinnatullin [artem.zinnatullin@gmail.com]
 */
@ContentPathForContentResolver(TestStorableItem.TableInfo.TABLE_NAME)
public class TestStorableItem extends ABambooStorableItem {

    private String mTestStringField;

    private int mTestIntField;

    private long mTestLongField;

    public String getTestStringField() {
        return mTestStringField;
    }

    public TestStorableItem setTestStringField(String testStringField) {
        mTestStringField = testStringField;
        return this;
    }

    public int getTestIntField() {
        return mTestIntField;
    }

    public TestStorableItem setTestIntField(int testIntField) {
        mTestIntField = testIntField;
        return this;
    }

    public long getTestLongField() {
        return mTestLongField;
    }

    public TestStorableItem setTestLongField(long testLongField) {
        mTestLongField = testLongField;
        return this;
    }

    @NonNull @Override public ContentValues _toContentValues(@NonNull Resources res) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(TableInfo.TEST_STRING_FIELD, mTestStringField);
        contentValues.put(TableInfo.TEST_INT_FIELD, mTestIntField);
        contentValues.put(TableInfo.TEST_LONG_FIELD, mTestLongField);

        return contentValues;
    }

    @Override
    public void _fillFromCursor(@NonNull Cursor cursor) {
        mTestStringField = cursor.getString(cursor.getColumnIndex(TableInfo.TEST_STRING_FIELD));
        mTestIntField    = cursor.getInt(cursor.getColumnIndex(TableInfo.TEST_INT_FIELD));
        mTestLongField   = cursor.getLong(cursor.getColumnIndex(TableInfo.TEST_LONG_FIELD));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TestStorableItem that = (TestStorableItem) o;

        if (mTestIntField != that.mTestIntField) {
            return false;
        }

        if (mTestLongField != that.mTestLongField) {
            return false;
        }

        if (mTestStringField != null ? !mTestStringField.equals(that.mTestStringField) : that.mTestStringField != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = mTestStringField != null ? mTestStringField.hashCode() : 0;
        result = 31 * result + mTestIntField;
        result = 31 * result + (int) (mTestLongField ^ (mTestLongField >>> 32));
        return result;
    }

    public interface TableInfo {
        String TABLE_NAME = "test_items";

        String TEST_STRING_FIELD = "TEST_STRING_FIELD";
        String TEST_INT_FIELD    = "TEST_INT_FIELD";
        String TEST_LONG_FIELD   = "TEST_LONG_FIELD";

        String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
                BaseColumns._ID + " INTEGER PRIMARY KEY, " +
                TEST_STRING_FIELD + " TEXT, " +
                TEST_INT_FIELD + " INTEGER, " +
                TEST_LONG_FIELD + " INTEGER);"; // SQLite Integer is 64bits -> Java long
    }
}
