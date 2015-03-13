package com.pushtorefresh.storio.db.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.MapFunc;
import com.pushtorefresh.storio.db.query.DeleteQuery;

import java.util.Collections;

import rx.Observable;
import rx.Subscriber;

public class PreparedDeleteObject<T> extends PreparedDelete<DeleteResult> {

    @NonNull private final T object;
    @NonNull private final MapFunc<T, DeleteQuery> mapFunc;

    protected PreparedDeleteObject(@NonNull StorIODb storIODb, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc) {
        super(storIODb);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final StorIODb.Internal internal = storIODb.internal();
        final DeleteQuery deleteQuery = mapFunc.map(object);

        final int countOfDeletedRows = internal.delete(deleteQuery);

        internal.notifyAboutChanges(new Changes(deleteQuery.table));

        return DeleteResult.newDeleteResult(countOfDeletedRows, Collections.singleton(deleteQuery.table));
    }

    @NonNull @Override public Observable<DeleteResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder<T> {

        @NonNull private final StorIODb storIODb;
        @NonNull private final T object;

        private MapFunc<T, DeleteQuery> mapFunc;

        public Builder(@NonNull StorIODb storIODb, @NonNull T object) {
            this.storIODb = storIODb;
            this.object = object;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public PreparedDeleteObject<T> prepare() {
            return new PreparedDeleteObject<>(storIODb, object, mapFunc);
        }
    }
}
