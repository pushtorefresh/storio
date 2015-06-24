package com.pushtorefresh.storio.contentresolver.integration;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResults;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteTest extends IntegrationTest {

    @Test
    public void deleteOne() {
        final User user = putUser();
        oneUserInStorageCheck(user);
        deleteUser(user);
        noUsersInStorageCheck();
    }

    @Test
    public void deleteCollection() {
        final List<User> allUsers = putUsers(10);

        final List<User> usersToDelete = new ArrayList<User>();

        for (int i = 0; i < allUsers.size(); i += 2) {  // Delete every second
            usersToDelete.add(allUsers.get(i));
        }

        final DeleteResults<User> deleteResults = storIOContentResolver
                .delete()
                .objects(usersToDelete)
                .prepare()
                .executeAsBlocking();

        final List<User> existUsers = getAllUsers();

        assertNotNull(existUsers);

        for (User user : allUsers) {
            final boolean shouldBeDeleted = usersToDelete.contains(user);

            // Check that we deleted what we going to.
            assertEquals(shouldBeDeleted, deleteResults.wasDeleted(user));

            // Check that everything that should be kept exist
            assertEquals(!shouldBeDeleted, existUsers.contains(user));
        }
    }
}
