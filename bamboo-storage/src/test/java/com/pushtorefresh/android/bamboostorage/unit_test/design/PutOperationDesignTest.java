package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;

import com.pushtorefresh.android.bamboostorage.operation.put.PutCollectionResult;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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

        PutCollectionResult<User> putResult = bambooStorage()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<>();

        Observable<PutCollectionResult<User>> putResultObservable = bambooStorage()
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

    @Test public void putContentValuesIterableBlocking() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(User.MAP_TO_CONTENT_VALUES.map(newUser()));

        PutCollectionResult<ContentValues> putResult = bambooStorage()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putContentValuesIterableObservable() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(User.MAP_TO_CONTENT_VALUES.map(newUser()));

        Observable<PutCollectionResult<ContentValues>> putResult = bambooStorage()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }

    @Test public void putContentValuesArrayBlocking() {
        ContentValues[] contentValuesArray = {User.MAP_TO_CONTENT_VALUES.map(newUser())};

        PutCollectionResult<ContentValues> putResult = bambooStorage()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putContentValuesArrayObservable() {
        ContentValues[] contentValuesArray = {User.MAP_TO_CONTENT_VALUES.map(newUser())};

        Observable<PutCollectionResult<ContentValues>> putResult = bambooStorage()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }
}
