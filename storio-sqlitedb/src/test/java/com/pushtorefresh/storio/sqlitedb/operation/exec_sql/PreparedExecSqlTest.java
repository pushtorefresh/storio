package com.pushtorefresh.storio.sqlitedb.operation.exec_sql;

import com.pushtorefresh.storio.sqlitedb.Changes;
import com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb;
import com.pushtorefresh.storio.sqlitedb.query.RawQuery;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecSqlTest {

    static class Stub {

        private final StorIOSQLiteDb storIOSQLiteDb;
        private final StorIOSQLiteDb.Internal internal;
        private final RawQuery rawQuery;

        Stub() {
            storIOSQLiteDb = mock(StorIOSQLiteDb.class);
            internal = mock(StorIOSQLiteDb.Internal.class);
            rawQuery = new RawQuery.Builder().query("DROP TABLE users").build();

            when(storIOSQLiteDb.internal())
                    .thenReturn(internal);

            when(storIOSQLiteDb.execSql())
                    .thenReturn(new PreparedExecSql.Builder(storIOSQLiteDb));

        }

        @SuppressWarnings("unchecked") void verifyBehavior() {
            // storIOSQLiteDb.execSql() should be called once
            verify(storIOSQLiteDb, times(1)).execSql();

            // storIOSQLiteDb.internal.execSql() should be called once for ANY RawQuery
            verify(internal, times(1)).execSql(any(RawQuery.class));

            // storIOSQLiteDb.internal.execSql() should be called once for required RawQuery
            verify(internal, times(1)).execSql(rawQuery);

            // no notifications should occur
            verify(internal, times(0)).notifyAboutChanges(any(Changes.class));
        }
    }

    @Test public void blocking() {
        final Stub stub = new Stub();

        stub.storIOSQLiteDb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        stub.verifyBehavior();
    }

    @Test public void observable() {
        final Stub stub = new Stub();

        stub.storIOSQLiteDb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        stub.verifyBehavior();
    }
}
