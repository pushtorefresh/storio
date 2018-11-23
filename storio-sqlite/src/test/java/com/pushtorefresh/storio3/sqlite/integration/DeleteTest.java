package com.pushtorefresh.storio3.sqlite.integration;

import android.database.Cursor;

import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import androidx.sqlite.db.SupportSQLiteQueryBuilder;

import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteTest extends BaseTest {

    @Test
    public void deleteOne() {
        final User user = putUserBlocking();

        final Cursor cursorAfterInsert = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());

        assertThat(cursorAfterInsert.getCount()).isEqualTo(1);
        cursorAfterInsert.close();

        deleteUserBlocking(user);

        final Cursor cursorAfterDelete = db.query(SupportSQLiteQueryBuilder.builder(UserTableMeta.TABLE).create());
        assertThat(cursorAfterDelete.getCount()).isEqualTo(0);
        cursorAfterDelete.close();
    }

    @Test
    public void deleteCollection() {
        final List<User> allUsers = putUsersBlocking(10);

        final List<User> usersToDelete = new ArrayList<User>();

        for (int i = 0; i < allUsers.size(); i += 2) {  // Delete every second user
            usersToDelete.add(allUsers.get(i));
        }

        final DeleteResults<User> deleteResults = storIOSQLite
                .delete()
                .objects(usersToDelete)
                .prepare()
                .executeAsBlocking();

        final List<User> usersAfterDelete = getAllUsersBlocking();

        assertThat(usersAfterDelete).hasSize(allUsers.size() / 2);

        for (User user : allUsers) {
            final boolean shouldBeDeleted = usersToDelete.contains(user);

            // Check that we deleted what we going to.
            assertThat(deleteResults.wasDeleted(user)).isEqualTo(shouldBeDeleted);

            // Check that we didn't delete users that we didn't want to
            assertThat(usersAfterDelete.contains(user)).isEqualTo(!shouldBeDeleted);
        }
    }
}
