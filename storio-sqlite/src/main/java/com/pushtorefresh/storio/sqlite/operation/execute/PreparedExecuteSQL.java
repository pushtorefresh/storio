package com.pushtorefresh.storio.sqlite.operation.execute;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Execute SQL Operation for {@link StorIOSQLite}.
 */
public final class PreparedExecuteSQL implements PreparedOperation<Void> {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    @NonNull
    private final RawQuery rawQuery;

    PreparedExecuteSQL(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        this.storIOSQLite = storIOSQLite;
        this.rawQuery = rawQuery;
    }

    /**
     * Executes SQL Operation immediately in current thread.
     *
     * @return {@code null}, sorry guys.
     */
    @Nullable
    @Override
    public Void executeAsBlocking() {
        storIOSQLite.internal().executeSQL(rawQuery);
        return null;
    }

    /**
     * Creates {@link Observable} which will perform Execute SQL Operation
     * and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * execution of SQL only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Does not operate by default on a particular {@link rx.Scheduler}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<Void> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

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
     * Builder for {@link PreparedExecuteSQL}.
     */
    public static final class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        public Builder(@NonNull StorIOSQLite storIOSQLite) {
            this.storIOSQLite = storIOSQLite;
        }

        /**
         * Required: Specifies query for ExecSql Operation.
         *
         * @param rawQuery any SQL query that you want to execute, but please, be careful with it.
         *                 Don't forget that you can set affected tables to the {@link RawQuery},
         *                 so ExecSQL operation will send notification about changes in that tables.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull RawQuery rawQuery) {
            checkNotNull(rawQuery, "Please set query object");
            return new CompleteBuilder(storIOSQLite, rawQuery);
        }
    }

    /**
     * Compile-time safe part of {@link Builder}.
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
         * Prepares ExecSql Operation.
         *
         * @return {@link PreparedExecuteSQL} instance.
         */
        @NonNull
        public PreparedExecuteSQL prepare() {
            return new PreparedExecuteSQL(
                    storIOSQLite,
                    rawQuery
            );
        }
    }
}
