package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutIterableContentValues extends PreparedPut<ContentValues, PutCollectionResult<ContentValues>> {

    @NonNull private final Iterable<ContentValues> contentValuesIterable;

    private PreparedPutIterableContentValues(
            @NonNull BambooStorage bambooStorage,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull Iterable<ContentValues> contentValuesIterable) {

        super(bambooStorage, putResolver);
        this.contentValuesIterable = contentValuesIterable;
    }

    @NonNull @Override public PutCollectionResult<ContentValues> executeAsBlocking() {
        final Map<ContentValues, PutResult> putResultsMap = new HashMap<>();

        for (ContentValues contentValues : contentValuesIterable) {
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
        @NonNull private final Iterable<ContentValues> contentValuesIterable;

        private PutResolver<ContentValues> putResolver;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Iterable<ContentValues> contentValuesIterable) {
            this.bambooStorage = bambooStorage;
            this.contentValuesIterable = contentValuesIterable;
        }

        @NonNull public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public PreparedPutIterableContentValues prepare() {
            return new PreparedPutIterableContentValues(
                    bambooStorage,
                    putResolver,
                    contentValuesIterable
            );
        }
    }
}
