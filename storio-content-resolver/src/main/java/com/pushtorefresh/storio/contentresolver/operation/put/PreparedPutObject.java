package com.pushtorefresh.storio.contentresolver.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;
import static com.pushtorefresh.storio.util.EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable;

public class PreparedPutObject<T> extends PreparedPut<T, PutResult> {

    @NonNull
    private final T object;

    protected PreparedPutObject(@NonNull StorIOContentResolver storIOContentResolver,
                                @NonNull PutResolver<T> putResolver,
                                @NonNull T object) {
        super(storIOContentResolver, putResolver);
        this.object = object;
    }

    @NonNull
    @Override
    public PutResult executeAsBlocking() {
        return putResolver.performPut(storIOContentResolver, object);
    }

    @NonNull
    @Override
    public Observable<PutResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutObject}
     *
     * @param <T> type of object
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
         * that should define behavior of Put Operation: insert or update of the {@link android.content.ContentValues}
         * <p/>
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set via {@link ContentResolverTypeDefaults} or explicitly -> exception will be thrown
         *
         * @param putResolver nullable resolver for Put Operation
         * @return builder
         */
        @NonNull
        public Builder<T> withPutResolver(@Nullable PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedPutObject}
         *
         * @return instance of {@link PreparedPutObject}
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedPutObject<T> prepare() {
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults((Class<T>) object.getClass());

            if (putResolver == null && typeDefaults != null) {
                putResolver = typeDefaults.putResolver;
            }

            checkNotNull(putResolver, "Please specify Put Resolver");

            return new PreparedPutObject<T>(
                    storIOContentResolver,
                    putResolver,
                    object
            );
        }
    }
}
