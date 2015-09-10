package com.pushtorefresh.storio.sqlite.integration;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class ExecSQLTest extends BaseTest {

    @Test
    public void execSQLWithEmptyArgs() {
        // Should not throw exceptions!
        storIOSQLite
                .executeSQL()
                .withQuery(RawQuery.builder()
                        .query("DROP TABLE IF EXISTS no_such_table") // we don't want to really delete table
                        .build())
                .prepare()
                .executeAsBlocking();
    }
}
