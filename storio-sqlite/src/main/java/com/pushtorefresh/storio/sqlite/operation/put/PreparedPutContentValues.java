package com.pushtorefresh.storio.sqlite.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

public class PreparedPutContentValues extends PreparedPut<ContentValues, PutResult> {

    @NonNull
    private final ContentValues contentValues;

    PreparedPutContentValues(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues, @NonNull PutResolver<ContentValues> putResolver) {
        super(storIOSQLite, putResolver);
        this.contentValues = contentValues;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null result of Put Operation
     */
    @NonNull
    @Override
    public PutResult executeAsBlocking() {
        final PutResult putResult = putResolver.performPut(storIOSQLite, contentValues);
        storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));
        return putResult;
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<PutResult> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutContentValues}
     */
    public static class Builder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final ContentValues contentValues;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues) {
            this.storIOSQLite = storIOSQLite;
            this.contentValues = contentValues;
        }

        /**
         * Required: Specifies {@link PutResolver} for Put Operation
         * which allows you to customize behavior of Put Operation
         *
         * @param putResolver put resolver
         * @return builder
         * @see {@link DefaultPutResolver} — easy way to create {@link PutResolver}
         */
        @NonNull
        public CompleteBuilder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            return new CompleteBuilder(
                    storIOSQLite,
                    contentValues,
                    putResolver
            );
        }
    }

    /**
     * Compile-time safe part of {@link Builder}
     */
    public static class CompleteBuilder {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final ContentValues contentValues;

        private final PutResolver<ContentValues> putResolver;

        CompleteBuilder(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues, PutResolver<ContentValues> putResolver) {
            this.storIOSQLite = storIOSQLite;
            this.contentValues = contentValues;
            this.putResolver = putResolver;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutContentValues} instance
         */
        @NonNull
        public PreparedPutContentValues prepare() {
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutContentValues(
                    storIOSQLite,
                    contentValues,
                    putResolver
            );
        }
    }
}
