package com.pushtorefresh.android.bamboostorage;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.android.bamboostorage.query.Query;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

public abstract class PreparedQuery {

    @NonNull protected final BambooStorage bambooStorage;
    @NonNull protected final Query query;

    public PreparedQuery(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
        this.bambooStorage = bambooStorage;
        this.query = query;
    }

    public static class Builder {

        @NonNull private final BambooStorage bambooStorage;
        @NonNull private final Query query;

        public Builder(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
            this.bambooStorage = bambooStorage;
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
            return new PreparedQueryWithResultAsCursor(bambooStorage, query);
        }

        @NonNull public <R> PreparedQueryWithResultsAsObjects<R> resultAsObjects(@NonNull Func1<Cursor, R> mapFunc) {
            validateFields();
            return new PreparedQueryWithResultsAsObjects<>(bambooStorage, query, mapFunc);
        }

        public static class PreparedQueryWithResultAsCursor extends PreparedQuery {

            PreparedQueryWithResultAsCursor(@NonNull BambooStorage bambooStorage, @NonNull Query query) {
                super(bambooStorage, query);
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

        public static class PreparedQueryWithResultsAsObjects<R> extends PreparedQuery {

            PreparedQueryWithResultsAsObjects(@NonNull BambooStorage bambooStorage, @NonNull Query query, @NonNull Func1<Cursor, R> mapFunc) {
                super(bambooStorage, query);
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
