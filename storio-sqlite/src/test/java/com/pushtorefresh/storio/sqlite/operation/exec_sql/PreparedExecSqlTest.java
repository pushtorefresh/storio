package com.pushtorefresh.storio.sqlite.operation.exec_sql;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.query.RawQuery;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecSqlTest {

    @Test
    public void blocking() {
        final Stub stub = new Stub();

        stub.storIOSQLite
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        stub.verifyBehavior();
    }

    @Test
    public void observable() {
        final Stub stub = new Stub();

        stub.storIOSQLite
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        stub.verifyBehavior();
    }

    static class Stub {

        private final StorIOSQLite storIOSQLite;
        private final StorIOSQLite.Internal internal;
        private final RawQuery rawQuery;

        Stub() {
            storIOSQLite = mock(StorIOSQLite.class);
            internal = mock(StorIOSQLite.Internal.class);
            rawQuery = new RawQuery.Builder().query("DROP TABLE users").build();

            when(storIOSQLite.internal())
                    .thenReturn(internal);

            when(storIOSQLite.execSql())
                    .thenReturn(new PreparedExecSql.Builder(storIOSQLite));

        }

        @SuppressWarnings("unchecked")
        void verifyBehavior() {
            // storIOSQLite.execSql() should be called once
            verify(storIOSQLite, times(1)).execSql();

            // storIOSQLite.internal.execSql() should be called once for ANY RawQuery
            verify(internal, times(1)).execSql(any(RawQuery.class));

            // storIOSQLite.internal.execSql() should be called once for required RawQuery
            verify(internal, times(1)).execSql(rawQuery);

            // no notifications should occur
            verify(internal, times(0)).notifyAboutChanges(any(Changes.class));
        }
    }
}
