package com.pushtorefresh.storio.sqlite.design;

import com.pushtorefresh.storio.sqlite.query.RawQuery;

import org.junit.Test;

import rx.Observable;

public class ExecSqlOperationDesignTest extends OperationDesignTest {

    @Test public void execSqlBlocking() {
        Void nothing = storIOSQLiteDb()
                .execSql()
                .withQuery(new RawQuery.Builder().query("ALTER TABLE users ...").build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void execSqlObservable() {
        Observable<Void> observable = storIOSQLiteDb()
                .execSql()
                .withQuery(new RawQuery.Builder().query("DROP TABLE users").build())
                .prepare()
                .createObservable();
    }
}
