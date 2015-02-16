package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetAllDesignTest {

    @NonNull public BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void allAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getAll()
                .asCursor();
    }

    @Test public void allAsList() {
        List<User> users = getBambooStorage()
                .forType(User.class)
                .getAll()
                .asList();
    }

    @Test public void allAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getAll()
                .asObservable();
    }

    @Test public void allAsObservableList() {
        Observable<List<User>> usersObservable = getBambooStorage()
                .forType(User.class)
                .getAll()
                .asObservableList();
    }

    @Test public void allWithParamsAsCursor() {
        Cursor cursor = getBambooStorage()
                .forType(User.class)
                .getAll(QueryBuilder.allFieldsNull())
                .asCursor();
    }

    @Test public void allWithParamsAsList() {
        List<User> users = getBambooStorage()
                .forType(User.class)
                .getAll(QueryBuilder.allFieldsNull())
                .asList();
    }

    @Test public void allWithParamsAsObservable() {
        Observable<User> userObservable = getBambooStorage()
                .forType(User.class)
                .getAll(QueryBuilder.allFieldsNull())
                .asObservable();
    }

    @Test public void allWithParamsAsObservableList() {
        Observable<List<User>> usersObservable = getBambooStorage()
                .forType(User.class)
                .getAll(QueryBuilder.allFieldsNull())
                .asObservableList();
    }
}