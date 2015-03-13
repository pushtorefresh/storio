package com.pushtorefresh.storio.db.unit_test.operation;

import com.pushtorefresh.storio.db.BambooStorageDb;
import com.pushtorefresh.storio.db.operation.Changes;
import com.pushtorefresh.storio.db.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.storio.db.query.RawQuery;
import com.pushtorefresh.storio.db.query.RawQueryBuilder;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecSqlTest {

    static class Stub {

        private final BambooStorageDb bambooStorageDb;
        private final BambooStorageDb.Internal internal;
        private final RawQuery rawQuery;

        Stub() {
            bambooStorageDb = mock(BambooStorageDb.class);
            internal = mock(BambooStorageDb.Internal.class);
            rawQuery = new RawQueryBuilder().query("DROP TABLE users").build();

            when(bambooStorageDb.internal())
                    .thenReturn(internal);

            when(bambooStorageDb.execSql())
                    .thenReturn(new PreparedExecSql.Builder(bambooStorageDb));

        }

        @SuppressWarnings("unchecked") void verifyBehavior() {
            // bambooStorageDb.execSql() should be called once
            verify(bambooStorageDb, times(1)).execSql();

            // bambooStorageDb.internal.execSql() should be called once for ANY RawQuery
            verify(internal, times(1)).execSql(any(RawQuery.class));

            // bambooStorageDb.internal.execSql() should be called once for required RawQuery
            verify(internal, times(1)).execSql(rawQuery);

            // no notifications should occur
            verify(internal, times(0)).notifyAboutChanges(any(Changes.class));
        }
    }

    @Test public void blocking() {
        final Stub stub = new Stub();

        stub.bambooStorageDb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        stub.verifyBehavior();
    }

    @Test public void observable() {
        final Stub stub = new Stub();

        stub.bambooStorageDb
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        stub.verifyBehavior();
    }
}
