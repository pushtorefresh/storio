package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Delete Operation of some collection of objects for {@link StorIOContentResolver}
 *
 * @param <T> type of objects
 */
public class PreparedDeleteObjects<T> extends PreparedDelete<DeleteResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    @NonNull
    private final MapFunc<T, DeleteQuery> mapFunc;

    PreparedDeleteObjects(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver deleteResolver, @NonNull Iterable<T> objects, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(storIOContentResolver, deleteResolver);
        this.objects = objects;
        this.mapFunc = mapFunc;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null results of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResults<T> executeAsBlocking() {
        final Map<T, DeleteResult> deleteResultsMap = new HashMap<T, DeleteResult>();

        for (final T object : objects) {
            final DeleteQuery deleteQuery = mapFunc.map(object);
            final DeleteResult deleteResult = deleteResolver.performDelete(storIOContentResolver, deleteQuery);

            deleteResultsMap.put(object, deleteResult);
        }

        return DeleteResults.newInstance(deleteResultsMap);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send results to observer
     *
     * @return non-null {@link Observable} which will perform Delete Operation and send results to observer
     */
    @NonNull
    @Override
    public Observable<DeleteResults<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResults<T>>() {
            @Override
            public void call(Subscriber<? super DeleteResults<T>> subscriber) {
                final DeleteResults<T> deleteResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteObjects}
     * <p>
     * Required: You should specify map function see {@link #withMapFunc(MapFunc)}
     *
     * @param <T> type of objects
     */
    public static class Builder<T> {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        @NonNull
        final Iterable<T> objects;

        DeleteResolver deleteResolver;

        MapFunc<T, DeleteQuery> mapFunc;

        /**
         * Creates builder for {@link PreparedDeleteObjects}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         * @param objects               non-null collection of objects to delete
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Iterable<T> objects) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(objects, "Please specify objects to delete");

            this.storIOContentResolver = storIOContentResolver;
            this.objects = objects;
        }

        /**
         * Optional: Specifies resolver for Delete Operation
         * Allows you to customise behavior of Delete Operation
         * <p>
         * Default value is instance of {@link DefaultDeleteResolver}
         *
         * @param deleteResolver resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Required: Specifies map function that should map each object to {@link DeleteQuery}
         *
         * @param mapFunc map function
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return new CompleteBuilder<T>(this);
        }
    }

    /**
     * Builder for {@link PreparedDeleteObjects}
     *
     * @param <T> type of objects
     */
    public static class CompleteBuilder<T> extends Builder<T> {

        CompleteBuilder(@NonNull final Builder<T> builder) {
            super(builder.storIOContentResolver, builder.objects);
            deleteResolver = builder.deleteResolver;
            mapFunc = builder.mapFunc;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder<T> withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            super.withDeleteResolver(deleteResolver);
            return this;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            super.withMapFunc(mapFunc);
            return this;
        }

        /**
         * Builds instance of {@link PreparedDeleteObjects}
         *
         * @return instance of {@link PreparedDeleteObjects}
         */
        @NonNull
        public PreparedDeleteObjects<T> prepare() {
            checkNotNull(mapFunc, "Please specify map function");

            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteObjects<T>(
                    storIOContentResolver,
                    deleteResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
