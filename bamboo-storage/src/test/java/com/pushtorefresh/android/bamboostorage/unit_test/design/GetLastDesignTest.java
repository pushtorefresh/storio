package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

import org.junit.Test;

import rx.Observable;

public class GetLastDesignTest {

    @NonNull BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void getLastAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getLast()
                .asCursor();
    }

    @Test public void getLastAsObject() {
        User user = getBambooStorage()
                .forType(User.class)
                .getLast()
                .asObject();
    }

    @Test public void getLastAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getLast()
                .asObservable();
    }

    @Test public void getLastWithParamsAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getLast(QueryBuilder.allFieldsNull())
                .asCursor();
    }

    @Test public void getLastWithParamsAsObject() {
        User user = getBambooStorage()
                .forType(User.class)
                .getLast(QueryBuilder.allFieldsNull())
                .asObject();
    }

    @Test public void getLastWithParamsAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getLast(QueryBuilder.allFieldsNull())
                .asObservable();
    }
}
