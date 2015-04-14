package com.pushtorefresh.storio.sqlite.design;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operation.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResult;
import com.pushtorefresh.storio.sqlite.operation.put.PutResults;
import com.pushtorefresh.storio.sqlite.query.InsertQuery;
import com.pushtorefresh.storio.sqlite.query.UpdateQuery;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import rx.Observable;

import static org.mockito.Mockito.mock;

public class PutOperationDesignTest extends OperationDesignTest {

    private static final PutResolver<ContentValues> CONTENT_VALUES_PUT_RESOLVER = new DefaultPutResolver<ContentValues>() {
        @NonNull
        @Override
        protected InsertQuery mapToInsertQuery(@NonNull ContentValues object) {
            return new InsertQuery.Builder()
                    .table("some_table")
                    .build();
        }

        @NonNull
        @Override
        protected UpdateQuery mapToUpdateQuery(@NonNull ContentValues object) {
            return new UpdateQuery.Builder()
                    .table("some_table")
                    .where("_id = ?")
                    .whereArgs(object.get("_id"))
                    .build();
        }

        @NonNull
        @Override
        protected ContentValues mapToContentValues(@NonNull ContentValues object) {
            return object; // easy
        }
    };

    @Test
    public void putObjectBlocking() {
        User user = newUser();

        PutResult putResult = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectObservable() {
        User user = newUser();

        Observable<PutResult> observablePutResult = storIOSQLite()
                .put()
                .object(user)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsIterableBlocking() {
        Iterable<User> users = new ArrayList<User>();

        PutResults<User> putResults = storIOSQLite()
                .put()
                .objects(User.class, users)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsIterableObservable() {
        Iterable<User> users = new ArrayList<User>();

        Observable<PutResults<User>> putResultsObservable = storIOSQLite()
                .put()
                .objects(User.class, users)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putObjectsArrayBlocking() {
        User[] users = new User[]{};

        PutResults<User> putResults = storIOSQLite()
                .put()
                .objects(User.class, users)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putObjectsArrayObservable() {
        User[] users = new User[]{};

        Observable<PutResults<User>> putResultsObservable = storIOSQLite()
                .put()
                .objects(User.class, users)
                .withPutResolver(UserTableInfo.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesBlocking() {
        PutResult putResult = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesObservable() {
        Observable<PutResult> putResult = storIOSQLite()
                .put()
                .contentValues(mock(ContentValues.class))
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesIterableBlocking() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        PutResults<ContentValues> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesIterableObservable() {
        Iterable<ContentValues> contentValuesIterable
                = Arrays.asList(mock(ContentValues.class));

        Observable<PutResults<ContentValues>> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesIterable)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test
    public void putContentValuesArrayBlocking() {
        ContentValues[] contentValuesArray = {mock(ContentValues.class)};

        PutResults<ContentValues> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void putContentValuesArrayObservable() {
        ContentValues[] contentValuesArray = {mock(ContentValues.class)};

        Observable<PutResults<ContentValues>> putResults = storIOSQLite()
                .put()
                .contentValues(contentValuesArray)
                .withPutResolver(CONTENT_VALUES_PUT_RESOLVER)
                .prepare()
                .createObservable();
    }
}
