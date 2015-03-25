package com.pushtorefresh.storio.db.design;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.Changes;
import com.pushtorefresh.storio.db.operation.delete.PreparedDelete;
import com.pushtorefresh.storio.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.db.operation.get.PreparedGet;
import com.pushtorefresh.storio.db.operation.put.PreparedPut;
import com.pushtorefresh.storio.db.query.DeleteQuery;
import com.pushtorefresh.storio.db.query.InsertQuery;
import com.pushtorefresh.storio.db.query.Query;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.query.UpdateQuery;

import java.util.Set;

import rx.Observable;

import static org.mockito.Mockito.mock;

class DesignTestStorIODbImpl extends StorIODb {

    @NonNull @Override
    public Observable<Changes> observeChangesInTables(@NonNull Set<String> tables) {
        return Observable.empty();
    }

    @NonNull private final Internal internal = new Internal() {

        @Override public void execSql(@NonNull RawQuery rawQuery) {
            // no impl
        }

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

        @Override public void notifyAboutChanges(@NonNull Changes changes) {
            // no impl
        }

        @Override public boolean transactionsSupported() {
            return false;
        }

        @Override public void beginTransaction() {
            // no impl
        }

        @Override public void setTransactionSuccessful() {
            // no impl
        }

        @Override public void endTransaction() {
            // no impl
        }
    };

    @NonNull @Override public PreparedExecSql.Builder execSql() {
        return new PreparedExecSql.Builder(this);
    }

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
