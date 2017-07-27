package com.pushtorefresh.storio2.sqlite.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.PreparedDelete;
import com.pushtorefresh.storio2.sqlite.operations.execute.PreparedExecuteSQL;
import com.pushtorefresh.storio2.sqlite.operations.get.PreparedGet;
import com.pushtorefresh.storio2.sqlite.operations.put.PreparedPut;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio2.sqlite.queries.Query;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;
import com.pushtorefresh.storio2.sqlite.queries.UpdateQuery;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import rx.Observable;
import rx.Scheduler;

import static org.mockito.Mockito.mock;

class DesignTestStorIOSQLite extends StorIOSQLite {

    @NonNull
    private final Internal lowLevel = new Internal() {

        @Nullable
        @Override
        public <T> SQLiteTypeMapping<T> typeMapping(@NonNull Class<T> type) {
            // no impl
            return null;
        }

        @Override
        public void executeSQL(@NonNull RawQuery rawQuery) {
            // no impl
        }

        @NonNull
        @Override
        public Cursor rawQuery(@NonNull RawQuery rawQuery) {
            return mock(Cursor.class);
        }

        @NonNull
        @Override
        public Cursor query(@NonNull Query query) {
            return mock(Cursor.class);
        }

        @Override
        public long insert(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues) {
            return 0;
        }

        @Override
        public long insertWithOnConflict(@NonNull InsertQuery insertQuery, @NonNull ContentValues contentValues, int conflictAlgorithm) {
            return 0;
        }

        @Override
        public int update(@NonNull UpdateQuery updateQuery, @NonNull ContentValues contentValues) {
            return 0;
        }

        @Override
        public int delete(@NonNull DeleteQuery deleteQuery) {
            return 0;
        }

        @Override
        public void notifyAboutChanges(@NonNull Changes changes) {
            // no impl
        }

        @Override
        public void beginTransaction() {
            // no impl
        }

        @Override
        public void setTransactionSuccessful() {
            // no impl
        }

        @Override
        public void endTransaction() {
            // no impl
        }

        @NonNull
        @Override
        public SQLiteOpenHelper sqliteOpenHelper() {
            // not required in design test
            // noinspection ConstantConditions
            return null;
        }
    };

    @NonNull
    @Override
    public Observable<Changes> observeChangesInTables(@NonNull Set<String> tables) {
        return Observable.empty();
    }

    @NonNull
    @Override
    public Observable<Changes> observeChangesOfTags(@NonNull Set<String> tags) {
        return Observable.empty();
    }

    @Nullable
    @Override
    public Scheduler defaultScheduler() {
        return null;
    }

    @NonNull
    @Override
    public Observable<Changes> observeChanges() {
        return Observable.empty();
    }

    @NonNull
    @Override
    public PreparedExecuteSQL.Builder executeSQL() {
        return new PreparedExecuteSQL.Builder(this);
    }

    @NonNull
    @Override
    public PreparedGet.Builder get() {
        return new PreparedGet.Builder(this);
    }

    @NonNull
    @Override
    public PreparedPut.Builder put() {
        return new PreparedPut.Builder(this);
    }

    @NonNull
    @Override
    public PreparedDelete.Builder delete() {
        return new PreparedDelete.Builder(this);
    }

    @NonNull
    @Override
    public Internal internal() {
        return lowLevel;
    }

    @NonNull
    @Override
    public LowLevel lowLevel() {
        return lowLevel;
    }

    @NonNull
    @Override
    public List<Interceptor> interceptors() {
        return Collections.emptyList();
    }

    @Override
    public void close() throws IOException {
        // no impl
    }
}
