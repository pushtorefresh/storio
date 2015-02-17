package com.pushtorefresh.android.bamboostorage.integration_test.db;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.StorableType;

@StorableType(idFieldName = User.COLUMN_ID, tableName = User.TABLE_NAME)
public class User implements BambooStorableType {

    static final String TABLE_NAME      = "users";

    public static final String COLUMN_ID       = "internal_id";
    public static final String COLUMN_NAME     = "name";
    public static final String COLUMN_SURNAME  = "surname";

    public static final String CREATE_TABLE_QUERY = "CREATE TABLE " + TABLE_NAME + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_NAME + " TEXT, " +
            COLUMN_SURNAME + " TEXT)";

    @Nullable private Long storableId;

    @Nullable private String name;
    @Nullable private String surname;

    @Override @Nullable public Long getStorableId() {
        return storableId;
    }

    @Override
    public void setStorableId(@Nullable Long storableId) {
        this.storableId = storableId;
    }

    @Nullable public String getName() {
        return name;
    }

    @NonNull public User setName(@Nullable String name) {
        this.name = name;
        return this;
    }

    @Nullable public String getSurname() {
        return surname;
    }

    @NonNull public User setSurname(@Nullable String surname) {
        this.surname = surname;
        return this;
    }
}
