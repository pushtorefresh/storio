package com.pushtorefresh.storio3.contentresolver.operations.put;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Prepared Put Operation to perform put object
 * into {@link StorIOContentResolver}.
 *
 * @param <T> type of the object.
 */
public class PreparedPutObject<T> extends PreparedPut<PutResult, T> {

    @NonNull
    private final T object;

    @Nullable
    private final PutResolver<T> explicitPutResolver;

    PreparedPutObject(@NonNull StorIOContentResolver storIOContentResolver,
                      @Nullable PutResolver<T> explicitPutResolver,
                      @NonNull T object) {
        super(storIOContentResolver);
        this.object = object;
        this.explicitPutResolver = explicitPutResolver;
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    private class RealCallInterceptor implements Interceptor {
        @NonNull
        @Override
        public <Result, WrappedResult, Data> Result intercept(@NonNull PreparedOperation<Result, WrappedResult, Data> operation, @NonNull Chain chain) {
            try {
                final PutResolver<T> putResolver;

                if (explicitPutResolver != null) {
                    putResolver = explicitPutResolver;
                } else {
                    //noinspection unchecked
                    final ContentResolverTypeMapping<T> typeMapping
                            = storIOContentResolver.lowLevel().typeMapping((Class<T>) object.getClass());

                    if (typeMapping == null) {
                        throw new IllegalStateException("Object does not have type mapping: " +
                                "object = " + object + ", object.class = " + object.getClass() + ", " +
                                "ContentProvider was not affected by this operation, please add type mapping for this type");
                    }

                    putResolver = typeMapping.putResolver();
                }

                //noinspection unchecked
                return (Result) putResolver.performPut(storIOContentResolver, object);
            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Put operation. object = " + object, exception);
            }
        }
    }

    /**
     * Creates {@link Flowable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<PutResult> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOContentResolver, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Put Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Put Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<PutResult> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
    }

    /**
     * Creates {@link Completable} which will perform Put Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Put Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOContentResolver, this);
    }

    @NonNull
    @Override
    public T getData() {
        return object;
    }

    /**
     * Builder for {@link PreparedPutObject}.
     *
     * @param <T> type of object.
     */
    public static class Builder<T> {

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
         * <p>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly — exception will be thrown.
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
        @NonNull
        public PreparedPutObject<T> prepare() {
            return new PreparedPutObject<T>(
                    storIOContentResolver,
                    putResolver,
                    object
            );
        }
    }
}
