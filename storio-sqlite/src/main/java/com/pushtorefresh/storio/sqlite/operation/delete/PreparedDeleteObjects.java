package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.DeleteQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedDeleteObjects<T> extends PreparedDelete<DeleteResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    @NonNull
    private final MapFunc<T, DeleteQuery> mapFunc;

    private final boolean useTransactionIfPossible;

    PreparedDeleteObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull Iterable<T> objects, @NonNull MapFunc<T, DeleteQuery> mapFunc, boolean useTransactionIfPossible, @NonNull DeleteResolver deleteResolver) {
        super(storIOSQLite, deleteResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null results of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResults<T> executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();

        final Map<T, DeleteResult> results = new HashMap<T, DeleteResult>();

        final boolean withTransaction = useTransactionIfPossible && internal.transactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (final T object : objects) {
                final DeleteQuery deleteQuery = mapFunc.map(object);
                final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, deleteQuery);

                results.put(
                        object,
                        deleteResult
                );

                if (!withTransaction) {
                    internal.notifyAboutChanges(Changes.newInstance(deleteQuery.table));
                }
            }

            if (withTransaction) {
                internal.setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                internal.endTransaction();

                if (transactionSuccessful) {
                    // if delete was in transaction and it was successful -> notify about changes

                    final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be one table

                    for (final T object : results.keySet()) {
                        affectedTables.add(results.get(object).affectedTable());
                    }

                    internal.notifyAboutChanges(Changes.newInstance(affectedTables));
                }
            }
        }

        return DeleteResults.newInstance(results);
    }

    /**
     * Creates an {@link Observable} which will emit results of Delete Operation
     *
     * @return non-null {@link Observable} which will emit non-null results of Delete Operation
     */
    @NonNull
    @Override
    public Observable<DeleteResults<T>> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<DeleteResults<T>>() {
            @Override
            public void call(Subscriber<? super DeleteResults<T>> subscriber) {
                final DeleteResults<T> deleteResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteObjects}
     *
     * @param <T> type of objects to delete
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Iterable<T> objects;

        private MapFunc<T, DeleteQuery> mapFunc;
        private boolean useTransactionIfPossible = true;
        private DeleteResolver deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
            this.objects = objects;
        }

        /**
         * Optional: Specifies map function to map each object to {@link DeleteQuery}
         * <p/>
         * Can be set via {@link SQLiteTypeDefaults},
         * If value is not set via {@link SQLiteTypeDefaults} or explicitly, exception will be thrown
         *
         * @param mapFunc map function to map each object to {@link DeleteQuery}
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@Nullable MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Optional: Defines that Delete Operation will use transaction
         * if it is supported by implementation of {@link StorIOSQLite}
         * <p/>
         * By default, transaction will be used
         *
         * @param useTransaction true to use transaction, false to not
         * @return builder
         */
        @NonNull
        public Builder<T> useTransaction(boolean useTransaction) {
            useTransactionIfPossible = useTransaction;
            return this;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation
         * <p/>
         * <p/>
         * Can be set via {@link SQLiteTypeDefaults},
         * If value is not set via {@link SQLiteTypeDefaults} or explicitly, instance of {@link DefaultDeleteResolver} will be used
         *
         * @param deleteResolver {@link DeleteResolver} for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@Nullable DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation
         *
         * @return {@link PreparedDeleteObjects}
         */
        @NonNull
        public PreparedDeleteObjects<T> prepare() {
            final SQLiteTypeDefaults<T> typeDefaults = storIOSQLite.internal().typeDefaults(type);

            if (mapFunc == null && typeDefaults != null) {
                mapFunc = typeDefaults.mapToDeleteQuery;
            }

            if (deleteResolver == null) {
                if (typeDefaults != null && typeDefaults.deleteResolver != null) {
                    deleteResolver = typeDefaults.deleteResolver;
                } else {
                    deleteResolver = DefaultDeleteResolver.INSTANCE;
                }
            }

            checkNotNull(mapFunc, "Please specify map function");

            return new PreparedDeleteObjects<T>(
                    storIOSQLite,
                    objects,
                    mapFunc,
                    useTransactionIfPossible,
                    deleteResolver
            );
        }
    }
}
