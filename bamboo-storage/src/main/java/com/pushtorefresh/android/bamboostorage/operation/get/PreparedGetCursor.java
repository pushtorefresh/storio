package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.Query;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    public PreparedGetCursor(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        super(bambooStorage, query);
    }

    @NonNull public Cursor executeAsBlocking() {
        return bambooStorage.internal().query(query);
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

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        private Query query;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public Builder query(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public PreparedOperation<Cursor> prepare() {
            return new PreparedGetCursor(bambooStorage, query);
        }
    }
}
