package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;

import com.pushtorefresh.android.bamboostorage.query.QueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.RawQueryBuilder;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetOperationDesignTest extends OperationDesignTest {

    @Test public void getCursorBlocking() {
        Cursor cursor = bambooStorage()
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
        List<User> users = bambooStorage()
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
        Observable<Cursor> observableCursor = bambooStorage()
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
        Observable<List<User>> observableUsers = bambooStorage()
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
        Cursor cursor = bambooStorage()
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
        Observable<Cursor> cursorObservable = bambooStorage()
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
        List<User> users = bambooStorage()
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
        Observable<List<User>> usersObservable = bambooStorage()
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
        Observable<Cursor> usersObservableStream = bambooStorage()
                .get()
                .cursor()
                .withQuery(new QueryBuilder().table("users").build())
                .prepare()
                .createObservableStream();
    }

    @Test public void getListOfObjectsObservableStream() {
        Observable<List<User>> usersObservableStream = bambooStorage()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new QueryBuilder().table("users").build())
                .prepare()
                .createObservableStream();
    }
}
