package com.pushtorefresh.android.bamboostorage.db.unit_test.design;

import android.database.Cursor;

import com.pushtorefresh.android.bamboostorage.db.query.QueryBuilder;
import com.pushtorefresh.android.bamboostorage.db.query.RawQueryBuilder;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetOperationDesignTest extends OperationDesignTest {

    @Test public void getCursorBlocking() {
        Cursor cursor = bambooStorageDb()
                .get()
                .cursor()
                .withQuery(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getListOfObjectsBlocking() {
        List<User> users = bambooStorageDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getCursorObservable() {
        Observable<Cursor> observableCursor = bambooStorageDb()
                .get()
                .cursor()
                .withQuery(new QueryBuilder()
                        .table("users")
                        .whereArgs("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void getListOfObjectsObservable() {
        Observable<List<User>> observableUsers = bambooStorageDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new QueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void getCursorWithRawQueryBlocking() {
        Cursor cursor = bambooStorageDb()
                .get()
                .cursor()
                .withQuery(new RawQueryBuilder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getCursorWithRawQueryObservable() {
        Observable<Cursor> cursorObservable = bambooStorageDb()
                .get()
                .cursor()
                .withQuery(new RawQueryBuilder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void getListOfObjectsWithRawQueryBlocking() {
        List<User> users = bambooStorageDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQueryBuilder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void getListOfObjectsWithRawQueryObservable() {
        Observable<List<User>> usersObservable = bambooStorageDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQueryBuilder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void getCursorObservableStream() {
        Observable<Cursor> usersObservableStream = bambooStorageDb()
                .get()
                .cursor()
                .withQuery(new QueryBuilder().table("users").build())
                .prepare()
                .createObservableStream();
    }

    @Test public void getListOfObjectsObservableStream() {
        Observable<List<User>> usersObservableStream = bambooStorageDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new QueryBuilder().table("users").build())
                .prepare()
                .createObservableStream();
    }
}
