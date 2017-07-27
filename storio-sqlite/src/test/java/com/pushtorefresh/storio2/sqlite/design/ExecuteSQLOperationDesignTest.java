package com.pushtorefresh.storio2.sqlite.design;

import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import org.junit.Test;

import rx.Observable;

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
    public void execSqlObservable() {
        Observable<Object> observable = storIOSQLite()
                .executeSQL()
                .withQuery(RawQuery.builder()
                        .query("DROP TABLE users")
                        .build())
                .prepare()
                .asRxObservable();
    }
}
