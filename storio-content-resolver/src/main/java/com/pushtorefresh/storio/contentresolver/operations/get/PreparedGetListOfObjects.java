package com.pushtorefresh.storio.contentresolver.operations.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.operations.internal.MapSomethingToExecuteAsBlocking;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;
import static java.util.Collections.EMPTY_LIST;
import static java.util.Collections.unmodifiableList;

/**
 * Represents Get Operation for {@link StorIOContentResolver}
 * which performs query that retrieves data as list of objects
 * from {@link android.content.ContentProvider}.
 *
 * @param <T> type of result.
 */
public final class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull
    private final Class<T> type;

    @NonNull
    private final Query query;

    @Nullable
    private final GetResolver<T> explicitGetResolver;

    PreparedGetListOfObjects(@NonNull StorIOContentResolver storIOContentResolver,
                             @NonNull Class<T> type,
                             @NonNull Query query,
                             @Nullable GetResolver<T> explicitGetResolver) {
        super(storIOContentResolver);
        this.type = type;
        this.query = query;
        this.explicitGetResolver = explicitGetResolver;
    }

    /**
     * Executes Prepared Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null, immutable {@link List} with mapped results, list can be empty.
     */
    @WorkerThread
    @SuppressWarnings("unchecked") // for empty list
    @NonNull
    @Override
    public List<T> executeAsBlocking() {
        try {
            final GetResolver<T> getResolver;

            if (explicitGetResolver != null) {
                getResolver = explicitGetResolver;
            } else {
                final ContentResolverTypeMapping<T> typeMapping = storIOContentResolver.internal().typeMapping(type);

                if (typeMapping == null) {
                    throw new IllegalStateException("This type does not have type mapping: " +
                            "type = " + type + "," +
                            "ContentProvider was not touched by this operation, please add type mapping for this type");
                }

                getResolver = typeMapping.getResolver();
            }

            final Cursor cursor = getResolver.performGet(storIOContentResolver, query);

            try {
                final int count = cursor.getCount();

                if (count == 0) {
                    return EMPTY_LIST; // it's immutable
                } else {
                    final List<T> list = new ArrayList<T>(count);

                    while (cursor.moveToNext()) {
                        list.add(getResolver.mapFromCursor(cursor));
                    }

                    return unmodifiableList(list);
                }
            } finally {
                cursor.close();
            }
        } catch (Exception exception) {
            throw new StorIOException(exception);
        }
    }

    /**
     * Creates "Hot" {@link Observable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs.
     * <p>
     * First result will be emitted immediately after subscription,
     * other emissions will occur only if changes of {@link #query} Uri will occur.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     * <p>
     * Please don't forget to unsubscribe from this {@link Observable}
     * because it's "Hot" and endless.
     *
     * @return non-null {@link Observable} which will emit non-null, immutable
     * {@link List} with mapped results and will be subscribed to changes of {@link #query} Uri,
     * list can be empty.
     */
    @NonNull
    @Override
    public Observable<List<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return storIOContentResolver
                .observeChangesOfUri(query.uri()) // each change triggers executeAsBlocking
                .map(MapSomethingToExecuteAsBlocking.newInstance(this))
                .startWith(Observable.create(OnSubscribeExecuteAsBlocking.newInstance(this))) // start stream with first query result
                .subscribeOn(Schedulers.io());
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
         * Can be set via {@link ContentResolverTypeMapping},
         * If value is not set via {@link ContentResolverTypeMapping} — exception will be thrown.
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
            return new PreparedGetListOfObjects<T>(
                    storIOContentResolver,
                    type,
                    query,
                    getResolver
            );
        }
    }
}
