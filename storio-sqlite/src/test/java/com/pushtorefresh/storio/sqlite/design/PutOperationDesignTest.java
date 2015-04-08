package com.pushtorefresh.storio.sqlite.design;

import android.content.ContentValues;

import com.pushtorefresh.storio.sqlite.operation.put.PutResults;
import com.pushtorefresh.storio.sqlite.operation.put.PutResult;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;

public class PutOperationDesignTest extends OperationDesignTest {

    @Test
    public void putObjectBlocking() {
        User user = newUser();

        PutResult putResult = storIOSQLiteDb()
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectObservable() {
        User user = newUser();

        Observable<PutResult> observablePutResult = storIOSQLiteDb()
                .put()
                .object(user)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsIterableBlocking() {
        Iterable<User> users = new ArrayList<User>();

        PutResults<User> putResults = storIOSQLiteDb()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsIterableObservable() {
        Iterable<User> users = new ArrayList<User>();

        Observable<PutResults<User>> putResultsObservable = storIOSQLiteDb()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsArrayBlocking() {
        User[] users = new User[]{};

        PutResults<User> putResults = storIOSQLiteDb()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsArrayObservable() {
        User[] users = new User[]{};

        Observable<PutResults<User>> putResultsObservable = storIOSQLiteDb()
                .put()
                .objects(users)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .withPutResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesBlocking() {
        ContentValues contentValues = User.MAP_TO_CONTENT_VALUES.map(newUser());

        PutResult putResult = storIOSQLiteDb()
                .put()
                .contentValues(contentValues)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesObservable() {
        ContentValues contentValues = User.MAP_TO_CONTENT_VALUES.map(newUser());

        Observable<PutResult> putResult = storIOSQLiteDb()
                .put()
                .contentValues(contentValues)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesIterableBlocking() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(User.MAP_TO_CONTENT_VALUES.map(newUser()));

        PutResults<ContentValues> putResults = storIOSQLiteDb()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableObservable() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(User.MAP_TO_CONTENT_VALUES.map(newUser()));

        Observable<PutResults<ContentValues>> putResults = storIOSQLiteDb()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesArrayBlocking() {
        ContentValues[] contentValuesArray = {User.MAP_TO_CONTENT_VALUES.map(newUser())};

        PutResults<ContentValues> putResults = storIOSQLiteDb()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesArrayObservable() {
        ContentValues[] contentValuesArray = {User.MAP_TO_CONTENT_VALUES.map(newUser())};

        Observable<PutResults<ContentValues>> putResults = storIOSQLiteDb()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(User.PUT_RESOLVER_FOR_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }
}
