package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
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
     *
     * @param <T> type of objects
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Iterable<T> objects;

        private DeleteResolver deleteResolver;

        private MapFunc<T, DeleteQuery> mapFunc;

        /**
         * Creates builder for {@link PreparedDeleteObjects}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         * @param type                  type of objects
         * @param objects               non-null collection of objects to delete
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.objects = objects;
        }

        /**
         * Optional: Specifies resolver for Delete Operation
         * Allows you to customise behavior of Delete Operation
         * <p/>
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set via {@link ContentResolverTypeDefaults} or explicitly instance of {@link DefaultDeleteResolver} will be used
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
         * Optional: Specifies map function that should map each object to {@link DeleteQuery}
         * <p/>
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set view {@link ContentResolverTypeDefaults} or explicitly, exception will be thrown
         *
         * @param mapFunc map function
         * @return builder
         */
        @NonNull
        public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        /**
         * Builds instance of {@link PreparedDeleteObjects}
         *
         * @return instance of {@link PreparedDeleteObjects}
         */
        @NonNull
        public PreparedDeleteObjects<T> prepare() {
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults(type);

            if (mapFunc == null && typeDefaults != null) {
                mapFunc = typeDefaults.mapToDeleteQuery;
            }

            checkNotNull(mapFunc, "Please specify map function");

            if (deleteResolver == null) {
                if (typeDefaults != null && typeDefaults.deleteResolver != null) {
                    deleteResolver = typeDefaults.deleteResolver;
                } else {
                    deleteResolver = DefaultDeleteResolver.INSTANCE;
                }
            }

            checkNotNull(deleteResolver, "Please specify Delete Resolver");

            return new PreparedDeleteObjects<T>(
                    storIOContentResolver,
                    deleteResolver,
                    objects,
                    mapFunc
            );
        }
    }
}
