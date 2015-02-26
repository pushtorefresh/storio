package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.query.Query;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetWithResultAsCursor extends PreparedGet<Cursor> {

    public PreparedGetWithResultAsCursor(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        super(bambooStorage, query);
    }

    @NonNull public Cursor executeAsBlocking() {
        return bambooStorage.getInternal().query(query);
    }

    @NonNull @Override public Observable<Cursor> createObservable() {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override public void call(Subscriber<? super Cursor> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }
}
