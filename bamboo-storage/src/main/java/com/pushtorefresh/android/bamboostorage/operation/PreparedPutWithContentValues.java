package com.pushtorefresh.android.bamboostorage.operation;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.exception.PutException;
import com.pushtorefresh.android.bamboostorage.query.InsertQuery;
import com.pushtorefresh.android.bamboostorage.query.UpdateQuery;

import rx.Observable;
import rx.Subscriber;

public class PreparedPutWithContentValues extends PreparedPut<Long> {

    @Nullable private final InsertQuery insertQuery;
    @Nullable private final UpdateQuery updateQuery;
    @NonNull private final ContentValues contentValues;

    PreparedPutWithContentValues(@NonNull BambooStorage bambooStorage, @NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
        super(bambooStorage);
        this.insertQuery = insertQuery;
        this.updateQuery = null;
        this.contentValues = contentValues;
    }

    PreparedPutWithContentValues(@NonNull BambooStorage bambooStorage, @NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
        super(bambooStorage);
        this.insertQuery = null;
        this.updateQuery = updateQuery;
        this.contentValues = contentValues;
    }

    @NonNull public Long executeAsBlocking() {
        if (insertQuery != null) {
            return bambooStorage.getInternal().insert(insertQuery, contentValues);
        } else if (updateQuery != null) {
            return (long) bambooStorage.getInternal().update(updateQuery, contentValues);
        } else {
            throw new PutException("Put can not be performed because no query is set, please specify InsertQuery or UpdateQuery");
        }
    }

    @NonNull public Observable<Long> createObservable() {
        return Observable.create(new Observable.OnSubscribe<Long>() {
            @Override public void call(Subscriber<? super Long> subscriber) {
                long result = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(result);
                    subscriber.onCompleted();
                }
            }
        });
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;
        private InsertQuery insertQuery;
        private UpdateQuery updateQuery;
        private ContentValues contentValues;

        public Builder(@NonNull BambooStorage bambooStorage) {
            this.bambooStorage = bambooStorage;
        }

        @NonNull public Builder query(@NonNull InsertQuery insertQuery) {
            this.insertQuery = insertQuery;
            return this;
        }

        @NonNull public Builder updateQuery(@NonNull UpdateQuery updateQuery) {
            this.updateQuery = updateQuery;
            return this;
        }

        @NonNull public Builder data(@NonNull ContentValues contentValues) {
            this.contentValues = contentValues;
            return this;
        }

        @NonNull public PreparedOperation<Long> prepare() {
            if (insertQuery != null) {
                return new PreparedPutWithContentValues(bambooStorage, insertQuery, contentValues);
            } else if (updateQuery != null) {
                return new PreparedPutWithContentValues(bambooStorage, updateQuery, contentValues);
            } else {
                throw new IllegalStateException("Please specify InsertQuery or UpdateQuery");
            }
        }
    }
}
