package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.operation.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs query that retrieves data as {@link Cursor}
 * from {@link android.content.ContentProvider}
 */
public class PreparedGetCursor extends PreparedGet<Cursor, Cursor> {

    @NonNull
    protected final Query query;

    PreparedGetCursor(@NonNull StorIOContentResolver storIOContentResolver, @NonNull GetResolver<Cursor> getResolver, @NonNull Query query) {
        super(storIOContentResolver, getResolver);
        this.query = query;
    }

    @NonNull
    @Override
    public Cursor executeAsBlocking() {
        return getResolver.performGet(storIOContentResolver, query);
    }

    @NonNull
    @Override
    public Observable<Cursor> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");
        return Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this));
    }

    /**
     * Creates an {@link Observable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs
     * <p/>
     * First result will be emitted immediately,
     * other emissions will occur only if changes of {@link #query} Uri will occur
     *
     * @return non-null {@link Observable} which will emit non-null list with mapped results and will be subscribed to changes of {@link #query} Uri
     */
    @NonNull
    @Override
    public Observable<Cursor> createObservableStream() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservableStream()");

        return storIOContentResolver
                .observeChangesOfUri(query.uri) // each change triggers executeAsBlocking
                .map(MapSomethingToExecuteAsBlocking.newInstance(this))
                .startWith(executeAsBlocking()); // start stream with first query result
    }

    /**
     * Builder for {@link PreparedGetCursor}
     * <p/>
     * Required: You should specify query see {@link #withQuery(Query)}
     */
    public static final class Builder {

        @NonNull
        final StorIOContentResolver storIOContentResolver;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver) {
            this.storIOContentResolver = storIOContentResolver;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation
         *
         * @param query query
         * @return builder
         */
        @NonNull
        public CompleteBuilder withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify Query");
            return new CompleteBuilder(storIOContentResolver, query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetCursor}
     */
    public static final class CompleteBuilder {

        private static final GetResolver<Cursor> STANDARD_GET_RESOLVER = new DefaultGetResolver<Cursor>() {
            @NonNull
            @Override
            public Cursor mapFromCursor(@NonNull Cursor cursor) {
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
         * which allows you to customize behavior of Get Operation
         * <p/>
         * If no value will be set, builder will use resolver that simply redirects query to {@link StorIOContentResolver}
         *
         * @param getResolver get resolver
         * @return builder
         */
        @NonNull
        public CompleteBuilder withGetResolver(@Nullable GetResolver<Cursor> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Prepares Get Operation
         *
         * @return {@link PreparedGetCursor} instance
         */
        @NonNull
        public PreparedOperationWithReactiveStream<Cursor> prepare() {
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
