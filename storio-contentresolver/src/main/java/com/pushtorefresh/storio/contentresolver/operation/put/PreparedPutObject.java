package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.MapFunc;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutObject<T> extends PreparedPut<T, PutResult> {

    @NonNull
    private final T object;

    @NonNull
    private final MapFunc<T, ContentValues> mapFunc;

    protected PreparedPutObject(@NonNull StorIOContentResolver storIOContentResolver,
                                @NonNull PutResolver<T> putResolver,
                                @NonNull T object, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(storIOContentResolver, putResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @Nullable
    @Override
    public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(storIOContentResolver, mapFunc.map(object));

        putResolver.afterPut(object, putResult);
        // TODO notify about changes

        return putResult;
    }

    @NonNull
    @Override
    public Observable<PutResult> createObservable() {
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

    /**
     * Builder for {@link PreparedPutObject}
     *
     * @param <T> type of object to put
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final T object;

        private MapFunc<T, ContentValues> mapFunc;

        private PutResolver<T> putResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object) {
            this.storIOContentResolver = storIOContentResolver;
            this.object = object;
        }

        /**
         * Required: Specifies map function that should map object to {@link ContentValues}
         *
         * @param mapFunc map function
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@NonNull MapFunc<T, ContentValues> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Required: Specifies resolver for Put Operation
         * that should define behavior of Put Operation: insert or update of the {@link ContentValues}
         *
         * @param putResolver resolver for Put Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedPutObject}
         *
         * @return instance of {@link PreparedPutObject}
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedPutObject<T> prepare() {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(object, "Please specify object for Put Operation");
            checkNotNull(mapFunc, "Please specify map function");
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutObject<>(
                    storIOContentResolver,
                    putResolver,
                    object,
                    mapFunc
            );
        }
    }
}
