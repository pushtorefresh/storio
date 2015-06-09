package com.pushtorefresh.storio.contentresolver.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Put Operation to perform put object
 * into {@link StorIOContentResolver}.
 *
 * @param <T> type of the object.
 */
public final class PreparedPutObject<T> extends PreparedPut<T, PutResult> {

    @NonNull
    private final T object;

    protected PreparedPutObject(@NonNull StorIOContentResolver storIOContentResolver,
                                @NonNull PutResolver<T> putResolver,
                                @NonNull T object) {
        super(storIOContentResolver, putResolver);
        this.object = object;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public PutResult executeAsBlocking() {
        return putResolver.performPut(storIOContentResolver, object);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p/>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<PutResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedPutObject}.
     *
     * @param <T> type of object.
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final T object;

        private PutResolver<T> putResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull T object) {
            this.storIOContentResolver = storIOContentResolver;
            this.object = object;
        }

        /**
         * Optional: Specifies resolver for Put Operation
         * that should define behavior of Put Operation: insert or update
         * of the object.
         * <p/>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly -> exception will be thrown.
         *
         * @param putResolver nullable resolver for Put Operation.
         * @return builder.
         */
        @NonNull
        public Builder<T> withPutResolver(@Nullable PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedPutObject}.
         *
         * @return instance of {@link PreparedPutObject}.
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedPutObject<T> prepare() {
            final ContentResolverTypeMapping<T> typeMapping = storIOContentResolver.internal().typeMapping((Class<T>) object.getClass());

            if (putResolver == null && typeMapping != null) {
                putResolver = typeMapping.putResolver();
            }

            checkNotNull(putResolver, "StorIO can not perform put of object = " +
                    object + "\nof type " + object.getClass() +
                    " without type mapping or Operation resolver." +
                    "\n Please add type mapping or Operation resolver");

            return new PreparedPutObject<T>(
                    storIOContentResolver,
                    putResolver,
                    object
            );
        }
    }
}
