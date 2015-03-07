package com.pushtorefresh.android.bamboostorage.unit_test.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.delete.PreparedDelete;
import com.pushtorefresh.android.bamboostorage.operation.get.PreparedGet;
import com.pushtorefresh.android.bamboostorage.operation.put.PreparedPut;
import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;
import com.pushtorefresh.android.bamboostorage.query.InsertQuery;
import com.pushtorefresh.android.bamboostorage.query.Query;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;
import com.pushtorefresh.android.bamboostorage.query.UpdateQuery;

import java.util.Set;

import rx.Observable;

import static org.mockito.Mockito.mock;

public class DesignTestBambooStorageImpl implements BambooStorage {

    @NonNull @Override
    public Observable<String> subscribeOnChanges(@NonNull Set<String> tables) {
        return Observable.empty();
    }

    @NonNull private final Internal internal = new Internal() {

        @NonNull @Override public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return mock(Cursor.class);
        }

        @NonNull @Override public Cursor query(@NonNull Query query) {
            return mock(Cursor.class);
        }

        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return 0;
        }

        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return 0;
        }

        @Override public int delete(@NonNull DeleteQuery deleteQuery) {
            return 0;
        }

        @Override public void notifyAboutChangeInTable(@NonNull String table) {

        }
    };

    @NonNull @Override public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull @Override public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull @Override public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    @NonNull @Override public Internal internal() {
        return internal;
    }
}
