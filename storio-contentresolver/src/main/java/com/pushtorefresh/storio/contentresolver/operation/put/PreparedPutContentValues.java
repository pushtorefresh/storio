package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Put Operation for {@link ContentValues}
 */
public class PreparedPutContentValues extends PreparedPut<ContentValues, PutResult> {

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
        final PutResult putResult = putResolver.performPut(storIOContentResolver, contentValues);
        putResolver.afterPut(contentValues, putResult);
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
        return Observable.create(new Observable.OnSubscribe<PutResult>() {
            @Override
            public void call(Subscriber<? super PutResult> subscriber) {
                final PutResult putResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedPutContentValues}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final ContentValues contentValues;

        private PutResolver<ContentValues> putResolver;

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
         * <p>
         * Default value is <code>null</code>
         *
         * @param putResolver resolver for Put Operation
         * @return builder
         */
        @NonNull
        public Builder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedPutContentValues}
         *
         * @return instance of {@link PreparedPutContentValues}
         */
        @NonNull
        public PreparedPutContentValues prepare() {
            checkNotNull(putResolver, "Please specify put resolver");

            return new PreparedPutContentValues(
                    storIOContentResolver,
                    putResolver,
                    contentValues
            );
        }
    }
}
