package com.pushtorefresh.android.bamboostorage.db.unit_test.design;

import com.pushtorefresh.android.bamboostorage.db.query.RawQueryBuilder;

import org.junit.Test;

import rx.Observable;

public class ExecSqlOperationDesignTest extends OperationDesignTest {

    @Test public void execSqlBlocking() {
        Void nothing = bambooStorageDb()
                .execSql()
                .withQuery(new RawQueryBuilder().query("ALTER TABLE users ...").build())
                .prepare()
                .executeAsBlocking();
    }

    @Test public void execSqlObservable() {
        Observable<Void> observable = bambooStorageDb()
                .execSql()
                .withQuery(new RawQueryBuilder().query("DROP TABLE users").build())
                .prepare()
                .createObservable();
    }
}
