package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for
 * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 */
public final class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull
    private final T object;

    @Nullable
    private final DeleteResolver<T> explicitDeleteResolver;

    PreparedDeleteObject(@NonNull StorIOContentResolver storIOContentResolver,
                         @NonNull T object,
                         @Nullable DeleteResolver<T> explicitDeleteResolver) {
        super(storIOContentResolver);
        this.object = object;
        this.explicitDeleteResolver = explicitDeleteResolver;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null result of Delete Operation.
     */
    @SuppressWarnings("unchecked")
    @WorkerThread
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        final DeleteResolver<T> deleteResolver;

        if (explicitDeleteResolver != null) {
            deleteResolver = explicitDeleteResolver;
        } else {
            final ContentResolverTypeMapping<T> typeMapping
                    = storIOContentResolver.internal().typeMapping((Class<T>) object.getClass());

            if (typeMapping == null) {
                throw new IllegalStateException("Object does not have type mapping: " +
                        "object = " + object + ", object.class = " + object.getClass() + "," +
                        "ContentProvider was not affected by this operation, please add type mapping for this type");
            }

            deleteResolver = typeMapping.deleteResolver();
        }

        return deleteResolver.performDelete(storIOContentResolver, object);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedDeleteObject}.
     *
     * @param <T> type of object to delete.
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final T object;

        private DeleteResolver<T> deleteResolver;

        /**
         * Creates builder for {@link PreparedDeleteObject}.
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
         * @param object                non-null object that should be deleted.
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(object, "Please specify object to delete");

            this.storIOContentResolver = storIOContentResolver;
            this.object = object;
        }

        /**
         * Optional: Specifies resolver for Delete Operation.
         * Allows you to customise behavior of Delete Operation.
         * <p>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly — exception will be thrown.
         *
         * @param deleteResolver resolver for Delete Operation.
         * @return builder.
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedDeleteObject}.
         *
         * @return new instance of {@link PreparedDeleteObject}.
         */
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<T>(
                    storIOContentResolver,
                    object,
                    deleteResolver
            );
        }
    }
}
