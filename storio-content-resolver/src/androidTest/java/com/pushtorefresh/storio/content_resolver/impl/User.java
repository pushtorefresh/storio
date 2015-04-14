package com.pushtorefresh.storio.content_resolver.impl;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;

public class User implements Comparable<User> {

    static final String TABLE = "users";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_EMAIL = "email";

    // Artem will be very old when Java will support string interpolation =(
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_EMAIL + " TEXT NOT NULL" +
            ");";

    public static final String CONTENT_URI = "content://" + TestContentProvider.AUTHORITY + "/" + TABLE;

    public static final DeleteQuery DELETE_ALL = new DeleteQuery.Builder()
            .uri(CONTENT_URI)
            .build();

    @NonNull
    public static final MapFunc<User, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<User, ContentValues>() {
        @NonNull
        @Override
        public ContentValues map(@NonNull User user) {
            final ContentValues contentValues = new ContentValues(2);

            contentValues.put(COLUMN_ID, user.id);
            contentValues.put(COLUMN_EMAIL, user.email);

            return contentValues;
        }
    };

    @NonNull
    public static final MapFunc<Cursor, User> MAP_FROM_CURSOR = new MapFunc<Cursor, User>() {
        @NonNull
        @Override
        public User map(@NonNull Cursor cursor) {
            return new User(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            );
        }
    };

    @NonNull
    public static final MapFunc<User, DeleteQuery> MAP_TO_DELETE_QUERY = new MapFunc<User, DeleteQuery>() {
        @NonNull
        @Override
        public DeleteQuery map(@NonNull User user) {
            return new DeleteQuery.Builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + "=?")
                    .whereArgs(String.valueOf(user.id))
                    .build();
        }
    };

    @NonNull
    public static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {

        @NonNull
        @Override
        protected Uri getUri(@NonNull ContentValues contentValues) {
            return Uri.parse(CONTENT_URI);
        }

        @Override
        public void afterPut(@NonNull User user, @NonNull PutResult putResult) {
            if (putResult.wasInserted()) {
                final Uri insertedUri = putResult.insertedUri();
                user.id = insertedUri != null ? ContentUris.parseId(insertedUri) : null;
            }
        }
    };

    @Nullable
    private volatile Long id;
    private String email;

    User(@Nullable Long id, @NonNull String email) {
        this.id = id;
        this.email = email;
    }

    public User(@Nullable Long id) {
        this.id = id;
    }

    @Nullable
    public Long getId() {
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

    @Override
    public int compareTo(@NonNull User another) {
        return email == null ? 0 : email.compareTo(another.getEmail());
    }
}
