package com.pushtorefresh.storio.db.integration_test.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.db.operation.put.PutResolver;
import com.pushtorefresh.storio.db.operation.put.PutResult;
import com.pushtorefresh.storio.db.query.DeleteQuery;

public class User implements Comparable<User> {

    // they are open just for test purposes
    static final String TABLE = "users";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_EMAIL = "email";

    // I'll be very old when Java will support string interpolation :(
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_EMAIL + " TEXT NOT NULL" +
            ");";

    public static final DeleteQuery DELETE_ALL = new DeleteQuery.Builder()
            .table(TABLE)
            .build();

    @NonNull
    public static final MapFunc<User, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<User, ContentValues>() {
        @Override public ContentValues map(User user) {
            final ContentValues contentValues = new ContentValues(2);

            contentValues.put(COLUMN_ID, user.id);
            contentValues.put(COLUMN_EMAIL, user.email);

            return contentValues;
        }
    };

    @NonNull
    public static final MapFunc<Cursor, User> MAP_FROM_CURSOR = new MapFunc<Cursor, User>() {
        @Override public User map(Cursor cursor) {
            return new User(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            );
        }
    };

    @NonNull
    public static final MapFunc<User, DeleteQuery> MAP_TO_DELETE_QUERY = new MapFunc<User, DeleteQuery>() {
        @Override public DeleteQuery map(User user) {
            return new DeleteQuery.Builder()
                    .table(TABLE)
                    .where(COLUMN_ID + "=?")
                    .whereArgs(String.valueOf(user.id))
                    .build();
        }
    };

    @NonNull public static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {
        @NonNull @Override protected String getTable() {
            return TABLE;
        }

        @Override public void afterPut(@NonNull User user, @NonNull PutResult putResult) {
            if (putResult.wasInserted()) {
                user.id = putResult.getInsertedId();
            }
        }
    };

    @Nullable private volatile Long id;
    private String email;

    User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    @Nullable public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean equalsExceptId(@NonNull User other) {
        return email.equals(other.email);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (id != null ? !id.equals(user.id) : user.id != null) return false;
        if (email != null ? !email.equals(user.email) : user.email != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override public int compareTo(@NonNull User another) {
        return email == null ? 0 : email.compareTo(another.getEmail());
    }
}
