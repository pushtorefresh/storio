package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Put Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of object to put.
 */
public final class PreparedPutObject<T> extends PreparedPut<PutResult> {

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
            final StorIOSQLite.Internal internal = storIOSQLite.internal();

            final PutResolver<T> putResolver;

            if (explicitPutResolver != null) {
                putResolver = explicitPutResolver;
            } else {
                final SQLiteTypeMapping<T> typeMapping = internal.typeMapping((Class<T>) object.getClass());

                if (typeMapping == null) {
                    throw new IllegalStateException("Object does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + "," +
                            "db was not affected by this operation, please add type mapping for this type");
                }

                putResolver = typeMapping.putResolver();
            }

            final PutResult putResult = putResolver.performPut(storIOSQLite, object);

            internal.notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));

            return putResult;
        } catch (Exception exception) {
            throw new StorIOException(exception);
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
     * @param <T> type of object to put.
     */
    public static final class Builder<T> {

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
