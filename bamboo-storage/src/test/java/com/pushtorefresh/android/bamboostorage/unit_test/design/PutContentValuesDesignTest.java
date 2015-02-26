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
                .asContentValues()
                .query(new InsertQueryBuilder()
                        .table("users")
                        .build())
                .data(contentValues)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void insertContentValuesAsObservable() {
        final ContentValues contentValues = new ContentValues();

        Observable<Long> observableInsertedRowId = bambooStorage()
                .put()
                .asContentValues()
                .query(new InsertQueryBuilder()
                        .table("users")
                        .build())
                .data(contentValues)
                .prepare()
                .createObservable();
    }

    @Test public void updateContentValuesBlocking() {
        final ContentValues contentValues = new ContentValues();

        long updatedRowsCount = bambooStorage()
                .put()
                .asContentValues()
                .updateQuery(new UpdateQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .data(contentValues)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void updateContentValuesAsObservable() {
        final ContentValues contentValues = new ContentValues();

        Observable<Long> observableUpdatedRowsCount = bambooStorage()
                .put()
                .asContentValues()
                .updateQuery(new UpdateQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs("artem.zinnatullin@gmail.com")
                        .build())
                .data(contentValues)
                .prepare()
                .createObservable();
    }
}
