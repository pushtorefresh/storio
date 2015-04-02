package com.pushtorefresh.storio.contentprovider.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentprovider.StorIOContentResolver;
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
public class PreparedPutObjects<T> extends PreparedPut<T, PutCollectionResult<T>> {

    @NonNull
    private final Iterable<T> objects;

    @NonNull
    private final MapFunc<T, ContentValues> mapFunc;

    PreparedPutObjects(@NonNull StorIOContentResolver storIOContentProvider, @NonNull PutResolver<T> putResolver, @NonNull Iterable<T> objects, @NonNull MapFunc<T, ContentValues> mapFunc) {
        super(storIOContentProvider, putResolver);
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
    public PutCollectionResult<T> executeAsBlocking() {

        final Map<T, PutResult> putResults = new HashMap<>();

        for (T object : objects) {
            final PutResult putResult = putResolver.performPut(storIOContentProvider, mapFunc.map(object));
            putResolver.afterPut(object, putResult);

            putResults.put(object, putResult);
        }

        return PutCollectionResult.newInstance(putResults);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<PutCollectionResult<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutCollectionResult<T>>() {
            @Override
            public void call(Subscriber<? super PutCollectionResult<T>> subscriber) {
                PutCollectionResult<T> putCollectionResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putCollectionResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedPutObjects}
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentProvider;

        @NonNull
        private final Iterable<T> objects;

        private MapFunc<T, ContentValues> mapFunc;

        private PutResolver<T> putResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentProvider, @NonNull Iterable<T> objects) {
            this.storIOContentProvider = storIOContentProvider;
            this.objects = objects;
        }

        /**
         * Required: Specifies map function that should map each object to {@link ContentValues}
         * <p/>
         * Default value is <code>null</code>
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
         * <p/>
         * Default value is <code>null</code>
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
            checkNotNull(storIOContentProvider, "Please specify StorIOContentProvider");
            checkNotNull(objects, "Please specify objects");
            checkNotNull(mapFunc, "Please specify map function");
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutObjects<>(
                    storIOContentProvider,
                    putResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
