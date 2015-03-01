package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.Query;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetAsListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull private final MapFunc<Cursor, T> mapFunc;

    PreparedGetAsListOfObjects(@NonNull BambooStorage bambooStorage, @NonNull Query query, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(bambooStorage, query);
        this.mapFunc = mapFunc;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull public List<T> executeAsBlocking() {
        final Cursor cursor = bambooStorage.internal().query(query);

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

    public static class Builder<T> {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Class<T> type; // currently not used

        private MapFunc<Cursor, T> mapFunc;
        private Query query;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type) {
            this.bambooStorage = bambooStorage;
            this.type = type;
        }

        @NonNull public Builder<T> mapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> query(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public PreparedOperation<List<T>> prepare() {
            return new PreparedGetAsListOfObjects<>(bambooStorage, query, mapFunc);
        }
    }
}
