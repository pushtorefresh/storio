package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Put Operation to perform put multiple {@link ContentValues} into {@link StorIOContentResolver}
 */
public class PreparedPutContentValuesIterable extends PreparedPut<ContentValues, PutResults<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValues;

    PreparedPutContentValuesIterable(@NonNull StorIOContentResolver storIOContentResolver, @NonNull PutResolver<ContentValues> putResolver, @NonNull Iterable<ContentValues> contentValues) {
        super(storIOContentResolver, putResolver);
        this.contentValues = contentValues;
    }

    /**
     * Executes Put Operation immediately in current thread
     *
     * @return non-null results of Put Operation
     */
    @NonNull
    @Override
    public PutResults<ContentValues> executeAsBlocking() {
        final Map<ContentValues, PutResult> putResultsMap = new HashMap<ContentValues, PutResult>();

        for (final ContentValues cv : contentValues) {
            final PutResult putResult = putResolver.performPut(storIOContentResolver, cv);
            putResultsMap.put(cv, putResult);
        }

        return PutResults.newInstance(putResultsMap);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send results to observer
     *
     * @return non-null {@link Observable} which will perform Put Operation and send results to observer
     */
    @NonNull
    @Override
    public Observable<PutResults<ContentValues>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<PutResults<ContentValues>>() {
            @Override
            public void call(Subscriber<? super PutResults<ContentValues>> subscriber) {
                final PutResults<ContentValues> putResults = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(putResults);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}
     * <p/>
     * Required: You should specify query see {@link #withPutResolver(PutResolver)}
     */
    public static final class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Iterable<ContentValues> contentValues;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver,
                       @NonNull Iterable<ContentValues> contentValues) {
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
     * Compile-time safe part of builder for {@link PreparedPutContentValuesIterable}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Iterable<ContentValues> contentValues;

        @NonNull
        private final PutResolver<ContentValues> putResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Iterable<ContentValues> contentValues, @NonNull PutResolver<ContentValues> putResolver) {
            this.storIOContentResolver = storIOContentResolver;
            this.contentValues = contentValues;
            this.putResolver = putResolver;
        }

        /**
         * Builds instance of {@link PreparedPutContentValuesIterable}
         *
         * @return instance of {@link PreparedPutContentValuesIterable}
         */
        @NonNull
        public PreparedPutContentValuesIterable prepare() {
            return new PreparedPutContentValuesIterable(
                    storIOContentResolver,
                    putResolver,
                    contentValues
            );
        }
    }
}