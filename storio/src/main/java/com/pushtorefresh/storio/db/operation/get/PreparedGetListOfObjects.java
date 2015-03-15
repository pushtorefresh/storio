package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class PreparedGetListOfObjects<T> extends PreparedGet<List<T>> {

    @NonNull private final MapFunc<Cursor, T> mapFunc;

    PreparedGetListOfObjects(@NonNull StorIODb storIODb, @NonNull Query query, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(storIODb, query, getResolver);
        this.mapFunc = mapFunc;
    }

    PreparedGetListOfObjects(@NonNull StorIODb storIODb, @NonNull RawQuery rawQuery, @NonNull GetResolver getResolver, @NonNull MapFunc<Cursor, T> mapFunc) {
        super(storIODb, rawQuery, getResolver);
        this.mapFunc = mapFunc;
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull public List<T> executeAsBlocking() {
        final Cursor cursor;

        if (query != null) {
            cursor = getResolver.performGet(storIODb, query);
        } else if (rawQuery != null) {
            cursor = getResolver.performGet(storIODb, rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }

        try {
            final List<T> list = new ArrayList<>(cursor.getCount());

            while (cursor.moveToNext()) {
                list.add(mapFunc.map(cursor));
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    @NonNull public Observable<List<T>> createObservable() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override public void call(Subscriber<? super List<T>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull @Override public Observable<List<T>> createObservableStream() {
        final Set<String> tables;

        if (query != null) {
            tables = new HashSet<>(1);
            tables.add(query.table);
        } else if (rawQuery != null) {
            tables = rawQuery.tables;
        } else {
            throw new IllegalStateException("Please specify query");
        }

        if (tables != null && !tables.isEmpty()) {
            return storIODb
                    .observeChangesInTables(tables)
                    .map(new Func1<Changes, List<T>>() { // each change triggers executeAsBlocking
                        @Override public List<T> call(Changes affectedTables) {
                            return executeAsBlocking();
                        }
                    })
                    .startWith(executeAsBlocking()); // start stream with first query result
        } else {
            return createObservable();
        }
    }

    public static class Builder<T> {

        @NonNull private final StorIODb storIODb;
        @NonNull
        private final Class<T> type; // currently type not used as object, only for generic Builder class

        private MapFunc<Cursor, T> mapFunc;
        private Query query;
        private RawQuery rawQuery;
        private GetResolver getResolver;

        public Builder(@NonNull StorIODb storIODb, @NonNull Class<T> type) {
            this.storIODb = storIODb;
            this.type = type;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<Cursor, T> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder<T> withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        @NonNull public Builder withGetResolver(@NonNull GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        @NonNull public PreparedOperationWithReactiveStream<List<T>> prepare() {
            if (getResolver == null) {
                getResolver = DefaultGetResolver.INSTANCE;
            }

            if (query != null) {
                return new PreparedGetListOfObjects<>(storIODb, query, getResolver, mapFunc);
            } else if (rawQuery != null) {
                return new PreparedGetListOfObjects<>(storIODb, rawQuery, getResolver, mapFunc);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
