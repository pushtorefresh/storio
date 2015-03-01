package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResolver;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.query.DeleteQueryBuilder;

public class User {

    public static final MapFunc<Cursor, User> MAP_FROM_CURSOR = new MapFunc<Cursor, User>() {
        @Override public User map(Cursor cursor) {
            // normally you should get some values from cursor
            return new User();
        }
    };

    public static final MapFunc<User, ContentValues> MAP_TO_CONTENT_VALUES = new MapFunc<User, ContentValues>() {
        @Override public ContentValues map(User user) {
            // normally you should fill content values with entity data
            return new ContentValues();
        }
    };

    public static final PutResolver<User> PUT_RESOLVER = new PutResolver<User>() {

        @NonNull @Override public String getTableName(@NonNull User object) {
            return "users";
        }

        @NonNull @Override public String getInternalIdColumnName(@NonNull User object) {
            return "_id";
        }

        @Nullable@Override public Long getInternalIdValue(@NonNull User object) {
            return object.internalId;
        }

        @Override public void setInternalId(@NonNull User user, long id) {
            user.internalId = id;
        }
    };

    public static final MapFunc<User, DeleteQuery> MAP_TO_DELETE_QUERY = new MapFunc<User, DeleteQuery>() {
        @Override public DeleteQuery map(User user) {
            return new DeleteQueryBuilder()
                    .table("users")
                    .where("_id = ?")
                    .whereArgs(String.valueOf(user.internalId))
                    .build();
        }
    };

    private Long internalId;
    private String email;

    public String getEmail() {
        return email;
    }
}
