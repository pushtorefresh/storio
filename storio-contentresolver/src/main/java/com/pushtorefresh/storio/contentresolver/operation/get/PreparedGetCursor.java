package com.pushtorefresh.storio.contentresolver.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.util.EnvironmentUtil;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Represents an Operation for {@link StorIOContentResolver} which performs query that retrieves data as {@link Cursor}
 * from {@link android.content.ContentProvider}
 */
public class PreparedGetCursor extends PreparedGet<Cursor> {

    @NonNull
    protected final Query query;

    PreparedGetCursor(@NonNull StorIOContentResolver storIOContentResolver, @NonNull GetResolver getResolver, @NonNull Query query) {
        super(storIOContentResolver, getResolver);
        this.query = query;
    }

    @Nullable
    @Override
    public Cursor executeAsBlocking() {
        return getResolver.performGet(storIOContentResolver, query);
    }

    @NonNull
    @Override
    public Observable<Cursor> createObservable() {
        EnvironmentUtil.throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override
            public void call(Subscriber<? super Cursor> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Creates an {@link Observable} which will be subscribed to changes of {@link #query} Uri
     * and will emit result each time change occurs
     * <p>
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
                .observeChangesOfUri(query.uri)
                .map(new Func1<Changes, Cursor>() {
                    @Override
                    public Cursor call(Changes changes) {
                        return executeAsBlocking();
                    }
                })
                .startWith(executeAsBlocking()); // start stream with first query result
    }

    /**
     * Builder for {@link PreparedGetCursor}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        private Query query;

        private GetResolver getResolver;

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
        public Builder withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        /**
         * Optional: Specifies {@link GetResolver} for Get Operation
         * which allows you to customize behavior of Get Operation
         * <p>
         * Default value is instance of {@link DefaultGetResolver}
         *
         * @param getResolver get resolver
         * @return builder
         */
        @NonNull
        public Builder withGetResolver(@Nullable GetResolver getResolver) {
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
            checkNotNull(query, "Please specify query");

            if (getResolver == null) {
                getResolver = DefaultGetResolver.INSTANCE;
            }

            return new PreparedGetCursor(
                    storIOContentResolver,
                    getResolver,
                    query
            );
        }
    }
}
