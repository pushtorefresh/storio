package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;

import java.lang.Override;

import rx.functions.Func1;

public class User implements BambooStorableType {

    public static final Func1<Cursor, User> MAP_FUNC_DEFAULT = new Func1<Cursor, User>() {
        @Override public User call(Cursor cursor) {
            return new User();
        }
    };

    @Nullable @Override public Long getStorableId() {
        return null;
    }

    @Override public void setStorableId(@Nullable Long storableId) { }

}
