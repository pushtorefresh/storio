package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public final class PreparedPutContentValuesIterable extends PreparedPut<ContentValues, PutResults<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValuesIterable;

    private final boolean useTransaction;

    PreparedPutContentValuesIterable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Iterable<ContentValues> contentValuesIterable,
            @NonNull PutResolver<ContentValues> putResolver,
            boolean useTransaction) {

        super(storIOSQLite, putResolver);
        this.contentValuesIterable = contentValuesIterable;
        this.useTransaction = useTransaction;
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

        if (useTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (ContentValues contentValues : contentValuesIterable) {
                final PutResult putResult = putResolver.performPut(storIOSQLite, contentValues);
                putResults.put(contentValues, putResult);

                if (!useTransaction) {
                    internal.notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));
                }
            }

            if (useTransaction) {
                storIOSQLite.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (useTransaction) {
                storIOSQLite.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table

                    for (final ContentValues contentValues : putResults.keySet()) {
                        affectedTables.addAll(putResults.get(contentValues).affectedTables());
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
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}
     */
    public static final class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Iterable<ContentValues> contentValuesIterable;

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
        public CompleteBuilder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            checkNotNull(putResolver, "Please specify put resolver");

            return new CompleteBuilder(
                    storIOSQLite,
                    contentValuesIterable,
                    putResolver
            );
        }
    }

    /**
     * Compile-time safe part of {@link Builder}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Iterable<ContentValues> contentValuesIterable;

        @NonNull
        private final PutResolver<ContentValues> putResolver;

        private boolean useTransaction = true;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull Iterable<ContentValues> contentValuesIterable, @NonNull PutResolver<ContentValues> putResolver) {
            this.storIOSQLite = storIOSQLite;
            this.contentValuesIterable = contentValuesIterable;
            this.putResolver = putResolver;
        }

        /**
         * Optional: Defines that Put Operation will use transaction
         * if it is supported by implementation of {@link StorIOSQLite}
         * <p/>
         * By default, transaction will be used
         *
         * @return builder
         */
        @NonNull
        public CompleteBuilder useTransaction(boolean useTransaction) {
            this.useTransaction = useTransaction;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutContentValuesIterable} instance
         */
        @NonNull
        public PreparedPutContentValuesIterable prepare() {
            return new PreparedPutContentValuesIterable(
                    storIOSQLite,
                    contentValuesIterable,
                    putResolver,
                    useTransaction
            );
        }
    }
}
