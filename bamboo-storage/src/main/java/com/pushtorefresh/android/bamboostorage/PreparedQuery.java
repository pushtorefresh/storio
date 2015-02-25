package com.pushtorefresh.android.bamboostorage;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.wtf.Query;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public abstract class PreparedQuery {

    @NonNull private final Query query;

    public PreparedQuery(@NonNull Query query) {
        this.query = query;
    }

    public static class Builder {

        @NonNull private final Query query;

        public Builder(@NonNull Query query) {
            this.query = query;
        }

        protected void validateFields() {
            //noinspection ConstantConditions
            if (query == null) {
                throw new IllegalStateException("Please set query object");
            }
        }

        @NonNull public PreparedQueryWithResultAsCursor resultAsCursor() {
            validateFields();
            return new PreparedQueryWithResultAsCursor(query);
        }

        @NonNull public <R extends BambooStorableType> PreparedQueryWithResultsAsObjects<R>
        resultAsObjects(@NonNull Class<R> type, @NonNull Func1<Cursor, R> mapFunc) {
            validateFields();
            return new PreparedQueryWithResultsAsObjects<>(query, mapFunc);
        }

        public static class PreparedQueryWithResultAsCursor extends PreparedQuery {

            PreparedQueryWithResultAsCursor(@NonNull Query query) {
                super(query);
            }

            @NonNull public Cursor executeAsBlocking() {
                return null;
            }

            @NonNull public Observable<Cursor> executeAsObservable() {
                return Observable.create(new Observable.OnSubscribe<Cursor>() {
                    @Override public void call(Subscriber<? super Cursor> subscriber) {

                    }
                });
            }
        }

        public static class PreparedQueryWithResultsAsObjects<R extends BambooStorableType> extends PreparedQuery {

            PreparedQueryWithResultsAsObjects(@NonNull Query query, @NonNull Func1<Cursor, R> mapFunc) {
                super(query);
            }

            @Nullable public List<R> executeAsBlocking() {
                return null;
            }

            @NonNull public Observable<List<R>> executeAsObservable() {
                return null;
            }
        }
    }
}
