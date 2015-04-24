package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeDefaults;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutObject<T> extends PreparedPut<T, PutResult> {

    @NonNull
    private final T object;

    PreparedPutObject(@NonNull StorIOSQLite storIOSQLite,
                      @NonNull T object,
                      @NonNull PutResolver<T> putResolver) {
        super(storIOSQLite, putResolver);
        this.object = object;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null result of Put Operation
     */
    @NonNull
    public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(storIOSQLite, object);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));
        return putResult;
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    public Observable<PutResult> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutObject}
     *
     * @param <T> type of object to put
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
         * which allows you to customize behavior of Put Operation
         * <p/>
         * Can be set via {@link SQLiteTypeDefaults}
         * If it's not set via {@link SQLiteTypeDefaults} or explicitly -> exception will be thrown
         *
         * @param putResolver put resolver
         * @return builder
         * @see {@link DefaultPutResolver} — easy way to create {@link PutResolver}
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutObject} instance
         */
        @SuppressWarnings("unchecked")
        @NonNull
        public PreparedPutObject<T> prepare() {
            final SQLiteTypeDefaults<T> typeDefaults = storIOSQLite.internal().typeDefaults((Class<T>) object.getClass());

            if (putResolver == null && typeDefaults != null) {
                putResolver = typeDefaults.putResolver;
            }

            checkNotNull(putResolver, "Please specify PutResolver");

            return new PreparedPutObject<T>(
                    storIOSQLite,
                    object,
                    putResolver
            );
        }
    }
}
