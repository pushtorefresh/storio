package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Put Operation for {@link ContentValues}
 */
public final class PreparedPutContentValues extends PreparedPut<ContentValues, PutResult> {

    @NonNull
    private final ContentValues contentValues;

    PreparedPutContentValues(@NonNull StorIOContentResolver storIOContentResolver, @NonNull PutResolver<ContentValues> putResolver, @NonNull ContentValues contentValues) {
        super(storIOContentResolver, putResolver);
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
        return putResolver.performPut(storIOContentResolver, contentValues);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<PutResult> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Builder for {@link PreparedPutContentValues}
     * <p/>
     * Required: You should specify put resolver see {@link #withPutResolver(PutResolver)}
     */
    public static final class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final ContentValues contentValues;

        /**
         * Creates builder for {@link PreparedPutContentValues}
         *
         * @param storIOContentResolver instance of {@link StorIOContentResolver}
         * @param contentValues         some {@link ContentValues} to put
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull ContentValues contentValues) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(contentValues, "Please specify content values");

            this.storIOContentResolver = storIOContentResolver;
            this.contentValues = contentValues;
        }

        /**
         * Required: Specifies resolver for Put Operation
         * that should define behavior of Put Operation: insert or update of the {@link ContentValues}
         *
         * @param putResolver resolver for Put Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            checkNotNull(putResolver, "Please specify PutResolver");
            return new CompleteBuilder(storIOContentResolver, contentValues, putResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedPutContentValues}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final ContentValues contentValues;

        @NonNull
        private final PutResolver<ContentValues> putResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull ContentValues contentValues, @NonNull PutResolver<ContentValues> putResolver) {
            this.storIOContentResolver = storIOContentResolver;
            this.contentValues = contentValues;
            this.putResolver = putResolver;
        }

        /**
         * Builds instance of {@link PreparedPutContentValues}
         *
         * @return instance of {@link PreparedPutContentValues}
         */
        @NonNull
        public PreparedPutContentValues prepare() {
            return new PreparedPutContentValues(
                    storIOContentResolver,
                    putResolver,
                    contentValues
            );
        }
    }
}
