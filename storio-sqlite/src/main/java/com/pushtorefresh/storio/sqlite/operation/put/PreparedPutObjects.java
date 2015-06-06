package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

public final class PreparedPutObjects<T> extends PreparedPut<T, PutResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    private final boolean useTransaction;

    PreparedPutObjects(@NonNull StorIOSQLite storIOSQLite,
                       @NonNull Iterable<T> objects,
                       @NonNull PutResolver<T> putResolver,
                       boolean useTransaction) {
        super(storIOSQLite, putResolver);
        this.objects = objects;
        this.useTransaction = useTransaction;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public PutResults<T> executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();
        final Map<T, PutResult> putResults = new HashMap<T, PutResult>();

        if (useTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (T object : objects) {
                final PutResult putResult = putResolver.performPut(storIOSQLite, object);

                putResults.put(object, putResult);

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

                    for (final T object : putResults.keySet()) {
                        affectedTables.addAll(putResults.get(object).affectedTables());
                    }

                    storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(affectedTables));
                }
            }
        }

        return PutResults.newInstance(putResults);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p/>
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
    public Observable<PutResults<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedPutObjects}
     *
     * @param <T> type of objects to put
     */
    public static final class Builder<T> {

        @NonNull
        private final Class<T> type;

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Iterable<T> objects;

        private PutResolver<T> putResolver;

        private boolean useTransaction = true;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
            this.objects = objects;
        }

        /**
         * Optional: Specifies {@link PutResolver} for Put Operation
         * which allows you to customize behavior of Put Operation
         * <p/>
         * Can be set via {@link SQLiteTypeMapping}
         * If it's not set via {@link SQLiteTypeMapping} or explicitly -> exception will be thrown
         *
         * @param putResolver put resolver
         * @return builder
         * @see DefaultPutResolver
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Optional: Defines that Put Operation will use transaction if it is supported by implementation of {@link StorIOSQLite}
         * <p/>
         * By default, transaction will be used
         *
         * @return builder
         */
        @NonNull
        public Builder<T> useTransaction(boolean useTransaction) {
            this.useTransaction = useTransaction;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutObjects} instance
         */
        @NonNull
        public PreparedPutObjects<T> prepare() {
            final SQLiteTypeMapping<T> typeMapping = storIOSQLite.internal().typeMapping(type);

            if (putResolver == null && typeMapping != null) {
                putResolver = typeMapping.putResolver();
            }

            checkNotNull(putResolver, "Please specify PutResolver");

            return new PreparedPutObjects<T>(
                    storIOSQLite,
                    objects,
                    putResolver,
                    useTransaction
            );
        }
    }
}
