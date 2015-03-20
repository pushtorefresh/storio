package com.pushtorefresh.storio.db.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.operation.PreparedOperation;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithObject<T> extends PreparedPut<T, PutResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutWithObject(@NonNull StorIODb storIODb, @NonNull PutResolver<T> putResolver,
                          @NonNull T object, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(storIODb, putResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(
                storIODb,
                mapFunc.map(object)
        );

        putResolver.afterPut(object, putResult);
        storIODb.internal().notifyAboutChanges(new Changes(putResult.affectedTables()));

        return putResult;
    }

    @NonNull public Observable<PutResult> createObservable() {
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

        @NonNull private final StorIODb storIODb;
        @NonNull private final T object;

        private MapFunc<T, ContentValues> mapFunc;
        private PutResolver<T> putResolver;

        Builder(@NonNull StorIODb storIODb, @NonNull T object) {
            this.storIODb = storIODb;
            this.object = object;
        }

        /**
         * Specifies map function for Put Operation which will be used to map object to {@link ContentValues}
         *
         * @param mapFunc map function for Put Operation which will be used to map object to {@link ContentValues}
         * @return builder
         */
        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
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
        @NonNull public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Prepares Put Operation
         * @return {@link PreparedPutWithObject} instance
         */
        @NonNull public PreparedOperation<PutResult> prepare() {
            if (mapFunc == null) {
                throw new IllegalStateException("Please specify map function");
            }

            return new PreparedPutWithObject<>(
                    storIODb,
                    putResolver,
                    object,
                    mapFunc
            );
        }
    }
}
