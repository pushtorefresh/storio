package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for {@link StorIOContentResolver}.
 *
 * @param <T> type of objects to delete.
 */
public final class PreparedDeleteObjects<T> extends PreparedDelete<T, DeleteResults<T>> {

    @NonNull
    private final Iterable<T> objects;

    PreparedDeleteObjects(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver<T> deleteResolver, @NonNull Iterable<T> objects) {
        super(storIOContentResolver, deleteResolver);
        this.objects = objects;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Delete Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public DeleteResults<T> executeAsBlocking() {
        final Map<T, DeleteResult> deleteResultsMap = new HashMap<T, DeleteResult>();

        for (final T object : objects) {
            final DeleteResult deleteResult = deleteResolver.performDelete(storIOContentResolver, object);
            deleteResultsMap.put(object, deleteResult);
        }

        return DeleteResults.newInstance(deleteResultsMap);
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
    public Observable<DeleteResults<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedDeleteObjects}.
     *
     * @param <T> type of objects.
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Iterable<T> objects;

        private DeleteResolver<T> deleteResolver;

        /**
         * Creates builder for {@link PreparedDeleteObjects}.
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}.
         * @param type                  type of objects.
         * @param objects               non-null collection of objects to delete.
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Iterable<T> objects) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.objects = objects;
        }

        /**
         * Optional: Specifies resolver for Delete Operation.
         * Allows you to customise behavior of Delete Operation.
         * <p/>
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping}
         * or explicitly -> exception will be thrown.
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
         * Builds instance of {@link PreparedDeleteObjects}.
         *
         * @return instance of {@link PreparedDeleteObjects}.
         */
        @NonNull
        public PreparedDeleteObjects<T> prepare() {
            final ContentResolverTypeMapping<T> typeMapping = storIOContentResolver.internal().typeMapping(type);

            if (deleteResolver == null && typeMapping != null) {
                deleteResolver = typeMapping.deleteResolver();
            }

            checkNotNull(deleteResolver, "Please specify Delete Resolver");

            return new PreparedDeleteObjects<T>(
                    storIOContentResolver,
                    deleteResolver,
                    objects
            );
        }
    }
}
