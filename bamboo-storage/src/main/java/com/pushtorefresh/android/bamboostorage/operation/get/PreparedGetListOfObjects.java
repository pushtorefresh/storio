package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull private final MapFunc<Cursor, T> mapFunc;

    PreparedGetListOfObjects(@NonNull BambooStorage bambooStorage, @NonNull Query query, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(bambooStorage, query);
        this.mapFunc = mapFunc;
    }

    PreparedGetListOfObjects(@NonNull BambooStorage bambooStorage, @NonNull RawQuery rawQuery, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(bambooStorage, rawQuery);
        this.mapFunc = mapFunc;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull public List<T> executeAsBlocking() {
        final Cursor cursor;

        if (query != null) {
            cursor = bambooStorage.internal().query(query);
        } else if (rawQuery != null) {
            cursor = bambooStorage.internal().rawQuery(rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }

        try {
            final List<T> list = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                list.add(mapFunc.map(cursor));
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    @NonNull public Observable<List<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull @Override public Observable<List<T>> createObservableStream() {
        final Set<String> tables;

        if (query != null) {
            tables = new HashSet<>(1);
            tables.add(query.table);
        } else if (rawQuery != null) {
            tables = rawQuery.tables;
        } else {
            throw new IllegalStateException("Please specify query");
        }

        if (tables != null && !tables.isEmpty()) {
            return bambooStorage
                    .subscribeOnChanges(tables)
                    .map(new Func1<Set<String>, List<T>>() {
                        @Override public List<T> call(Set<String> affectedTables) {
                            return executeAsBlocking();
                        }
                    })
                    .startWith(executeAsBlocking());
        } else {
            return createObservable();
        }
    }

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull
        private final Class<T> type; // currently type not used as object, only for generics

        private MapFunc<Cursor, T> mapFunc;
        private Query query;
        private RawQuery rawQuery;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type) {
            this.bambooStorage = bambooStorage;
            this.type = type;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder<T> withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        @NonNull public PreparedOperationWithReactiveStream<List<T>> prepare() {
            if (query != null) {
                return new PreparedGetListOfObjects<>(bambooStorage, query, mapFunc);
            } else if (rawQuery != null) {
                return new PreparedGetListOfObjects<>(bambooStorage, rawQuery, mapFunc);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
