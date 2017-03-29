package com.pushtorefresh.storio.sqlite.integration;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
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

    @Test
    public void shouldPassArgsAsObjects() {
        final User user = putUserBlocking();

        assertThat(user.id()).isNotNull();
        //noinspection ConstantConditions
        final long uid = user.id();

        final String query = "UPDATE " + UserTableMeta.TABLE
                + " SET " + UserTableMeta.COLUMN_ID + " = MIN(" + UserTableMeta.COLUMN_ID + ", ?)";

        storIOSQLite
                .executeSQL()
                .withQuery(
                        RawQuery.builder()
                                .query(query)
                                .args(uid - 1)  // as integer is less, as string is greater
                                .build())
                .prepare()
                .executeAsBlocking();

        List<User> users = getAllUsersBlocking();

        assertThat(users.size()).isEqualTo(1);

        // Was updated, because (uid - 1) passed as object, not string, and (uid - 1) < uid.
        assertThat(users.get(0).id()).isEqualTo(uid - 1);
    }
}
