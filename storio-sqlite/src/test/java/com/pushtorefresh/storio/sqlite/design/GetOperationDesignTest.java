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
        Cursor cursor = storIOSQLite()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsBlocking() {
        List<User> users = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getCursorObservable() {
        Observable<Cursor> observableCursor = storIOSQLite()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getListOfObjectsObservable() {
        Observable<List<User>> observableUsers = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void getCursorWithRawQueryBlocking() {
        Cursor cursor = storIOSQLite()
                .get()
                .cursor()
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getCursorWithRawQueryObservable() {
        Observable<Cursor> cursorObservable = storIOSQLite()
                .get()
                .cursor()
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test
    public void getListOfObjectsWithRawQueryBlocking() {
        List<User> users = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getListOfObjectsWithRawQueryObservable() {
        Observable<List<User>> usersObservable = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .createObservable();
    }
}
