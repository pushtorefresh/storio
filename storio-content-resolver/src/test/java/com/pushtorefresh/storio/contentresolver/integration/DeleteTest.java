package com.pushtorefresh.storio.contentresolver.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResults;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteTest extends IntegrationTest {

    @Test
    public void deleteOne() {
        final User user = insertUser();
        checkThatThereIsOnlyOneUserInStorage(user);

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserMeta.CONTENT_URI));

        final AbstractEmissionChecker<Changes> emissionChecker = new AbstractEmissionChecker<Changes>(expectedChanges) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return storIOContentResolver
                        .observeChangesOfUri(UserMeta.CONTENT_URI)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<Changes>() {
                            @Override
                            public void call(Changes changes) {
                                onNextObtained(changes);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        emissionChecker.awaitNextExpectedValue();
        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();

        checkThatThereAreNoUsersInStorage();
    }

    @Test
    public void deleteCollection() {
        final List<User> allUsers = insertUsers(10);

        final List<User> usersToDelete = new ArrayList<User>();

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        for (int i = 0; i < allUsers.size(); i += 2) {  // Delete every second user
            usersToDelete.add(allUsers.get(i));
            expectedChanges.add(Changes.newInstance(UserMeta.CONTENT_URI));
        }

        final AbstractEmissionChecker<Changes> emissionChecker = new AbstractEmissionChecker<Changes>(expectedChanges) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return storIOContentResolver
                        .observeChangesOfUri(UserMeta.CONTENT_URI)
                        .subscribeOn(Schedulers.io())
                        .subscribe(new Action1<Changes>() {
                            @Override
                            public void call(Changes changes) {
                                onNextObtained(changes);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        final DeleteResults<User> deleteResults = storIOContentResolver
                .delete()
                .objects(usersToDelete)
                .prepare()
                .executeAsBlocking();

        for (User user : usersToDelete) {
            assertTrue(deleteResults.wasDeleted(user));
            emissionChecker.awaitNextExpectedValue();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();

        final List<User> storedUsers = getAllUsers();

        assertNotNull(storedUsers);

        for (User user : allUsers) {
            final boolean shouldBeDeleted = usersToDelete.contains(user);

            // Check that we deleted what we going to.
            assertEquals(shouldBeDeleted, deleteResults.wasDeleted(user));

            // Check that everything that should be kept exist
            assertEquals(!shouldBeDeleted, storedUsers.contains(user));
        }
    }
}
