package com.pushtorefresh.storio.contentresolver.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Put Operation for collection of objects
 *
 * @param <T> type of objects
 */
public final class PreparedPutObjects<T> extends PreparedPut<T, PutResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    PreparedPutObjects(@NonNull StorIOContentResolver storIOContentResolver,
                       @NonNull PutResolver<T> putResolver,
                       @NonNull Iterable<T> objects) {
        super(storIOContentResolver, putResolver);
        this.objects = objects;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null result of Put Operation
     */
    @NonNull
    @Override
    public PutResults<T> executeAsBlocking() {
        final Map<T, PutResult> putResults = new HashMap<T, PutResult>();

        for (T object : objects) {
            final PutResult putResult = putResolver.performPut(storIOContentResolver, object);
            putResults.put(object, putResult);
        }

        return PutResults.newInstance(putResults);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<PutResults<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutObjects}
     *
     * @param <T> type of objects to put
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Iterable<T> objects;

        private PutResolver<T> putResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.objects = objects;
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
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedPutObjects}
         *
         * @return new instance of {@link PreparedPutObjects}
         */
        @NonNull
        public PreparedPutObjects<T> prepare() {
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults(type);

            if (putResolver == null && typeDefaults != null) {
                putResolver = typeDefaults.putResolver;
            }

            checkNotNull(putResolver, "Please specify Put Resolver");

            return new PreparedPutObjects<T>(
                    storIOContentResolver,
                    putResolver,
                    objects
            );
        }
    }
}
