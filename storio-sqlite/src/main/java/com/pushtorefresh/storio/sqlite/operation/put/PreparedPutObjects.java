package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.internal.Environment;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

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
     * Executes Put Operation immediately in current thread
     *
     * @return non-null results of Put Operation
     */
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
     * Creates {@link Observable} which will perform Put Operation and send results to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send results to observer
     */
    @NonNull
    @Override
    public Observable<PutResults<T>> createObservable() {
        Environment.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
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
         * Can be set via {@link SQLiteTypeDefaults}
         * If it's not set via {@link SQLiteTypeDefaults} or explicitly -> exception will be thrown
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
            final SQLiteTypeDefaults<T> typeDefaults = storIOSQLite.internal().typeDefaults(type);

            if (putResolver == null && typeDefaults != null) {
                putResolver = typeDefaults.putResolver;
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
