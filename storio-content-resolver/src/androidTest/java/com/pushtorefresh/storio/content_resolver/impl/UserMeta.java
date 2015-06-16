package com.pushtorefresh.storio.content_resolver.impl;

import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.DefaultGetResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.contentresolver.query.InsertQuery;
import com.pushtorefresh.storio.contentresolver.query.UpdateQuery;

class UserMeta {

    static final String TABLE = "users";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_EMAIL = "email";

    // We all will be very old when Java will support string interpolation =(
    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_EMAIL + " TEXT NOT NULL" +
            ");";

    static final String CONTENT_URI = "content://" + TestContentProvider.AUTHORITY + "/" + TABLE;


    static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull User user) {
            return InsertQuery.builder()
                    .uri(CONTENT_URI)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull User user) {
            return UpdateQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(user.id())
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull User user) {
            final ContentValues contentValues = new ContentValues(2); // wow, such optimization

            contentValues.put(COLUMN_ID, user.id());
            contentValues.put(COLUMN_EMAIL, user.email());

            return contentValues;
        }

        @NonNull
        @Override
        public PutResult performPut(@NonNull StorIOContentResolver storIOContentResolver, @NonNull User user) {
            final PutResult putResult = super.performPut(storIOContentResolver, user);

            if (putResult.wasInserted()) {
                final Uri insertedUri = putResult.insertedUri();
                user.setId(insertedUri != null ? ContentUris.parseId(insertedUri) : null);
            }

            return putResult;
        }
    };

    static final GetResolver<User> GET_RESOLVER = new DefaultGetResolver<User>() {
        @NonNull
        @Override
        public User mapFromCursor(@NonNull Cursor cursor) {
            return User.newInstance(
                    cursor.getLong(cursor.getColumnIndex(COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL))
            );
        }
    };

    static final DeleteResolver<User> DELETE_RESOLVER = new DefaultDeleteResolver<User>() {
        @NonNull
        @Override
        protected DeleteQuery mapToDeleteQuery(@NonNull User user) {
            return DeleteQuery.builder()
                    .uri(CONTENT_URI)
                    .where(COLUMN_ID + "=?")
                    .whereArgs(user.id())
                    .build();
        }
    };

    static final DeleteQuery DELETE_QUERY_ALL = DeleteQuery.builder()
            .uri(CONTENT_URI)
            .build();

    private UserMeta() {
        throw new IllegalStateException("No instances please");
    }
}
