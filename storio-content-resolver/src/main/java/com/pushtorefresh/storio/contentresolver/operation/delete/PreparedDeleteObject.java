package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for
 * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}.
 */
public final class PreparedDeleteObject<T> extends PreparedDelete<T, DeleteResult> {

    @NonNull
    private final T object;

    PreparedDeleteObject(@NonNull StorIOContentResolver storIOContentResolver,
                         @NonNull DeleteResolver<T> deleteResolver,
                         @NonNull T object) {
        super(storIOContentResolver, deleteResolver);
        this.object = object;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null result of Delete Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        return deleteResolver.performDelete(storIOContentResolver, object);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p/>
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
         * <p/>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly -> exception will be thrown.
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
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            final ContentResolverTypeMapping<T> typeMapping = storIOContentResolver.internal().typeMapping((Class<T>) object.getClass());

            if (deleteResolver == null && typeMapping != null) {
                deleteResolver = typeMapping.deleteResolver();
            }

            checkNotNull(deleteResolver, "StorIO can not perform delete of object = " +
                    object + "\nof type " + object.getClass() +
                    " without type mapping or Operation resolver." +
                    "\n Please add type mapping or Operation resolver");

            return new PreparedDeleteObject<T>(
                    storIOContentResolver,
                    deleteResolver,
                    object
            );
        }
    }
}
