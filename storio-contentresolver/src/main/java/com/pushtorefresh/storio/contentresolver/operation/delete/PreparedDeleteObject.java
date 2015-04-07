package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

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
     * <p>
     * Required: You should specify map function see {@link #withMapFunc(MapFunc)}
     *
     * @param <T> type of object to delete
     */
    public static class Builder<T> {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        @NonNull
        final T object;

        DeleteResolver deleteResolver;

        MapFunc<T, DeleteQuery> mapFunc;

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
         * <p>
         * Default value is <code>null</code>
         *
         * @param mapFunc map function
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return new CompleteBuilder<>(this);
        }
    }

    /**
     * Builder for {@link PreparedDeleteObject}
     *
     * @param <T> type of object to delete
     */
    public static class CompleteBuilder<T> extends Builder<T> {

        CompleteBuilder(@NonNull final Builder<T> builder) {
            super(builder.storIOContentResolver, builder.object);
            deleteResolver = builder.deleteResolver;
            mapFunc = builder.mapFunc;
        }

        /**
         * Builds instance of {@link PreparedDeleteObject}
         *
         * @return instance of {@link PreparedDeleteObject}
         */
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            checkNotNull(mapFunc, "Please specify map function");

            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteObject<>(
                    storIOContentResolver,
                    deleteResolver,
                    object,
                    mapFunc
            );
        }
    }
}
