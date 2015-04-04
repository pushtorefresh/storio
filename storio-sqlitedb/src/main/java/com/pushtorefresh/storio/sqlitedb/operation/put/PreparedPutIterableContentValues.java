package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutIterableContentValues extends PreparedPut<ContentValues, PutCollectionResult<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValuesIterable;

    private final boolean useTransactionIfPossible;

    PreparedPutIterableContentValues(
            @NonNull StorIOSQLiteDb storIOSQLiteDb,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull Iterable<ContentValues> contentValuesIterable, boolean useTransactionIfPossible) {

        super(storIOSQLiteDb, putResolver);
        this.contentValuesIterable = contentValuesIterable;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    @NonNull
    @Override
    public PutCollectionResult<ContentValues> executeAsBlocking() {
        final StorIOSQLiteDb.Internal internal = storIOSQLiteDb.internal();

        final Map<ContentValues, PutResult> putResults = new HashMap<>();

        final boolean withTransaction = useTransactionIfPossible
                && internal.transactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (ContentValues contentValues : contentValuesIterable) {
                final PutResult putResult = putResolver.performPut(storIOSQLiteDb, contentValues);
                putResults.put(contentValues, putResult);
                putResolver.afterPut(contentValues, putResult);

                if (!withTransaction) {
                    internal.notifyAboutChanges(new Changes(putResult.affectedTable()));
                }
            }

            if (withTransaction) {
                storIOSQLiteDb.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                storIOSQLiteDb.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<>(1); // in most cases it will be 1 table

                    for (final ContentValues contentValues : putResults.keySet()) {
                        affectedTables.add(putResults.get(contentValues).affectedTable());
                    }

                    storIOSQLiteDb.internal().notifyAboutChanges(new Changes(affectedTables));
                }
            }
        }

        return PutCollectionResult.newInstance(putResults);
    }

    @NonNull
    @Override
    public Observable<PutCollectionResult<ContentValues>> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

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

    /**
     * Builder for {@link PreparedPutIterableContentValues}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLiteDb storIOSQLiteDb;
        @NonNull
        private final Iterable<ContentValues> contentValuesIterable;

        private PutResolver<ContentValues> putResolver;
        private boolean useTransactionIfPossible = true;

        Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull Iterable<ContentValues> contentValuesIterable) {
            this.storIOSQLiteDb = storIOSQLiteDb;
            this.contentValuesIterable = contentValuesIterable;
        }

        /**
         * Required: Specifies {@link PutResolver} for Put Operation
         * which allows you to customize behavior of Put Operation
         *
         * @param putResolver put resolver
         * @return builder
         * @see {@link DefaultPutResolver} — easy way to create {@link PutResolver}
         */
        @NonNull
        public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Optional: Defines that Put Operation will use transaction if it is supported by implementation of {@link StorIOSQLiteDb}
         * By default, transaction will be used
         *
         * @return builder
         */
        @NonNull
        public Builder useTransactionIfPossible() {
            useTransactionIfPossible = true;
            return this;
        }

        /**
         * Optional: Defines that Put Operation won't use transaction
         * By default, transaction will be used
         *
         * @return builder
         */
        @NonNull
        public Builder dontUseTransaction() {
            useTransactionIfPossible = false;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutIterableContentValues} instance
         */
        @NonNull
        public PreparedPutIterableContentValues prepare() {
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutIterableContentValues(
                    storIOSQLiteDb,
                    putResolver,
                    contentValuesIterable,
                    useTransactionIfPossible
            );
        }
    }
}
