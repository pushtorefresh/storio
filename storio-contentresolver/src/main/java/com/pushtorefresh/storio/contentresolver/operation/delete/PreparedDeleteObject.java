package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull
    private final T object;

    @NonNull
    private final MapFunc<T, DeleteQuery> mapFunc;

    PreparedDeleteObject(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver deleteResolver, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(storIOContentResolver, deleteResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        final DeleteQuery deleteQuery = mapFunc.map(object);
        return deleteResolver.performDelete(storIOContentResolver, deleteQuery);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Delete Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override
            public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteObject}
     *
     * @param <T> type of object to delete
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final T object;

        private DeleteResolver deleteResolver;

        private MapFunc<T, DeleteQuery> mapFunc;

        /**
         * Creates builder for {@link PreparedDeleteObject}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         * @param object                non-null object that should be deleted
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(object, "Please specify object to delete");

            this.storIOContentResolver = storIOContentResolver;
            this.object = object;
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
         * Builds new instance of {@link PreparedDeleteObject}
         *
         * @return new instance of {@link PreparedDeleteObject}
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults((Class<T>) object.getClass());

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

            return new PreparedDeleteObject<T>(
                    storIOContentResolver,
                    deleteResolver,
                    object,
                    mapFunc
            );
        }
    }
}
