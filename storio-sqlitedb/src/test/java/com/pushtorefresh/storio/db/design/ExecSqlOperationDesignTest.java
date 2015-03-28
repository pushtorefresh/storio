package com.pushtorefresh.storio.db.design;

import com.pushtorefresh.storio.db.query.RawQuery;

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
