package com.pushtorefresh.storio.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public final class PreparedPutContentValuesIterable extends PreparedPut<PutResults<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValuesIterable;

    @NonNull
    private final PutResolver<ContentValues> putResolver;

    private final boolean useTransaction;

    PreparedPutContentValuesIterable(
            @NonNull StorIOSQLite storIOSQLite,
            @NonNull Iterable<ContentValues> contentValuesIterable,
            @NonNull PutResolver<ContentValues> putResolver,
            boolean useTransaction) {

        super(storIOSQLite);
        this.contentValuesIterable = contentValuesIterable;
        this.putResolver = putResolver;
        this.useTransaction = useTransaction;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public PutResults<ContentValues> executeAsBlocking() {
        try {
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
                    internal.setTransactionSuccessful();
                    transactionSuccessful = true;
                }
            } finally {
                if (useTransaction) {
                    internal.endTransaction();

                    if (transactionSuccessful) {
                        final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table

                        for (final ContentValues contentValues : putResults.keySet()) {
                            affectedTables.addAll(putResults.get(contentValues).affectedTables());
                        }

                        // IMPORTANT: Notifying about change should be done after end of transaction
                        // It'll reduce number of possible deadlock situations
                        internal.notifyAboutChanges(Changes.newInstance(affectedTables));
                    }
                }
            }

            return PutResults.newInstance(putResults);

        } catch (Exception exception) {
            throw new StorIOException(exception);
        }
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<PutResults<ContentValues>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
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
         * @see DefaultPutResolver
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
         * <p>
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
