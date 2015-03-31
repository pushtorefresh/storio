package com.pushtorefresh.storio.sqlitedb.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.MapFunc;
import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutObject<T> extends PreparedPut<T, PutResult> {

    @NonNull
    private final T object;
    @NonNull
    private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutObject(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull PutResolver<T> putResolver,
                      @NonNull T object, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(storIOSQLiteDb, putResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull
    public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(storIOSQLiteDb, mapFunc.map(object));

        putResolver.afterPut(object, putResult);
        storIOSQLiteDb.internal().notifyAboutChanges(new Changes(putResult.affectedTable()));

        return putResult;
    }

    @NonNull
    public Observable<PutResult> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<PutResult>() {
            @Override
            public void call(Subscriber<? super PutResult> subscriber) {
                final PutResult putResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull
        private final StorIOSQLiteDb storIOSQLiteDb;
        @NonNull
        private final T object;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        Builder(@NonNull StorIOSQLiteDb storIOSQLiteDb, @NonNull T object) {
            this.storIOSQLiteDb = storIOSQLiteDb;
            this.object = object;
        }

        /**
         * Specifies map function for Put Operation which will be used to map object to {@link ContentValues}
         *
         * @param mapFunc map function for Put Operation which will be used to map object to {@link ContentValues}
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Specifies {@link PutResolver} for Put Operation which allows you to customize behavior of Put Operation
         *
         * @param putResolver put resolver
         * @return builder
         * @see {@link DefaultPutResolver} — easy way to create {@link PutResolver}
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutObject} instance
         */
        @NonNull
        public PreparedOperation<PutResult> prepare() {
            checkNotNull(mapFunc, "Please specify map function");

            return new PreparedPutObject<>(
                    storIOSQLiteDb,
                    putResolver,
                    object,
                    mapFunc
            );
        }
    }
}
