package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;

import com.pushtorefresh.android.bamboostorage.operation.put.PutCollectionOfObjectResult;
import com.pushtorefresh.android.bamboostorage.operation.put.PutResult;
import com.pushtorefresh.android.bamboostorage.query.InsertQueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

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
                .into(User.TABLE)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putObjectAsObservable() {
        User user = newUser();

        Observable<PutResult> observablePutResult = bambooStorage()
                .put()
                .object(user)
                .into(User.TABLE)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }

    @Test public void putCollectionOfObjectsBlocking() {
        List<User> users = new ArrayList<>();

        PutCollectionOfObjectResult<User> putResult = bambooStorage()
                .put()
                .objects(users)
                .into(User.TABLE)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putCollectionOfObjectsObservable() {
        List<User> users = new ArrayList<>();

        Observable<PutCollectionOfObjectResult<User>> putResultObservable = bambooStorage()
                .put()
                .objects(users)
                .into(User.TABLE)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .prepare()
                .createObservable();
    }


    @Test public void updateByQueryBlocking() {
        User user = newUser();

        PutResult putResult = bambooStorage()
                .put()
                .object(user)
                .into(User.TABLE)
                .withMapFunc(User.MAP_TO_CONTENT_VALUES)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void insertContentValuesBlocking() {
        final ContentValues contentValues = new ContentValues();

        long insertedRowId = bambooStorage()
                .put()
                .contentValues(contentValues)
                .insertQuery(new InsertQueryBuilder()
                        .table("users")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void insertContentValuesAsObservable() {
        final ContentValues contentValues = new ContentValues();

        Observable<Long> observableInsertedRowId = bambooStorage()
                .put()
                .contentValues(contentValues)
                .insertQuery(new InsertQueryBuilder()
                        .table("users")
                        .build())
                .prepare()
                .createObservable();
    }

    @Test public void updateContentValuesBlocking() {
        final ContentValues contentValues = new ContentValues();

        long updatedRowsCount = bambooStorage()
                .put()
                .contentValues(contentValues)
                .updateQuery(new UpdateQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void updateContentValuesAsObservable() {
        final ContentValues contentValues = new ContentValues();

        Observable<Long> observableUpdatedRowsCount = bambooStorage()
                .put()
                .contentValues(contentValues)
                .updateQuery(new UpdateQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .prepare()
                .createObservable();
    }
}
