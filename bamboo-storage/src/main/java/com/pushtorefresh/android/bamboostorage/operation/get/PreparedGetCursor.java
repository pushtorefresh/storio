package com.pushtorefresh.android.bamboostorage.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    PreparedGetCursor(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        super(bambooStorage, query);
    }

    PreparedGetCursor(@NonNull BambooStorage bambooStorage, @NonNull RawQuery rawQuery) {
        super(bambooStorage, rawQuery);
    }

    @NonNull public Cursor executeAsBlocking() {
        if (query != null) {
            return bambooStorage.internal().query(query);
        } else if (rawQuery != null) {
            return bambooStorage.internal().rawQuery(rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }
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
        private RawQuery rawQuery;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public Builder withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        @NonNull public PreparedOperation<Cursor> prepare() {
            if (query != null) {
                return new PreparedGetCursor(bambooStorage, query);
            } else if (rawQuery != null) {
                return new PreparedGetCursor(bambooStorage, rawQuery);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
