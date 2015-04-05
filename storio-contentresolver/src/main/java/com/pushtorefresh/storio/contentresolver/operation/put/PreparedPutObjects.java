package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.MapFunc;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Put Operation for collection of objects
 *
 * @param <T> type of objects
 */
public class PreparedPutObjects<T> extends PreparedPut<T, PutResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    @NonNull
    private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutObjects(@NonNull StorIOContentResolver storIOContentResolver, @NonNull PutResolver<T> putResolver, @NonNull Iterable<T> objects, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(storIOContentResolver, putResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null result of Put Operation
     */
    @NonNull
    @Override
    public PutResults<T> executeAsBlocking() {

        final Map<T, PutResult> putResults = new HashMap<>();

        for (T object : objects) {
            final PutResult putResult = putResolver.performPut(storIOContentResolver, mapFunc.map(object));
            putResolver.afterPut(object, putResult);

            putResults.put(object, putResult);
        }

        return PutResults.newInstance(putResults);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<PutResults<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutResults<T>>() {
            @Override
            public void call(Subscriber<? super PutResults<T>> subscriber) {
                final PutResults<T> putResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedPutObjects}
     *
     * @param <T> type of objects to put
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Iterable<T> objects;

        private MapFunc<T, ContentValues> mapFunc;

        private PutResolver<T> putResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Iterable<T> objects) {
            this.storIOContentResolver = storIOContentResolver;
            this.objects = objects;
        }

        /**
         * Required: Specifies map function that should map each object to {@link ContentValues}
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
         * Builds instance of {@link PreparedPutObjects}
         *
         * @return instance of {@link PreparedPutObjects}
         */
        @NonNull
        public PreparedPutObjects<T> prepare() {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(objects, "Please specify objects");
            checkNotNull(mapFunc, "Please specify map function");
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutObjects<>(
                    storIOContentResolver,
                    putResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
