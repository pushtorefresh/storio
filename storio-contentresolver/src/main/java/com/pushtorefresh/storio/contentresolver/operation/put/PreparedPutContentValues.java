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
     * <p>
     * Required: You should specify put resolver see {@link #withPutResolver(PutResolver)}
     */
    public static class Builder {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        @NonNull
        final ContentValues contentValues;

        PutResolver<ContentValues> putResolver;

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
            this.putResolver = putResolver;
            return new CompleteBuilder(this);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedPutContentValues}
     */
    public static class CompleteBuilder extends Builder {

        CompleteBuilder(@NonNull final Builder builder) {
            super(builder.storIOContentResolver, builder.contentValues);

            putResolver = builder.putResolver;
        }

        /**
         * {@inheritDoc}
         */
        @NonNull
        @Override
        public CompleteBuilder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            super.withPutResolver(putResolver);
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
