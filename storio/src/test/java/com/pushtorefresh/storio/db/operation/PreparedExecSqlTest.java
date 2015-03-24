package com.pushtorefresh.storio.db.operation;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.db.Changes;
import com.pushtorefresh.storio.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.db.query.RawQuery;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecSqlTest {

    static class Stub {

        private final StorIODb storIODb;
        private final StorIODb.Internal internal;
        private final RawQuery rawQuery;

        Stub() {
            storIODb = mock(StorIODb.class);
            internal = mock(StorIODb.Internal.class);
            rawQuery = new RawQuery.Builder().query("DROP TABLE users").build();

            when(storIODb.internal())
                    .thenReturn(internal);

            when(storIODb.execSql())
                    .thenReturn(new PreparedExecSql.Builder(storIODb));

        }

        @SuppressWarnings("unchecked") void verifyBehavior() {
            // storIODb.execSql() should be called once
            verify(storIODb, times(1)).execSql();

            // storIODb.internal.execSql() should be called once for ANY RawQuery
            verify(internal, times(1)).execSql(any(RawQuery.class));

            // storIODb.internal.execSql() should be called once for required RawQuery
            verify(internal, times(1)).execSql(rawQuery);

            // no notifications should occur
            verify(internal, times(0)).notifyAboutChanges(any(Changes.class));
        }
    }

    @Test public void blocking() {
        final Stub stub = new Stub();

        stub.storIODb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        stub.verifyBehavior();
    }

    @Test public void observable() {
        final Stub stub = new Stub();

        stub.storIODb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        stub.verifyBehavior();
    }
}
