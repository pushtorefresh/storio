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

    PreparedDeleteObject(@NonNull StorIODb storIODb, @NonNull T object, @NonNull MapFunc<T, DeleteQuery> mapFunc, @NonNull DeleteResolver deleteResolver) {
        super(storIODb, deleteResolver);
        this.object = object;
        this.mapFunc = mapFunc;
    }

    @NonNull @Override public DeleteResult executeAsBlocking() {
        final StorIODb.Internal internal = storIODb.internal();
        final DeleteQuery deleteQuery = mapFunc.map(object);

        final int numberOfDeletedRows = deleteResolver.performDelete(storIODb, deleteQuery);

        internal.notifyAboutChanges(new Changes(deleteQuery.table));

        return DeleteResult.newDeleteResult(numberOfDeletedRows, Collections.singleton(deleteQuery.table));
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
        private DeleteResolver deleteResolver;

        public Builder(@NonNull StorIODb storIODb, @NonNull T object) {
            this.storIODb = storIODb;
            this.object = object;
        }

        @NonNull public Builder<T> withMapFunc(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            this.mapFunc = mapFunc;
            return this;
        }

        @NonNull public Builder<T> withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        @NonNull public PreparedDeleteObject<T> prepare() {
            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteObject<>(
                    storIODb,
                    object,
                    mapFunc,
                    deleteResolver
            );
        }
    }
}
