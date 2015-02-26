package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.MapFunc;
import com.pushtorefresh.android.bamboostorage.query.Query;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetWithResultsAsObjects<T> extends PreparedGet<List<T>> {

    @NonNull private final MapFunc<Cursor, T> mapFunc;

    PreparedGetWithResultsAsObjects(@NonNull BambooStorage bambooStorage, @NonNull Query query, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(bambooStorage, query);
        this.mapFunc = mapFunc;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull public List<T> executeAsBlocking() {
        final Cursor cursor = bambooStorage.getInternal().query(query);

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
}
