package com.pushtorefresh.android.bamboostorage.unit_test.operation;

import com.pushtorefresh.android.bamboostorage.BambooStorage;
import com.pushtorefresh.android.bamboostorage.operation.exec_sql.PreparedExecSql;
import com.pushtorefresh.android.bamboostorage.query.RawQuery;
import com.pushtorefresh.android.bamboostorage.query.RawQueryBuilder;

import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySet;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PreparedExecSqlTest {

    static class Stub {

        private final BambooStorage bambooStorage;
        private final BambooStorage.Internal internal;
        private final RawQuery rawQuery;

        Stub() {
            bambooStorage = mock(BambooStorage.class);
            internal = mock(BambooStorage.Internal.class);
            rawQuery = new RawQueryBuilder().query("DROP TABLE users").build();

            when(bambooStorage.internal())
                    .thenReturn(internal);

            when(bambooStorage.execSql())
                    .thenReturn(new PreparedExecSql.Builder(bambooStorage));

        }

        @SuppressWarnings("unchecked") void verifyBehavior() {
            // bambooStorage.execSql() should be called once
            verify(bambooStorage, times(1)).execSql();

            // bambooStorage.internal.execSql() should be called once for ANY RawQuery
            verify(internal, times(1)).execSql(any(RawQuery.class));

            // bambooStorage.internal.execSql() should be called once for required RawQuery
            verify(internal, times(1)).execSql(rawQuery);

            // no notifications should occur
            verify(internal, times(0)).notifyAboutChanges(anySet());
        }
    }

    @Test public void blocking() {
        final Stub stub = new Stub();

        stub.bambooStorage
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .executeAsBlocking();

        stub.verifyBehavior();
    }

    @Test public void observable() {
        final Stub stub = new Stub();

        stub.bambooStorage
                .execSql()
                .withQuery(stub.rawQuery)
                .prepare()
                .createObservable()
                .toBlocking()
                .last();

        stub.verifyBehavior();
    }
}
