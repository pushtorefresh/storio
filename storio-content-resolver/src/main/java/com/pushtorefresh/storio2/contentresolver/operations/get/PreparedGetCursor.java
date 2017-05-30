package com.pushtorefresh.storio2.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.contentresolver.Changes;
import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.contentresolver.operations.internal.RxJavaUtils;
import com.pushtorefresh.storio2.contentresolver.queries.Query;
import com.pushtorefresh.storio2.operations.internal.FlowableOnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio2.operations.internal.MapSomethingToExecuteAsBlocking;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;

import static com.pushtorefresh.storio2.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio2.internal.Environment.throwExceptionIfRxJava2IsNotAvailable;

/**
 * Represents Get Operation for {@link StorIOContentResolver}
 * which performs query that retrieves data as {@link Cursor}.
 * from {@link android.content.ContentProvider}.
 */
public class PreparedGetCursor extends PreparedGet<Cursor> {

    @NonNull
    private final GetResolver<Cursor> getResolver;

    PreparedGetCursor(@NonNull StorIOContentResolver storIOContentResolver,
                      @NonNull GetResolver<Cursor> getResolver,
                      @NonNull Query query) {
        super(storIOContentResolver, query);
        this.getResolver = getResolver;
    }

    /**
     * Executes Get Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null {@link Cursor}, can be empty.
     */
    @WorkerThread
    @NonNull
    @Override
    public Cursor executeAsBlocking() {
        try {
            return getResolver.performGet(storIOContentResolver, query);
        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Get operation. query = " + query, exception);
        }
    }

    /**
     * Creates "Hot" {@link Flowable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of {@link #query} Uri will occur.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Flowable} because
     * it's "Hot" and endless.
     *
     * @return non-null {@link Flowable} which will emit non-null
     * list with mapped results and will be subscribed to changes of {@link #query} Uri.
     */
    @NonNull
    @CheckResult
    @Override
    public Flowable<Cursor> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        throwExceptionIfRxJava2IsNotAvailable("asRxFlowable()");

        final Flowable<Cursor> observable = storIOContentResolver
                .observeChangesOfUri(query.uri(), backpressureStrategy) // each change triggers executeAsBlocking
                .map(new MapSomethingToExecuteAsBlocking<Changes, Cursor, Query>(this))
                .startWith(Flowable.create(new FlowableOnSubscribeExecuteAsBlocking<Cursor, Query>(this), backpressureStrategy)); // start stream with first query result

        return RxJavaUtils.subscribeOn(storIOContentResolver, observable);
    }

    /**
     * Creates {@link Single} which will perform Get Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOContentResolver#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Get Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<Cursor> asRxSingle() {
        return RxJavaUtils.createSingle(storIOContentResolver, this);
    }

    /**
     * Builder for {@link PreparedGetCursor}.
     * <p>
     * Required: You should specify query see {@link #withQuery(Query)}.
     */
    public static class Builder {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation.
         *
         * @param query query.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify Query");
            return new CompleteBuilder(storIOContentResolver, query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetCursor}.
     */
    public static class CompleteBuilder {

        static final GetResolver<Cursor> STANDARD_GET_RESOLVER = new DefaultGetResolver<Cursor>() {
            @NonNull
            @Override
            public Cursor mapFromCursor(
                    @NonNull StorIOContentResolver storIOContentResolver,
                    @NonNull Cursor cursor
            ) {
                return cursor; // easy
            }
        };

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Query query;

        private GetResolver<Cursor> getResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Query query) {
            this.storIOContentResolver = storIOContentResolver;
            this.query = query;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation.
         * <p>
         * If no value will be set, builder will use resolver that
         * simply redirects query to {@link StorIOContentResolver}.
         *
         * @param getResolver GetResolver.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder withGetResolver(@Nullable GetResolver<Cursor> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Prepares Get Operation.
         *
         * @return {@link PreparedGetCursor} instance.
         */
        @NonNull
        public PreparedGetCursor prepare() {
            if (getResolver == null) {
                getResolver = STANDARD_GET_RESOLVER;
            }

            return new PreparedGetCursor(
                    storIOContentResolver,
                    getResolver,
                    query
            );
        }
    }
}
