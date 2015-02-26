package com.pushtorefresh.android.bamboostorage.operation;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.query.Query;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetWithResultsAsObjects<R> extends PreparedGet {

    @NonNull private final MapFunc<Cursor, R> mapFunc;

    PreparedGetWithResultsAsObjects(@NonNull BambooStorage bambooStorage, @NonNull Query query, @NonNull MapFunc<Cursor, R> mapFunc) {
        super(bambooStorage, query);
        this.mapFunc = mapFunc;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @Nullable public List<R> executeAsBlocking() {
        final Cursor cursor = bambooStorage.getInternal().query(query);

        try {
            final List<R> list = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                list.add(mapFunc.map(cursor));
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    @NonNull public Observable<List<R>> executeAsObservable() {
        return Observable.create(new Observable.OnSubscribe<List<R>>() {
            @Override public void call(Subscriber<? super List<R>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }
}
