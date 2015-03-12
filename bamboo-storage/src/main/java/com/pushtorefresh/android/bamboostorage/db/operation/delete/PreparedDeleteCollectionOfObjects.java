package com.pushtorefresh.android.bamboostorage.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorageDb;
import com.pushtorefresh.android.bamboostorage.db.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.db.query.DeleteQuery;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteCollectionOfObjects<T> extends PreparedDelete<DeleteCollectionOfObjectsResult<T>> {

    @NonNull private final Collection<T> objects;
    @NonNull private final MapFunc<T, DeleteQuery> mapFunc;
    private final boolean useTransactionIfPossible;

    protected PreparedDeleteCollectionOfObjects(@NonNull BambooStorageDb bambooStorageDb, @NonNull Collection<T> objects, @NonNull MapFunc<T, DeleteQuery> mapFunc, boolean useTransactionIfPossible) {
        super(bambooStorageDb);
        this.objects = objects;
        this.mapFunc = mapFunc;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    @NonNull @Override public DeleteCollectionOfObjectsResult<T> executeAsBlocking() {
        final BambooStorageDb.Internal internal = bambooStorageDb.internal();

        final Map<T, DeleteResult> results = new HashMap<>();

        final boolean withTransaction = useTransactionIfPossible && internal.areTransactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (final T object : objects) {
                final DeleteQuery deleteQuery = mapFunc.map(object);
                final int countOfDeletedRows = internal.delete(deleteQuery);

                results.put(
                        object,
                        DeleteResult.newDeleteResult(
                                countOfDeletedRows,
                                Collections.singleton(deleteQuery.table))
                );

                if (!withTransaction) {
                    internal.notifyAboutChanges(Collections.singleton(deleteQuery.table));
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

                    final Set<String> affectedTables = new HashSet<>(1); // in most cases it will be one table

                    for (final T object : results.keySet()) {
                        affectedTables.addAll(results.get(object).affectedTables());
                    }

                    internal.notifyAboutChanges(affectedTables);
                }
            }
        }

        return new DeleteCollectionOfObjectsResult<>(results);
    }

    @NonNull @Override public Observable<DeleteCollectionOfObjectsResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteCollectionOfObjectsResult<T>>() {
            @Override
            public void call(Subscriber<? super DeleteCollectionOfObjectsResult<T>> subscriber) {
                DeleteCollectionOfObjectsResult<T> deleteCollectionOfObjectsResult
                        = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteCollectionOfObjectsResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorageDb bambooStorageDb;
        @NonNull private final Collection<T> objects;

        private MapFunc<T, DeleteQuery> mapFunc;
        private boolean useTransactionIfPossible = true;

        public Builder(@NonNull BambooStorageDb bambooStorageDb, @NonNull Collection<T> objects) {
            this.bambooStorageDb = bambooStorageDb;
            this.objects = objects;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> useTransactionIfPossible() {
            useTransactionIfPossible = true;
            return this;
        }

        @NonNull public Builder<T> dontUseTransaction() {
            useTransactionIfPossible = false;
            return this;
        }

        @NonNull public PreparedDeleteCollectionOfObjects<T> prepare() {
            return new PreparedDeleteCollectionOfObjects<>(
                    bambooStorageDb,
                    objects,
                    mapFunc,
                    useTransactionIfPossible
            );
        }
    }
}
