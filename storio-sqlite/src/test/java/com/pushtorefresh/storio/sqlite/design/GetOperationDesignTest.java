package com.pushtorefresh.storio.sqlite.design;

import android.database.Cursor;

import com.pushtorefresh.storio.sqlite.query.Query;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetOperationDesignTest extends OperationDesignTest {

    @Test
    public void getCursorBlocking() {
        Cursor cursor = storIOSQLiteDb()
                .get()
                .cursor()
                .withQuery(new Query.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<User> users = storIOSQLiteDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getCursorObservable() {
        Observable<Cursor> observableCursor = storIOSQLiteDb()
                .get()
                .cursor()
                .withQuery(new Query.Builder()
                        .table("users")
                        .whereArgs("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getListOfObjectsObservable() {
        Observable<List<User>> observableUsers = storIOSQLiteDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getCursorWithRawQueryBlocking() {
        Cursor cursor = storIOSQLiteDb()
                .get()
                .cursor()
                .withQuery(new RawQuery.Builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getCursorWithRawQueryObservable() {
        Observable<Cursor> cursorObservable = storIOSQLiteDb()
                .get()
                .cursor()
                .withQuery(new RawQuery.Builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getListOfObjectsWithRawQueryBlocking() {
        List<User> users = storIOSQLiteDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQuery.Builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsWithRawQueryObservable() {
        Observable<List<User>> usersObservable = storIOSQLiteDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new RawQuery.Builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getCursorObservableStream() {
        Observable<Cursor> usersObservableStream = storIOSQLiteDb()
                .get()
                .cursor()
                .withQuery(new Query.Builder().table("users").build())
                .prepare()
                .createObservableStream();
    }

    @Test
    public void getListOfObjectsObservableStream() {
        Observable<List<User>> usersObservableStream = storIOSQLiteDb()
                .get()
                .listOfObjects(User.class)
                .withMapFunc(User.MAP_FROM_CURSOR)
                .withQuery(new Query.Builder().table("users").build())
                .prepare()
                .createObservableStream();
    }
}
