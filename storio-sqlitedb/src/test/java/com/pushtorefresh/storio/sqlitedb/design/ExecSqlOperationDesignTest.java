package com.pushtorefresh.storio.sqlitedb.design;

import com.pushtorefresh.storio.sqlitedb.query.RawQuery;

import org.junit.Test;

import rx.Observable;

public class ExecSqlOperationDesignTest extends OperationDesignTest {

    @Test public void execSqlBlocking() {
        Void nothing = storIODb()
                .execSql()
                .withQuery(new RawQuery.Builder().query("ALTER TABLE users ...").build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void execSqlObservable() {
        Observable<Void> observable = storIODb()
                .execSql()
                .withQuery(new RawQuery.Builder().query("DROP TABLE users").build())
                .prepare()
                .createObservable();
    }
}
