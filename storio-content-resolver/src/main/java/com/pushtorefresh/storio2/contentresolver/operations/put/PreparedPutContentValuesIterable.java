package com.pushtorefresh.storio2.contentresolver.operations.put;

import android.content.ContentValues;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.internal.RxJavaUtils;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;

/**
 * Prepared Put Operation to perform put multiple {@link ContentValues}
 * into {@link StorIOContentResolver}.
 */
public class PreparedPutContentValuesIterable extends PreparedPut<PutResults<ContentValues>, Iterable<ContentValues>> {

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
     * <p>
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
        try {
            final Map<ContentValues, PutResult> putResultsMap = new HashMap<ContentValues, PutResult>();

            for (final ContentValues cv : contentValues) {
                final PutResult putResult = putResolver.performPut(storIOContentResolver, cv);
                putResultsMap.put(cv, putResult);
            }

            return PutResults.newInstance(putResultsMap);
        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Put operation. contentValues = " + contentValues, exception);
        }
    }

    /**
     * Creates {@link Flowable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<PutResults<ContentValues>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOContentResolver, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Put Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Put Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<PutResults<ContentValues>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
   }

    /**
     * Creates {@link Completable} which will perform Put Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Put Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOContentResolver, this);
    }

    @NonNull
    @Override
    public Iterable<ContentValues> getData() {
        return contentValues;
    }

    /**
     * Builder for {@link PreparedPutContentValuesIterable}.
     * <p>
     * Required: You should specify query see {@link #withPutResolver(PutResolver)}.
     */
    public static class Builder {

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
    public static class CompleteBuilder {

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
