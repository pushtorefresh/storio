package com.pushtorefresh.storio.sqlite.design;

import android.database.Cursor;

import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.Single;

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
                .asRxObservable();
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
                .asRxObservable();
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
                .asRxObservable();
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
                .asRxObservable();
    }

    @Test
    public void getObjectBlocking() {
        User user = storIOSQLite()
                .get()
                .object(User.class)
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
    public void getObjectBlockingWithRawQueryBlocking() {
        User user = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void getObjectObservable() {
        Observable<User> userObservable = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxObservable();
    }

    @Test
    public void getObjectWithRawQueryObservable() {
        Observable<User> userObservable = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxObservable();
    }

    @Test
    public void getListOfObjectsSingle() {
        Single<List<User>> singleUsers = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getListOfObjectsWithRawQuerySingle() {
        Single<List<User>> singleUsers = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getObjectSingle() {
        Single<User> singleUser = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getObjectWithRawQuerySingle() {
        Single<User> singleUsers = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxSingle();
    }

    @Test
    public void getCursorSingle() {
        Single<Cursor> singleCursor = storIOSQLite()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .asRxSingle();
    }
}
