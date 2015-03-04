package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;

import com.pushtorefresh.android.bamboostorage.operation.put.PutCollectionOfObjectsResult;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

public class PutOperationDesignTest extends OperationDesignTest {

    @Test public void putObjectBlocking() {
        User user = newUser();

        PutResult putResult = bambooStorage()
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putObjectObservable() {
        User user = newUser();

        Observable<PutResult> observablePutResult = bambooStorage()
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test public void putCollectionOfObjectsBlocking() {
        List<User> users = new ArrayList<>();

        PutCollectionOfObjectsResult<User> putResult = bambooStorage()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<>();

        Observable<PutCollectionOfObjectsResult<User>> putResultObservable = bambooStorage()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test public void putContentValuesBlocking() {
        ContentValues contentValues = User.MAP_TO_CONTENT_VALUES.map(newUser());

        PutResult putResult = bambooStorage()
                .put()
                .contentValues(contentValues)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putContentValuesObservable() {
        ContentValues contentValues = User.MAP_TO_CONTENT_VALUES.map(newUser());

        Observable<PutResult> putResult = bambooStorage()
                .put()
                .contentValues(contentValues)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }
}
