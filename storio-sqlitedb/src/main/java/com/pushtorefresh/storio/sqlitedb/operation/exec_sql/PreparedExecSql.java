package com.pushtorefresh.storio.sqlitedb.operation.exec_sql;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlitedb.query.RawQuery;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

public class PreparedExecSql implements PreparedOperation<Void> {

    @NonNull private final StorIOSQLiteDb storIOSQLiteDb;
    @NonNull private final RawQuery rawQuery;

    PreparedExecSql(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull RawQuery rawQuery) {
        this.storIOSQLiteDb = storIOSQLiteDb;
        this.rawQuery = rawQuery;
    }

    @NonNull @Override public Void executeAsBlocking() {
        storIOSQLiteDb.internal().execSql(rawQuery);
        return null;
    }

    @NonNull @Override public Observable<Void> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

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

        @NonNull private final StorIOSQLiteDb storIOSQLiteDb;

        private RawQuery rawQuery;

        public Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb) {
            this.storIOSQLiteDb = storIOSQLiteDb;
        }

        /**
         * Specifies query for ExecSql Operation
         *
         * @param rawQuery query
         * @return builder
         */
        @NonNull public Builder withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        /**
         * Prepares ExecSql Operation
         * @return {@link PreparedExecSql} instance
         */
        @NonNull public PreparedExecSql prepare() {
            if (rawQuery == null) {
                throw new IllegalStateException("Please set query object");
            }

            return new PreparedExecSql(
                    storIOSQLiteDb,
                    rawQuery
            );
        }
    }
}
