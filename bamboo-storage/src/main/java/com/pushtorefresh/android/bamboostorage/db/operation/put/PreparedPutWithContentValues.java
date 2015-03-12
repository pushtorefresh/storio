package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithContentValues extends PreparedPut<ContentValues, PutResult> {

    @NonNull private final ContentValues contentValues;

    private PreparedPutWithContentValues(@NonNull BambooStorageDb bambooStorageDb, @NonNull PutResolver<ContentValues> putResolver, @NonNull ContentValues contentValues) {
        super(bambooStorageDb, putResolver);
        this.contentValues = contentValues;
    }

    @NonNull @Override public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(
                bambooStorageDb,
                contentValues
        );

        putResolver.afterPut(contentValues, putResult);
        bambooStorageDb.internal().notifyAboutChanges(putResult.affectedTables());
        return putResult;
    }

    @NonNull @Override public Observable<PutResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutResult>() {
            @Override public void call(Subscriber<? super PutResult> subscriber) {
                final PutResult putResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final BambooStorageDb bambooStorageDb;
        @NonNull private final ContentValues contentValues;

        private PutResolver<ContentValues> putResolver;

        public Builder(@NonNull BambooStorageDb bambooStorageDb, @NonNull ContentValues contentValues) {
            this.bambooStorageDb = bambooStorageDb;
            this.contentValues = contentValues;
        }

        @NonNull public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedPutWithContentValues prepare() {
            return new PreparedPutWithContentValues(
                    bambooStorageDb,
                    putResolver,
                    contentValues
            );
        }
    }
}
