package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

import org.junit.Test;

import rx.Observable;

public class GetFirstDesignTest {

    @NonNull public BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void getFirstAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getFirst()
                .asCursor();
    }

    @Test public void getFirstAsObject() {
        User user = getBambooStorage()
                .forType(User.class)
                .getFirst()
                .asObject();
    }

    @Test public void getFirstAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getFirst()
                .asObservable();
    }

    @Test public void getFirstWithParamsAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getFirst(QueryBuilder.allFieldsNull())
                .asCursor();
    }

    @Test public void getFirstWithParamsAsObject() {
        User user = getBambooStorage()
                .forType(User.class)
                .getFirst(QueryBuilder.allFieldsNull())
                .asObject();
    }

    @Test public void getFirstWithParamsAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getFirst(QueryBuilder.allFieldsNull())
                .asObservable();
    }
}
