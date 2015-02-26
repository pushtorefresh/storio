package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.put.SinglePutResult;
import com.pushtorefresh.android.bamboostorage.query.UpdateQueryBuilder;

import org.junit.Test;

import rx.Observable;

public class PutObjectsDesignTest {

    @NonNull private BambooStorage bambooStorage() {
        return new DesignTestBambooStorageImpl();
    }

    @Test public void putObjectBlocking() {
        User user = new User();

        SinglePutResult putResult = bambooStorage()
                .put()
                .object(user)
                .mapFunc(User.MAP_TO_CONTENT_VALUES)
                .putResolver(User.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();
    }

    @Test public void putObjectAsObservable() {
        User user = new User();

        Observable<SinglePutResult> observablePutResult = bambooStorage()
                .put()
                .object(user)
                .mapFunc(User.MAP_TO_CONTENT_VALUES)
                .putResolver(User.PUT_RESOLVER)
                .prepare()
                .createObservable();
    }

    @Test public void updateByQueryBlocking() {
        User user = new User();

        SinglePutResult putResult = bambooStorage()
                .put()
                .object(user)
                .mapFunc(User.MAP_TO_CONTENT_VALUES)
                .updateQuery(new UpdateQueryBuilder()
                        .table("users")
                        .where("email = ?")
                        .whereArgs(user.getEmail())
                        .build())
                .prepare()
                .executeAsBlocking();
    }
}
