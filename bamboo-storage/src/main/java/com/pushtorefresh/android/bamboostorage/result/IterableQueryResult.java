package com.pushtorefresh.android.bamboostorage.result;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.wtf.Query;
import com.pushtorefresh.android.bamboostorage.wtf.StorableTypeParser;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

public class IterableQueryResult<T extends BambooStorableType> {

    @NonNull private final BambooStorage bambooStorage;
    @NonNull private final Class<T> type;
    @NonNull private final Query query;

    public IterableQueryResult(@NonNull BambooStorage bambooStorage, @NonNull Class<T> type, @NonNull
    Query query) {
        this.bambooStorage = bambooStorage;
        this.type = type;
        this.query = query;
    }

    @NonNull public Cursor asCursor() {
        return bambooStorage.getInternal().query(type, query.where, query.whereArgs, query.orderBy);
    }

    @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
    @NonNull public List<T> asList() {
        final Cursor cursor = asCursor();

        try {
            final List<T> list = new ArrayList<>(cursor.getCount());
            final StorableTypeParser<T> parser = bambooStorage.getParser(type);

            while (cursor.moveToNext()) {
                list.add(parser.parseFromCursor(cursor));
            }

            return list;
        } finally {
            cursor.close();
        }
    }

    @NonNull public Observable<T> asObservable() {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @SuppressWarnings("TryFinallyCanBeTryWithResources") // Min SDK :(
            @Override public void call(Subscriber<? super T> subscriber) {
                final Cursor cursor = asCursor();

                try {
                    final StorableTypeParser<T> parser = bambooStorage.getParser(type);

                    while (!subscriber.isUnsubscribed() && cursor.moveToNext()) {
                        subscriber.onNext(parser.parseFromCursor(cursor));
                    }
                } finally {
                    cursor.close();
                }

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }

    @NonNull public Observable<List<T>> asObservableList() {
        return Observable.create(new Observable.OnSubscribe<List<T>>() {
            @Override
            public void call(Subscriber<? super List<T>> subscriber) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(asList());
                    subscriber.onCompleted();
                }
            }
        });
    }
}
