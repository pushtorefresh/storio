package com.pushtorefresh.storio3.sqlite.design;

import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import org.junit.Test;

import io.reactivex.Flowable;

import static io.reactivex.BackpressureStrategy.MISSING;

public class ExecuteSQLOperationDesignTest extends OperationDesignTest {

    @Test
    public void execSqlBlocking() {
        Object nothing = storIOSQLite()
                .executeSQL()
                .withQuery(RawQuery.builder()
                        .query("ALTER TABLE users ...")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void execSqlAsRxFlowable() {
        Flowable<Object> flowable = storIOSQLite()
                .executeSQL()
                .withQuery(RawQuery.builder()
                        .query("DROP TABLE users")
                        .build())
                .prepare()
                .asRxFlowable(MISSING);
    }
}
