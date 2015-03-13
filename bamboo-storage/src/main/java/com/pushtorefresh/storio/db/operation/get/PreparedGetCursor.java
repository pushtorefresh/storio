package com.pushtorefresh.storio.db.operation.get;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.PreparedOperationWithReactiveStream;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;

import java.util.HashSet;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public class PreparedGetCursor extends PreparedGet<Cursor> {

    PreparedGetCursor(@NonNull BambooStorageDb bambooStorageDb, @NonNull Query query) {
        super(bambooStorageDb, query);
    }

    PreparedGetCursor(@NonNull BambooStorageDb bambooStorageDb, @NonNull RawQuery rawQuery) {
        super(bambooStorageDb, rawQuery);
    }

    @NonNull public Cursor executeAsBlocking() {
        if (query != null) {
            return bambooStorageDb.internal().query(query);
        } else if (rawQuery != null) {
            return bambooStorageDb.internal().rawQuery(rawQuery);
        } else {
            throw new IllegalStateException("Please specify query");
        }
    }

    @NonNull @Override public Observable<Cursor> createObservable() {
        return Observable.create(new Observable.OnSubscribe<Cursor>() {
            @Override public void call(Subscriber<? super Cursor> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(executeAsBlocking());
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull @Override public Observable<Cursor> createObservableStream() {
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
            return bambooStorageDb
                    .observeChangesInTables(tables)
                    .map(new Func1<Changes, Cursor>() { // each change triggers executeAsBlocking
                        @Override public Cursor call(Changes changes) {
                            return executeAsBlocking();
                        }
                    })
                    .startWith(executeAsBlocking()); // start stream with first query result
        } else {
            return createObservable();
        }
    }

    public static class Builder {

        @NonNull private final BambooStorageDb bambooStorageDb;

        private Query query;
        private RawQuery rawQuery;

        public Builder(@NonNull BambooStorageDb bambooStorageDb) {
            this.bambooStorageDb = bambooStorageDb;
        }

        @NonNull public Builder withQuery(@NonNull Query query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder withQuery(@NonNull RawQuery rawQuery) {
            this.rawQuery = rawQuery;
            return this;
        }

        @NonNull public PreparedOperationWithReactiveStream<Cursor> prepare() {
            if (query != null) {
                return new PreparedGetCursor(bambooStorageDb, query);
            } else if (rawQuery != null) {
                return new PreparedGetCursor(bambooStorageDb, rawQuery);
            } else {
                throw new IllegalStateException("Please specify query");
            }
        }
    }
}
