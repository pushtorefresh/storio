package com.pushtorefresh.storio2.sqlite.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.internal.RxJavaUtils;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to delete.
 */
public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult, T> {

    @NonNull
    private final T object;

    @Nullable
    private final DeleteResolver<T> explicitDeleteResolver;

    PreparedDeleteObject(@NonNull StorIOSQLite storIOSQLite,
                         @NonNull T object,
                         @Nullable DeleteResolver<T> explicitDeleteResolver) {
        super(storIOSQLite);
        this.object = object;
        this.explicitDeleteResolver = explicitDeleteResolver;
    }

    /**
     * Creates {@link Flowable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<DeleteResult> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOSQLite, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Delete Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<DeleteResult> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
   }

    /**
     * Creates {@link Completable} which will perform Delete Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Delete Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    @NonNull
    @Override
    public T getData() {
        return object;
    }

    private class RealCallInterceptor implements Interceptor {
        @NonNull
        @Override
        public <Result, Data> Result intercept(@NonNull PreparedOperation<Result, Data> operation, @NonNull Chain chain) {
            try {
                final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

                final DeleteResolver<T> deleteResolver;

                if (explicitDeleteResolver != null) {
                    deleteResolver = explicitDeleteResolver;
                } else {
                    final SQLiteTypeMapping<T> typeMapping
                            = lowLevel.typeMapping((Class<T>) object.getClass());

                    if (typeMapping == null) {
                        throw new IllegalStateException("Object does not have type mapping: " +
                                "object = " + object + ", object.class = " + object.getClass() + ", " +
                                "db was not affected by this operation, please add type mapping for this type");
                    }

                    deleteResolver = typeMapping.deleteResolver();
                }

                final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);
                if (deleteResult.numberOfRowsDeleted() > 0) {
                    final Changes changes = Changes.newInstance(
                            deleteResult.affectedTables(),
                            deleteResult.affectedTags()
                    );
                    lowLevel.notifyAboutChanges(changes);
                }
                return (Result) deleteResult;

            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Delete operation. object = " + object, exception);
            }
        }

    }

    /**
     * Builder for {@link PreparedDeleteObject}.
     *
     * @param <T> type of object to delete.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final T object;

        private DeleteResolver<T> deleteResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull T object) {
            this.storIOSQLite = storIOSQLite;
            this.object = object;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation.
         * <p>
         * Can be set via {@link SQLiteTypeMapping},
         * If resolver is not set via {@link SQLiteTypeMapping}
         * or explicitly — exception will be thrown.
         *
         * @param deleteResolver delete resolver.
         * @return builder.
         */
        @NonNull
        public Builder<T> withDeleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation.
         *
         * @return {@link PreparedDeleteObject} instance.
         */
        @NonNull
        public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<T>(
                    storIOSQLite,
                    object,
                    deleteResolver
            );
        }
    }
}
