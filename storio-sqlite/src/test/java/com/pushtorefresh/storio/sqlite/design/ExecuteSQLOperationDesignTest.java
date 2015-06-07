package com.pushtorefresh.storio.sqlite.design;

import com.pushtorefresh.storio.sqlite.query.RawQuery;

import org.junit.Test;

import rx.Observable;

public class ExecuteSQLOperationDesignTest extends OperationDesignTest {

    @Test
    public void execSqlBlocking() {
        Object nothing = storIOSQLite()
                .executeSQL()
                .withQuery(new RawQuery.Builder()
                        .query("ALTER TABLE users ...")
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @Test
    public void execSqlObservable() {
        Observable<Object> observable = storIOSQLite()
                .executeSQL()
                .withQuery(new RawQuery.Builder()
                        .query("DROP TABLE users")
                        .build())
                .prepare()
                .createObservable();
    }
}
