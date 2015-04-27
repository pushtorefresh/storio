package com.pushtorefresh.storio.sqlite.operation.exec_sql;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.RawQuery;
import com.pushtorefresh.storio.internal.Environment;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

public final class PreparedExecSql implements PreparedOperation<Void> {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    @NonNull
    private final RawQuery rawQuery;

    PreparedExecSql(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
    }

    @NonNull
    @Override
    public Void executeAsBlocking() {
        storIOSQLite.internal().execSql(rawQuery);
        return null;
    }

    @NonNull
    @Override
    public Observable<Void> createObservable() {
        Environment.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedExecSql}
     */
    public static final class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Required: Specifies query for ExecSql Operation
         *
         * @param rawQuery query
         * @return builder
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please set query object");
            return new CompleteBuilder(storIOSQLite, rawQuery);
        }
    }

    /**
     * Compile-time safe part of {@link Builder}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final RawQuery rawQuery;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
            this.storIOSQLite = storIOSQLite;
            this.rawQuery = rawQuery;
        }

        /**
         * Prepares ExecSql Operation
         *
         * @return {@link PreparedExecSql} instance
         */
        @NonNull
        public PreparedExecSql prepare() {
            return new PreparedExecSql(
                    storIOSQLite,
                    rawQuery
            );
        }
    }
}
