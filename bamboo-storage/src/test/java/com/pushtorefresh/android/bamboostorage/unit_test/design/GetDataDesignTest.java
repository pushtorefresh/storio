package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.QueryBuilder;

import org.junit.Test;

import java.util.List;

import rx.Observable;

public class GetDataDesignTest {

    @NonNull BambooStorage getBambooStorage() {
        return new BambooStorageForDesignTest();
    }

    @Test public void getAsCursorBlocking() {
        Cursor cursor = getBambooStorage()
                .prepareQuery(QueryBuilder.allFieldsNull())
                .resultAsCursor()
                .executeAsBlocking();
    }

    @Test public void getAsCursorObservable() {
        Observable<Cursor> cursorObservable = getBambooStorage()
                .prepareQuery(QueryBuilder.allFieldsNull())
                .resultAsCursor()
                .executeAsObservable();
    }

    @Test public void getAsObjectsBlocking() {
        List<User> users = getBambooStorage()
                .prepareQuery(QueryBuilder.allFieldsNull())
                .resultAsObjects(User.class, User.MAP_FUNC_DEFAULT)
                .executeAsBlocking();
    }

    @Test public void getAsObjectsObservable() {
        Observable<List<User>> usersObservable = getBambooStorage()
                .prepareQuery(QueryBuilder.allFieldsNull())
                .resultAsObjects(User.class, User.MAP_FUNC_DEFAULT)
                .executeAsObservable();
    }
}
