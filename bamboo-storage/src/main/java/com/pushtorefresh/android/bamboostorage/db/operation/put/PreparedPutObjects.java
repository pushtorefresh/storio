package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.db.BambooStorage;
import com.pushtorefresh.android.bamboostorage.db.operation.MapFunc;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutObjects<T> extends PreparedPut<T, PutCollectionResult<T>> {

    @NonNull private final Iterable<T> objects;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;
    private final boolean useTransactionIfPossible;

    public PreparedPutObjects(@NonNull BambooStorage bambooStorage,
                              @NonNull PutResolver<T> putResolver,
                              @NonNull Iterable<T> objects, @NonNull MapFunc<T, ContentValues> mapFunc,
                              boolean useTransactionIfPossible) {
        super(bambooStorage, putResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
        this.useTransactionIfPossible = useTransactionIfPossible;
    }

    @NonNull @Override public PutCollectionResult<T> executeAsBlocking() {
        final BambooStorage.Internal internal = bambooStorage.internal();
        final Map<T, PutResult> putResults = new HashMap<>();

        final boolean withTransaction = useTransactionIfPossible
                && bambooStorage.internal().areTransactionsSupported();

        if (withTransaction) {
            internal.beginTransaction();
        }

        boolean transactionSuccessful = false;

        try {
            for (T object : objects) {
                final PutResult putResult = putResolver.performPut(
                        bambooStorage,
                        mapFunc.map(object)
                );

                putResolver.afterPut(object, putResult);
                putResults.put(object, putResult);

                if (!withTransaction) {
                    internal.notifyAboutChanges(putResult.affectedTables());
                }
            }

            if (withTransaction) {
                bambooStorage.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (withTransaction) {
                bambooStorage.internal().endTransaction();

                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<>(1); // in most cases it will be 1 table

                    for (final T object : putResults.keySet()) {
                        affectedTables.addAll(putResults.get(object).affectedTables());
                    }

                    bambooStorage.internal().notifyAboutChanges(affectedTables);
                }
            }
        }

        return new PutCollectionResult<>(putResults);
    }

    @NonNull @Override public Observable<PutCollectionResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionResult<T>>() {
            @Override
            public void call(Subscriber<? super PutCollectionResult<T>> subscriber) {
                PutCollectionResult<T> putResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Iterable<T> objects;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;
        private boolean useTransactionIfPossible = true;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Iterable<T> objects) {
            this.bambooStorage = bambooStorage;
            this.objects = objects;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
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

        @NonNull public PreparedPutObjects<T> prepare() {
            return new PreparedPutObjects<>(
                    bambooStorage,
                    putResolver,
                    objects,
                    mapFunc,
                    useTransactionIfPossible);
        }
    }
}
