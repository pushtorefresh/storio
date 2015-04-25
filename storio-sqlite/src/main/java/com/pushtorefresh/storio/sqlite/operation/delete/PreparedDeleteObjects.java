package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
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
    private final DeleteResolver<T> deleteResolver;

    private final boolean useTransaction;

    PreparedDeleteObjects(@NonNull StorIOSQLite storIOSQLite, @NonNull Iterable<T> objects, @NonNull DeleteResolver<T> deleteResolver, boolean useTransaction) {
        super(storIOSQLite);
        this.objects = objects;
        this.deleteResolver = deleteResolver;
        this.useTransaction = useTransaction;
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

        final boolean withTransaction = useTransaction && internal.transactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (final T object : objects) {
                final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);

                results.put(
                        object,
                        deleteResult
                );

                if (!withTransaction) {
                    internal.notifyAboutChanges(Changes.newInstance(deleteResult.affectedTables()));
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
                        affectedTables.addAll(results.get(object).affectedTables());
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
    public static final class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Iterable<T> objects;

        private DeleteResolver<T> deleteResolver;

        private boolean useTransaction = true;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOSQLite = storIOSQLite;
            this.type = type;
            this.objects = objects;
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
            this.useTransaction = useTransaction;
            return this;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation
         * <p/>
         * <p/>
         * Can be set via {@link SQLiteTypeDefaults},
         * If value is not set via {@link SQLiteTypeDefaults} or explicitly -> exception will be thrown
         *
         * @param deleteResolver {@link DeleteResolver} for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@Nullable DeleteResolver<T> deleteResolver) {
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

            if (deleteResolver == null && typeDefaults != null) {
                deleteResolver = typeDefaults.deleteResolver;
            }

            checkNotNull(deleteResolver, "Please specify DeleteResolver");

            return new PreparedDeleteObjects<T>(
                    storIOSQLite,
                    objects,
                    deleteResolver,
                    useTransaction
            );
        }
    }
}
