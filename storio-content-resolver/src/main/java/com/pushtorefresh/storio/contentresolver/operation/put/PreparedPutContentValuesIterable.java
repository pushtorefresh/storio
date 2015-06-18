package com.pushtorefresh.storio.contentresolver.operation.put;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Put Operation to perform put multiple {@link ContentValues}
 * into {@link StorIOContentResolver}.
 */
public final class PreparedPutContentValuesIterable extends PreparedPut<PutResults<ContentValues>> {

    @NonNull
    private final Iterable<ContentValues> contentValues;

    @NonNull
    private final PutResolver<ContentValues> putResolver;

    PreparedPutContentValuesIterable(@NonNull StorIOContentResolver storIOContentResolver,
                                     @NonNull PutResolver<ContentValues> putResolver,
                                     @NonNull Iterable<ContentValues> contentValues) {
        super(storIOContentResolver);
        this.contentValues = contentValues;
        this.putResolver = putResolver;
    }

    /**
     * Executes Put Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @WorkerThread
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
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p/>
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
    public Observable<PutResults<ContentValues>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}.
     * <p/>
     * Required: You should specify query see {@link #withPutResolver(PutResolver)}.
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
         * that should define behavior of Put Operation: insert or update
         * of the {@link ContentValues}.
         *
         * @param putResolver resolver for Put Operation.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withPutResolver(@NonNull PutResolver<ContentValues> putResolver) {
            checkNotNull(putResolver, "Please specify PutResolver");
            return new CompleteBuilder(storIOContentResolver, contentValues, putResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedPutContentValuesIterable}.
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
         * Builds instance of {@link PreparedPutContentValuesIterable}.
         *
         * @return instance of {@link PreparedPutContentValuesIterable}.
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