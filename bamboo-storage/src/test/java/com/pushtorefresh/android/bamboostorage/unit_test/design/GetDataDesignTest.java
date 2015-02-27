package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.query.QueryBuilder;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetDataDesignTest {

    @NonNull private BambooStorage bambooStorage() {
        return new DesignTestBambooStorageImpl();
    }

    @Test public void getAsCursor() {
        Cursor cursor = bambooStorage()
                .get()
                .asCursor()
                .query(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getAsObjects() {
        List<User> users = bambooStorage()
                .get()
                .asListOfObjects(User.class)
                .mapFunc(User.MAP_FROM_CURSOR)
                .query(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getAsObservableCursor() {
        Observable<Cursor> observableCursor = bambooStorage()
                .get()
                .asCursor()
                .query(new QueryBuilder()
                        .table("users")
                        .whereArgs("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void getAsObservableObjects() {
        Observable<List<User>> observableUsers = bambooStorage()
                .get()
                .asListOfObjects(User.class)
                .mapFunc(User.MAP_FROM_CURSOR)
                .query(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }
}
