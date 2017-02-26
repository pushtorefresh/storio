package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.internal.RxJavaUtils;

import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Prepared Put Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to put.
 */
public class PreparedPutObject<T> extends PreparedPut<PutResult> {

    @NonNull
    private final T object;

    @Nullable
    private final PutResolver<T> explicitPutResolver;

    PreparedPutObject(@NonNull StorIOSQLite storIOSQLite,
                      @NonNull T object,
                      @Nullable PutResolver<T> explicitPutResolver) {
        super(storIOSQLite);
        this.object = object;
        this.explicitPutResolver = explicitPutResolver;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null result of Put Operation.
     */
    @SuppressWarnings("unchecked")
    @WorkerThread
    @NonNull
    @Override
    public PutResult executeAsBlocking() {
        try {
            final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

            final PutResolver<T> putResolver;

            if (explicitPutResolver != null) {
                putResolver = explicitPutResolver;
            } else {
                final SQLiteTypeMapping<T> typeMapping = lowLevel.typeMapping((Class<T>) object.getClass());

                if (typeMapping == null) {
                    throw new IllegalStateException("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + ", " +
                            "db was not affected by this operation, please add type mapping for this type");
                }

                putResolver = typeMapping.putResolver();
            }

            final PutResult putResult = putResolver.performPut(storIOSQLite, object);

            if (putResult.wasInserted() || putResult.wasUpdated()) {
                final Changes changes = Changes.newInstance(
                        putResult.affectedTables(),
                        putResult.affectedTags()
                );
                lowLevel.notifyAboutChanges(changes);
            }

            return putResult;
        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Put operation. object = " + object, exception);
        }
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Put Operation.
     * and send result to observer.
     * @deprecated (will be removed in 2.0) please use {@link #asRxObservable()}.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<PutResult> createObservable() {
        return asRxObservable();
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<PutResult> asRxObservable() {
        return RxJavaUtils.createObservable(storIOSQLite, this);
    }

    /**
     * Creates {@link Single} which will perform Put Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Put Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<PutResult> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    /**
     * Creates {@link Completable} which will perform Put Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Put Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
   }

    /**
     * Builder for {@link PreparedPutObject}.
     *
     * @param <T> type of object to put.
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final T object;

        private PutResolver<T> putResolver;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull T object) {
            this.storIOSQLite = storIOSQLite;
            this.object = object;
        }

        /**
         * Optional: Specifies {@link PutResolver} for Put Operation
         * which allows you to customize behavior of Put Operation.
         * <p>
         * Can be set via {@link SQLiteTypeMapping}
         * If it's not set via {@link SQLiteTypeMapping} or explicitly — exception will be thrown.
         *
         * @param putResolver put resolver.
         * @return builder.
         * @see DefaultPutResolver
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Prepares Put Operation.
         *
         * @return {@link PreparedPutObject} instance.
         */
        @NonNull
        public PreparedPutObject<T> prepare() {
            return new PreparedPutObject<T>(
                    storIOSQLite,
                    object,
                    putResolver
            );
        }
    }
}
