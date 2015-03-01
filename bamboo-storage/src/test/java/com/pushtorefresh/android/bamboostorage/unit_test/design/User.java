package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.query.DeleteQueryBuilder;

public class User {

    public  static final String TABLE = "users";
    private static final String COLUMN_ID = BaseColumns._ID;
    private static final String COLUMN_EMAIL = "email";

    public static final MapFunc<Cursor, User> MAP_FROM_CURSOR = new MapFunc<Cursor, User>() {
        @Override public User map(Cursor cursor) {
            return new User(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            );
        }
    };

    public static final MapFunc<User, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<User, ContentValues>() {
        @Override public ContentValues map(User user) {
            final ContentValues contentValues = new ContentValues(2);

            contentValues.put(COLUMN_ID, user.id);
            contentValues.put(COLUMN_EMAIL, user.email);

            return contentValues;
        }
    };

    public static final MapFunc<User, DeleteQuery> MAP_TO_DELETE_QUERY = new MapFunc<User, DeleteQuery>() {
        @Override public DeleteQuery map(User user) {
            return new DeleteQueryBuilder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(String.valueOf(user.id))
                    .build();
        }
    };

    private final Long id;
    private final String email;

    public User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
