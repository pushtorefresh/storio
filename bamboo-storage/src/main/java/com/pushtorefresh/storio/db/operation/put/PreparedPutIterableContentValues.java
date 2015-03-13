package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.Changes;

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
            @NonNull BambooStorageDb bambooStorageDb,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull Iterable<ContentValues> contentValuesIterable, boolean useTransactionIfPossible) {

        super(bambooStorageDb, putResolver);
        this.contentValuesIterable = contentValuesIterable;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    @NonNull @Override public PutCollectionResult<ContentValues> executeAsBlocking() {
        final BambooStorageDb.Internal internal = bambooStorageDb.internal();

        final Map<ContentValues, PutResult> putResults = new HashMap<>();

        final boolean withTransaction = useTransactionIfPossible
                && internal.areTransactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (ContentValues contentValues : contentValuesIterable) {
                final PutResult putResult = putResolver.performPut(bambooStorageDb, contentValues);
                putResults.put(contentValues, putResult);
                putResolver.afterPut(contentValues, putResult);

                if (!withTransaction) {
                    internal.notifyAboutChanges(new Changes(putResult.affectedTables()));
                }
            }

            if (withTransaction) {
                bambooStorageDb.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                bambooStorageDb.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<>(1); // in most cases it will be 1 table

                    for (final ContentValues contentValues : putResults.keySet()) {
                        affectedTables.addAll(putResults.get(contentValues).affectedTables());
                    }

                    bambooStorageDb.internal().notifyAboutChanges(new Changes(affectedTables));
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

        @NonNull private final BambooStorageDb bambooStorageDb;
        @NonNull private final Iterable<ContentValues> contentValuesIterable;

        private PutResolver<ContentValues> putResolver;
        private boolean useTransactionIfPossible = true;

        public Builder(@NonNull BambooStorageDb bambooStorageDb, @NonNull Iterable<ContentValues> contentValuesIterable) {
            this.bambooStorageDb = bambooStorageDb;
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
                    bambooStorageDb,
                    putResolver,
                    contentValuesIterable,
                    useTransactionIfPossible
            );
        }
    }
}
