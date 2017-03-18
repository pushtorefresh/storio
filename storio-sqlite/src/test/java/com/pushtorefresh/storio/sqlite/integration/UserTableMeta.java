package com.pushtorefresh.storio.sqlite.integration;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

public class UserTableMeta {

    // they are open just for test purposes
    static final String TABLE = "users";
    static final String COLUMN_ID = "_id";
    static final String COLUMN_EMAIL = "email";
    static final String COLUMN_PHONE = "phone";
    static final String NOTIFICATION_TAG = "tag";

    // We all will be very old when Java will support string interpolation :(
    static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_EMAIL + " TEXT NOT NULL, " +
            COLUMN_PHONE + " TEXT" + // optional
            ");";

    static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    static final DeleteQuery DELETE_QUERY_ALL = DeleteQuery.builder()
            .table(TABLE)
            .affectsTags(NOTIFICATION_TAG)
            .build();

    static final PutResolver<User> PUT_RESOLVER = new DefaultPutResolver<User>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull User user) {
            return InsertQuery.builder()
                    .table(TABLE)
                    .affectsTags(NOTIFICATION_TAG)
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull User user) {
            return UpdateQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(user.id())
                    .affectsTags(NOTIFICATION_TAG)
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull User user) {
            final ContentValues contentValues = new ContentValues(2);

            contentValues.put(COLUMN_ID, user.id());
            contentValues.put(COLUMN_EMAIL, user.email());
            contentValues.put(COLUMN_PHONE, user.phone());

            return contentValues;
        }

        @NonNull
        @Override
        public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull User object) {
            final PutResult putResult = super.performPut(storIOSQLite, object);

            if (putResult.wasInserted()) {
                object.setId(putResult.insertedId()); // let's think that we need to set id after insert, sometimes it's really required
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
                    cursor.getString(cursor.getColumnIndex(COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndex(COLUMN_PHONE))
            );
        }
    };
    static final DeleteResolver<User> DELETE_RESOLVER = new DefaultDeleteResolver<User>() {
        @NonNull
        @Override
        public DeleteQuery mapToDeleteQuery(@NonNull User user) {
            return DeleteQuery.builder()
                    .table(TABLE)
                    .where(COLUMN_ID + " = ?")
                    .whereArgs(user.id())
                    .affectsTags(NOTIFICATION_TAG)
                    .build();
        }
    };

    private UserTableMeta() {
        throw new IllegalStateException("No instances please");
    }
}
