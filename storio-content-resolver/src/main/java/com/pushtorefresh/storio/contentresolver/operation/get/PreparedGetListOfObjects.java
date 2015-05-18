package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeDefaults;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.internal.MapSomethingToExecuteAsBlocking;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Represents Get Operation for {@link StorIOContentResolver}
 * which performs query that retrieves data as list of objects
 * from {@link android.content.ContentProvider}.
 *
 * @param <T> type of result.
 */
public final class PreparedGetListOfObjects<T> extends PreparedGet<T, List<T>> {

    @NonNull
    private final Query query;

    PreparedGetListOfObjects(@NonNull StorIOContentResolver storIOContentResolver,
                             @NonNull GetResolver<T> getResolver,
                             @NonNull Query query) {
        super(storIOContentResolver, getResolver);
        this.query = query;
    }

    /**
     * Executes Prepared Operation immediately in current thread.
     *
     * @return non-null {@link List} with mapped results, can be empty.
     */
    @NonNull
    @Override
    public List<T> executeAsBlocking() {
        final Cursor cursor = getResolver.performGet(storIOContentResolver, query);

        try {
            if (cursor.getCount() == 0) {
                return new ArrayList<T>(0);
            } else {
                final List<T> list = new ArrayList<T>(cursor.getCount());

                while (cursor.moveToNext()) {
                    list.add(getResolver.mapFromCursor(cursor));
                }

                return list;
            }
        } finally {
            cursor.close();
        }
    }

    /**
     * Creates "Hot" {@link Observable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately,
     * other emissions will occur only if changes of {@link #query} Uri will occur.
     * <p>
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>Does not operate by default on a particular {@link rx.Scheduler}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Observable}
     * because it's "Hot" and endless.
     *
     * @return non-null {@link Observable} which will emit non-null
     * list with mapped results and will be subscribed to changes of {@link #query} Uri.
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return storIOContentResolver
                .observeChangesOfUri(query.uri()) // each change triggers executeAsBlocking
                .map(MapSomethingToExecuteAsBlocking.newInstance(this))
                .startWith(executeAsBlocking());  // start stream with first query result
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetListOfObjects}.
     *
     * @param <T> type of objects for query.
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
        }

        /**
         * Required: Specifies {@link Query} for Get Operation.
         *
         * @param query query.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> withQuery(@NonNull Query query) {
            checkNotNull(query, "Please specify query");
            return new CompleteBuilder<T>(storIOContentResolver, type, query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link PreparedGetListOfObjects}.
     *
     * @param <T> type of objects for query.
     */
    public static final class CompleteBuilder<T> {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final Class<T> type;

        @NonNull
        private final Query query;

        private GetResolver<T> getResolver;

        CompleteBuilder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull Class<T> type, @NonNull Query query) {
            this.storIOContentResolver = storIOContentResolver;
            this.type = type;
            this.query = query;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation.
         * <p>
         * Can be set via {@link ContentResolverTypeDefaults},
         * If value is not set via {@link ContentResolverTypeDefaults} -> exception will be thrown.
         *
         * @param getResolver GetResolver.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> withGetResolver(@Nullable GetResolver<T> getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Builds new instance of {@link PreparedGetListOfObjects}.
         *
         * @return new instance of {@link PreparedGetListOfObjects}.
         */
        @NonNull
        public PreparedGetListOfObjects<T> prepare() {
            final ContentResolverTypeDefaults<T> typeDefaults = storIOContentResolver.internal().typeDefaults(type);

            if (getResolver == null && typeDefaults != null) {
                getResolver = typeDefaults.getResolver;
            }

            checkNotNull(getResolver, "Please specify Get Resolver");

            return new PreparedGetListOfObjects<T>(
                    storIOContentResolver,
                    getResolver,
                    query
            );
        }
    }
}