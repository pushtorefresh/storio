package com.pushtorefresh.storio.sqlite.integration;

import android.database.Cursor;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteTest extends BaseTest {

    @Test
    public void deleteOne() {
        final User user = putUserBlocking();

        final Cursor cursorAfterInsert = db.query(UserTableMeta.TABLE, null, null, null, null, null, null);
        assertEquals(1, cursorAfterInsert.getCount());
        cursorAfterInsert.close();

        deleteUserBlocking(user);

        final Cursor cursorAfterDelete = db.query(UserTableMeta.TABLE, null, null, null, null, null, null);
        assertEquals(0, cursorAfterDelete.getCount());
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

        assertEquals(allUsers.size() / 2, usersAfterDelete.size());

        for (User user : allUsers) {
            final boolean shouldBeDeleted = usersToDelete.contains(user);

            // Check that we deleted what we going to.
            assertEquals(shouldBeDeleted, deleteResults.wasDeleted(user));

            // Check that we didn't delete users that we didn't want to
            assertEquals(!shouldBeDeleted, usersAfterDelete.contains(user));
        }
    }
}
