package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.query.InsertQueryBuilder;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

import org.junit.Test;

import rx.Observable;

public class PutContentValuesDesignTest {

    @NonNull private BambooStorage bambooStorage() {
        return new DesignTestBambooStorageImpl();
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
