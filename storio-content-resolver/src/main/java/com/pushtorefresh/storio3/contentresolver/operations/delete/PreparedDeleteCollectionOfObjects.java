package com.pushtorefresh.storio3.contentresolver.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio3.Interceptor;
import com.pushtorefresh.storio3.StorIOException;
import com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}.
 *
 * @param <T> type of objects to delete.
 */
public class PreparedDeleteCollectionOfObjects<T> extends PreparedDelete<DeleteResults<T>, Collection<T>> {

    @NonNull
    private final Collection<T> objects;

    @Nullable
    private final DeleteResolver<T> explicitDeleteResolver;

    PreparedDeleteCollectionOfObjects(@NonNull StorIOContentResolver storIOContentResolver,
                                      @NonNull Collection<T> objects,
                                      @Nullable DeleteResolver<T> explicitDeleteResolver) {
        super(storIOContentResolver);
        this.objects = objects;
        this.explicitDeleteResolver = explicitDeleteResolver;
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
                final StorIOContentResolver.LowLevel lowLevel = storIOContentResolver.lowLevel();

                // Nullable
                final List<SimpleImmutableEntry<T, DeleteResolver<T>>> objectsAndDeleteResolvers;

                if (explicitDeleteResolver != null) {
                    objectsAndDeleteResolvers = null;
                } else {
                    objectsAndDeleteResolvers = new ArrayList<SimpleImmutableEntry<T, DeleteResolver<T>>>(objects.size());

                    for (final T object : objects) {
                        //noinspection unchecked
                        final ContentResolverTypeMapping<T> typeMapping
                                = (ContentResolverTypeMapping<T>) lowLevel.typeMapping(object.getClass());

                        if (typeMapping == null) {
                            throw new IllegalStateException("One of the objects from the collection does not have type mapping: " +
                                    "object = " + object + ", object.class = " + object.getClass() + "," +
                                    "ContentProvider was not affected by this operation, please add type mapping for this type");
                        }

                        objectsAndDeleteResolvers.add(new SimpleImmutableEntry<T, DeleteResolver<T>>(
                                object,
                                typeMapping.deleteResolver()
                        ));
                    }
                }

                final Map<T, DeleteResult> results = new HashMap<T, DeleteResult>(objects.size());

                if (explicitDeleteResolver != null) {
                    for (final T object : objects) {
                        final DeleteResult deleteResult = explicitDeleteResolver.performDelete(storIOContentResolver, object);
                        results.put(object, deleteResult);
                    }
                } else {
                    for (final SimpleImmutableEntry<T, DeleteResolver<T>> objectAndDeleteResolver : objectsAndDeleteResolvers) {
                        final T object = objectAndDeleteResolver.getKey();
                        final DeleteResolver<T> deleteResolver = objectAndDeleteResolver.getValue();

                        final DeleteResult deleteResult = deleteResolver.performDelete(storIOContentResolver, object);
                        results.put(object, deleteResult);
                    }
                }

                //noinspection unchecked
                return (Result) DeleteResults.newInstance(results);

            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Delete operation. objects = " + objects, exception);
            }
        }
    }

    /**
     * Creates {@link Flowable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<DeleteResults<T>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOContentResolver, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Delete Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<DeleteResults<T>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
    }

    /**
     * Creates {@link Completable} which will perform Delete Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Delete Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOContentResolver, this);
    }

    @NonNull
    @Override
    public Collection<T> getData() {
        return objects;
    }

    /**
     * Builder for {@link PreparedDeleteCollectionOfObjects}.
     *
     * @param <T> type of objects.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Collection<T> objects;

        @Nullable
        private DeleteResolver<T> deleteResolver;

        /**
         * Creates builder for {@link PreparedDeleteCollectionOfObjects}.
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
         * @param objects               non-null collection of objects to delete.
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Collection<T> objects) {
            this.storIOContentResolver = storIOContentResolver;
            this.objects = objects;
        }

        /**
         * Optional: Specifies resolver for Delete Operation.
         * Allows you to customise behavior of Delete Operation.
         * <p>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly — exception will be thrown.
         *
         * @param deleteResolver nullable resolver for Delete Operation.
         * @return builder.
         */
        @NonNull
        public Builder<T> withDeleteResolver(@Nullable DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedDeleteCollectionOfObjects}.
         *
         * @return instance of {@link PreparedDeleteCollectionOfObjects}.
         */
        @NonNull
        public PreparedDeleteCollectionOfObjects<T> prepare() {
            return new PreparedDeleteCollectionOfObjects<T>(
                    storIOContentResolver,
                    objects,
                    deleteResolver
            );
        }
    }
}
