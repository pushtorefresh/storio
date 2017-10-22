package com.pushtorefresh.storio2.sqlite.design;

import android.database.Cursor;

import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import org.junit.Test;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

import static io.reactivex.BackpressureStrategy.LATEST;

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
    public void getCursorFlowable() {
        Flowable<Cursor> flowableCursor = storIOSQLite()
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .asRxFlowable(LATEST);
    }

    @Test
    public void getListOfObjectsFlowable() {
        Flowable<List<User>> flowableUsers = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(LATEST);
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
    public void getCursorWithRawQueryFlowable() {
        Flowable<Cursor> cursorFlowable = storIOSQLite()
                .get()
                .cursor()
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .prepare()
                .asRxFlowable(LATEST);
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
    public void getListOfObjectsWithRawQueryFlowable() {
        Flowable<List<User>> usersFlowable = storIOSQLite()
                .get()
                .listOfObjects(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(LATEST);
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
    public void getObjectFlowable() {
        Flowable<User> userFlowable = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(LATEST);
    }

    @Test
    public void getObjectWithRawQueryFlowable() {
        Flowable<User> userFlowable = storIOSQLite()
                .get()
                .object(User.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT FROM bla_bla join on bla_bla_bla WHERE x = ?")
                        .args("arg1", "arg2")
                        .build())
                .withGetResolver(UserTableMeta.GET_RESOLVER)
                .prepare()
                .asRxFlowable(LATEST);
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
