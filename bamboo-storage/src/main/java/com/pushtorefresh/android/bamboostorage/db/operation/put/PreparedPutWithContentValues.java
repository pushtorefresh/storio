package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithContentValues extends PreparedPut<ContentValues, PutResult> {

    @NonNull private final ContentValues contentValues;

    private PreparedPutWithContentValues(@NonNull BambooStorage bambooStorage, @NonNull PutResolver<ContentValues> putResolver, @NonNull ContentValues contentValues) {
        super(bambooStorage, putResolver);
        this.contentValues = contentValues;
    }

    @NonNull @Override public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(
                bambooStorage,
                contentValues
        );

        putResolver.afterPut(contentValues, putResult);
        bambooStorage.internal().notifyAboutChanges(putResult.affectedTables());
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

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final ContentValues contentValues;

        private PutResolver<ContentValues> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull ContentValues contentValues) {
            this.bambooStorage = bambooStorage;
            this.contentValues = contentValues;
        }

        @NonNull public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedPutWithContentValues prepare() {
            return new PreparedPutWithContentValues(
                    bambooStorage,
                    putResolver,
                    contentValues
            );
        }
    }
}
