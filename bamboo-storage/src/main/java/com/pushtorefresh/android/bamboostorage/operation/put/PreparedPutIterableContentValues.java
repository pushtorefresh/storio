package com.pushtorefresh.android.bamboostorage.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutIterableContentValues extends PreparedPut<ContentValues, PutCollectionResult<ContentValues>> {

    @NonNull private final Iterable<ContentValues> contentValuesIterable;
    private final boolean useTransactionIfPossible;

    private PreparedPutIterableContentValues(
            @NonNull BambooStorage bambooStorage,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull Iterable<ContentValues> contentValuesIterable, boolean useTransactionIfPossible) {

        super(bambooStorage, putResolver);
        this.contentValuesIterable = contentValuesIterable;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    @NonNull @Override public PutCollectionResult<ContentValues> executeAsBlocking() {
        final BambooStorage.Internal internal = bambooStorage.internal();

        final Map<ContentValues, PutResult> putResults = new HashMap<>();

        final boolean withTransaction = useTransactionIfPossible
                && internal.areTransactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (ContentValues contentValues : contentValuesIterable) {
                final PutResult putResult = putResolver.performPut(bambooStorage, contentValues);
                putResults.put(contentValues, putResult);
                putResolver.afterPut(contentValues, putResult);

                if (!withTransaction) {
                    internal.notifyAboutChanges(putResult.affectedTables());
                }
            }

            if (withTransaction) {
                bambooStorage.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                bambooStorage.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<>(1); // in most cases it will be 1 table

                    for (final ContentValues contentValues : putResults.keySet()) {
                        affectedTables.addAll(putResults.get(contentValues).affectedTables());
                    }

                    bambooStorage.internal().notifyAboutChanges(affectedTables);
                }
            }
        }

        return new PutCollectionResult<>(putResults);
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
        private boolean useTransactionIfPossible = true;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Iterable<ContentValues> contentValuesIterable) {
            this.bambooStorage = bambooStorage;
            this.contentValuesIterable = contentValuesIterable;
        }

        @NonNull public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        @NonNull public Builder useTransactionIfPossible() {
            useTransactionIfPossible = true;
            return this;
        }

        @NonNull public Builder dontUseTransaction() {
            useTransactionIfPossible = false;
            return this;
        }

        @NonNull public PreparedPutIterableContentValues prepare() {
            return new PreparedPutIterableContentValues(
                    bambooStorage,
                    putResolver,
                    contentValuesIterable,
                    useTransactionIfPossible
            );
        }
    }
}
