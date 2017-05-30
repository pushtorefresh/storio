package com.pushtorefresh.storio2.sqlite.operations.put;

import android.content.ContentValues;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.internal.RxJavaUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;

public class PreparedPutContentValuesIterable extends PreparedPut<PutResults<ContentValues>, Iterable<ContentValues>> {

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
     * Creates {@link Flowable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<PutResults<ContentValues>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOSQLite, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Put Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Put Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<PutResults<ContentValues>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    /**
     * Creates {@link Completable} which will perform Put Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Put Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
    }


    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    @NonNull
    @Override
    public Iterable<ContentValues> getData() {
        return contentValuesIterable;
    }

    private class RealCallInterceptor implements Interceptor {
        @NonNull
        @Override
        public <Result, Data> Result intercept(@NonNull PreparedOperation<Result, Data> operation, @NonNull Chain chain) {
            try {
                final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

                final Map<ContentValues, PutResult> putResults = new HashMap<ContentValues, PutResult>();

                if (useTransaction) {
                    lowLevel.beginTransaction();
                }

                boolean transactionSuccessful = false;

                try {
                    for (ContentValues contentValues : contentValuesIterable) {
                        final PutResult putResult = putResolver.performPut(storIOSQLite, contentValues);
                        putResults.put(contentValues, putResult);

                        if (!useTransaction && (putResult.wasInserted() || putResult.wasUpdated())) {
                            final Changes changes = Changes.newInstance(
                                    putResult.affectedTables(),
                                    putResult.affectedTags()
                            );
                            lowLevel.notifyAboutChanges(changes);
                        }
                    }

                    if (useTransaction) {
                        lowLevel.setTransactionSuccessful();
                        transactionSuccessful = true;
                    }
                } finally {
                    if (useTransaction) {
                        lowLevel.endTransaction();

                        if (transactionSuccessful) {
                            final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table
                            final Set<String> affectedTags = new HashSet<String>(1);

                            for (final ContentValues contentValues : putResults.keySet()) {
                                final PutResult putResult = putResults.get(contentValues);
                                if (putResult.wasInserted() || putResult.wasUpdated()) {
                                    affectedTables.addAll(putResult.affectedTables());
                                    affectedTags.addAll(putResult.affectedTags());
                                }
                            }

                            // IMPORTANT: Notifying about change should be done after end of transaction
                            // It'll reduce number of possible deadlock situations
                            if (!affectedTables.isEmpty() || !affectedTags.isEmpty()) {
                                lowLevel.notifyAboutChanges(Changes.newInstance(affectedTables, affectedTags));
                            }
                        }
                    }
                }

                return (Result) PutResults.newInstance(putResults);

            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Put operation. contentValues = " + contentValuesIterable, exception);
            }
        }
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}
     */
    public static class Builder {

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
    public static class CompleteBuilder {

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
