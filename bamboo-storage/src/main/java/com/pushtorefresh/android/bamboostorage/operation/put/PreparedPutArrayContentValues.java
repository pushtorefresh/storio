package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutArrayContentValues extends PreparedPut<ContentValues, PutCollectionResult<ContentValues>> {

    @NonNull private final ContentValues[] mContentValuesArray;

    private PreparedPutArrayContentValues(
            @NonNull BambooStorage bambooStorage,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull ContentValues[] contentValuesArray) {

        super(bambooStorage, putResolver);
        this.mContentValuesArray = contentValuesArray;
    }

    @NonNull @Override public PutCollectionResult<ContentValues> executeAsBlocking() {
        final Map<ContentValues, PutResult> putResultsMap = new HashMap<>();

        for (ContentValues contentValues : mContentValuesArray) {
            final PutResult putResult = putResolver.performPut(bambooStorage, contentValues);
            putResultsMap.put(contentValues, putResult);
            putResolver.afterPut(contentValues, putResult);
        }

        return new PutCollectionResult<>(putResultsMap);
    }

    @NonNull @Override public Observable<PutCollectionResult<ContentValues>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionResult<ContentValues>>() {

            @Override
            public void call(Subscriber<? super PutCollectionResult<ContentValues>> subscriber) {
                final PutCollectionResult<ContentValues> putCollectionResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putCollectionResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final ContentValues[] mContentValuesIterable;

        private PutResolver<ContentValues> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull ContentValues... contentValuesIterable) {
            this.bambooStorage = bambooStorage;
            this.mContentValuesIterable = contentValuesIterable;
        }

        @NonNull public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedPutArrayContentValues prepare() {
            return new PreparedPutArrayContentValues(
                    bambooStorage,
                    putResolver,
                    mContentValuesIterable
            );
        }
    }
}
