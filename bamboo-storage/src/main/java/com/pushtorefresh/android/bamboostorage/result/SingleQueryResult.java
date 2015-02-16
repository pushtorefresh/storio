package com.pushtorefresh.android.bamboostorage.result;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.Query;

import rx.Observable;
import rx.Subscriber;

public class SingleQueryResult<T extends BambooStorableType> {

    @NonNull private final BambooStorage bambooStorage;
    @NonNull private final Class<T> type;
    @NonNull private final Query query;

    public SingleQueryResult(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type, @NonNull
    Query query) {
        this.bambooStorage = bambooStorage;
        this.type = type;
        this.query = query;
    }

    @NonNull public Cursor asCursor() {
        return bambooStorage.getInternal().query(type, query.where, query.whereArgs, query.orderBy);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @Nullable public T asObject() {
        final Cursor cursor = asCursor();

        try {
            if (cursor.moveToNext()) {
                return bambooStorage.getParser(type).parseFromCursor(cursor);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    @NonNull public Observable<T> asObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override public void call(Subscriber<? super T> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(asObject());
                    subscriber.onCompleted();
                }
            }
        });
    }
}
