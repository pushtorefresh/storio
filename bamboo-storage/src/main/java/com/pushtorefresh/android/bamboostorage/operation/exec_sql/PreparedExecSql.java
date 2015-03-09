package com.pushtorefresh.android.bamboostorage.operation.exec_sql;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.PreparedOperation;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedExecSql implements PreparedOperation<Void> {

    @NonNull private final BambooStorage bambooStorage;
    @NonNull private final RawQuery rawQuery;

    public PreparedExecSql(@NonNull BambooStorage bambooStorage, @NonNull RawQuery rawQuery) {
        this.bambooStorage = bambooStorage;
        this.rawQuery = rawQuery;
    }

    @NonNull @Override public Void executeAsBlocking() {
        bambooStorage.internal().execSql(rawQuery);
        return null;
    }

    @NonNull @Override public Observable<Void> createObservable() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override public void call(Subscriber<? super Void> subscriber) {
                executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;

        private RawQuery rawQuery;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public Builder withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        @NonNull public PreparedExecSql prepare() {
            if (rawQuery == null) {
                throw new IllegalStateException("Please set query object");
            }

            return new PreparedExecSql(
                    bambooStorage,
                    rawQuery
            );
        }
    }
}
