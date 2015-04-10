package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutContentValuesIterable extends PreparedPut<ContentValues, PutResults<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValuesIterable;

    private final boolean useTransactionIfPossible;

    PreparedPutContentValuesIterable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull PutResolver<ContentValues> putResolver,
            @NonNull Iterable<ContentValues> contentValuesIterable, boolean useTransactionIfPossible) {

        super(storIOSQLite, putResolver);
        this.contentValuesIterable = contentValuesIterable;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null results of Put Operation
     */
    @NonNull
    @Override
    public PutResults<ContentValues> executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();

        final Map<ContentValues, PutResult> putResults = new HashMap<ContentValues, PutResult>();

        final boolean withTransaction = useTransactionIfPossible
                && internal.transactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (ContentValues contentValues : contentValuesIterable) {
                final PutResult putResult = putResolver.performPut(storIOSQLite, contentValues);
                putResults.put(contentValues, putResult);
                putResolver.afterPut(contentValues, putResult);

                if (!withTransaction) {
                    internal.notifyAboutChanges(Changes.newInstance(putResult.affectedTable()));
                }
            }

            if (withTransaction) {
                storIOSQLite.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                storIOSQLite.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table

                    for (final ContentValues contentValues : putResults.keySet()) {
                        affectedTables.add(putResults.get(contentValues).affectedTable());
                    }

                    storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(affectedTables));
                }
            }
        }

        return PutResults.newInstance(putResults);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send results to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send results to observer
     */
    @NonNull
    @Override
    public Observable<PutResults<ContentValues>> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;
        @NonNull
        private final Iterable<ContentValues> contentValuesIterable;

        private PutResolver<ContentValues> putResolver;
        private boolean useTransactionIfPossible = true;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Iterable<ContentValues> contentValuesIterable) {
            this.storIOSQLite = storIOSQLite;
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
         * Optional: Defines that Put Operation will use transaction
         * if it is supported by implementation of {@link StorIOSQLite}
         * <p>
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
         * <p>
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
         * @return {@link PreparedPutContentValuesIterable} instance
         */
        @NonNull
        public PreparedPutContentValuesIterable prepare() {
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutContentValuesIterable(
                    storIOSQLite,
                    putResolver,
                    contentValuesIterable,
                    useTransactionIfPossible
            );
        }
    }
}
