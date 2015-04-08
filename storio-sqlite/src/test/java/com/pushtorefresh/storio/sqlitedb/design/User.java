package com.pushtorefresh.storio.sqlitedb.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlitedb.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResolver;
import com.pushtorefresh.storio.sqlitedb.operation.put.PutResult;
import com.pushtorefresh.storio.sqlitedb.query.DeleteQuery;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test class that represents an object stored in Db
 */
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
            // unfortunately ContentValues is corrupted by Android Gradle Plugin (test)
            final ContentValues contentValues = mock(ContentValues.class);

            when(contentValues.getAsLong(COLUMN_ID))
                    .thenReturn(user.id);

            when(contentValues.getAsString(COLUMN_EMAIL))
                    .thenReturn(user.email);

            return contentValues;
        }
    };

    public static final MapFunc<User, DeleteQuery> MAP_TO_DELETE_QUERY = new MapFunc<User, DeleteQuery>() {
        @Override public DeleteQuery map(User user) {
            return new DeleteQuery.Builder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(String.valueOf(user.id))
                    .build();
        }
    };

    public static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {
        @NonNull @Override protected String getTable() {
            return TABLE;
        }

        @Override public void afterPut(@NonNull User object, @NonNull PutResult putResult) {
            if (putResult.wasInserted()) {
                object.id = putResult.insertedId(); // setting id after insert
            }
        }
    };

    public static final PutResolver<ContentValues> PUT_RESOLVER_FOR_CONTENT_VALUES = new DefaultPutResolver<ContentValues>() {
        @NonNull @Override protected String getTable() {
            return TABLE;
        }
    };

    private Long id;
    private final String email;

    public User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
