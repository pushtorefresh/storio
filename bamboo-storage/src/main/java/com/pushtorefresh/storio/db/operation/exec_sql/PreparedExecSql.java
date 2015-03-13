package com.pushtorefresh.storio.db.operation.exec_sql;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.PreparedOperation;
import com.pushtorefresh.storio.db.query.RawQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedExecSql implements PreparedOperation<Void> {

    @NonNull private final BambooStorageDb bambooStorageDb;
    @NonNull private final RawQuery rawQuery;

    public PreparedExecSql(@NonNull BambooStorageDb bambooStorageDb, @NonNull RawQuery rawQuery) {
        this.bambooStorageDb = bambooStorageDb;
        this.rawQuery = rawQuery;
    }

    @NonNull @Override public Void executeAsBlocking() {
        bambooStorageDb.internal().execSql(rawQuery);
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

        @NonNull private final BambooStorageDb bambooStorageDb;

        private RawQuery rawQuery;

        public Builder(@NonNull BambooStorageDb bambooStorageDb) {
            this.bambooStorageDb = bambooStorageDb;
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
                    bambooStorageDb,
                    rawQuery
            );
        }
    }
}
