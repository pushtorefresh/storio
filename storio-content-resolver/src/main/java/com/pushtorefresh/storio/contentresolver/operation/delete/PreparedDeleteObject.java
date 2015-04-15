package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedDeleteObject<T> extends PreparedDelete<T, DeleteResult> {

    @NonNull
    private final T object;

    PreparedDeleteObject(@NonNull StorIOContentResolver storIOContentResolver,
                         @NonNull DeleteResolver<T> deleteResolver,
                         @NonNull T object) {
        super(storIOContentResolver, deleteResolver);
        this.object = object;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        return deleteResolver.performDelete(storIOContentResolver, object);
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
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final T object;

        private DeleteResolver<T> deleteResolver;

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
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set via {@link ContentResolverTypeDefaults} or explicitly -> exception will be thrown
         *
         * @param deleteResolver resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
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

            if (deleteResolver == null && typeDefaults != null) {
                deleteResolver = typeDefaults.deleteResolver;
            }

            checkNotNull(deleteResolver, "Please specify Delete Resolver");

            return new PreparedDeleteObject<T>(
                    storIOContentResolver,
                    deleteResolver,
                    object
            );
        }
    }
}
