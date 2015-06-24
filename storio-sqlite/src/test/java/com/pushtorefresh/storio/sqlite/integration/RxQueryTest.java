package com.pushtorefresh.storio.sqlite.integration;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.BuildConfig;
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

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RxQueryTest extends BaseTest {

    private class EmissionChecker extends AbstractEmissionChecker<List<User>> {

        public EmissionChecker(@NonNull Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Subscription subscribe() {
            return storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(UserTableMeta.QUERY_ALL)
                    .prepare()
                    .createObservable()
                    .subscribe(new Action1<List<User>>() {
                        @Override
                        public void call(List<User> users) {
                            onNextObtained(users);
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> initialUsers = putUsersBlocking(10);
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(initialUsers.size() + usersForInsert.size());

        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(allUsers);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        // Should receive initial users
        emissionChecker.assertThatNextExpectedValueReceived();

        putUsersBlocking(usersForInsert);

        // Should receive initial users + inserted users
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsersBlocking(10);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        final List<User> updatedList = new ArrayList<User>(users.size());

        int count = 1;
        for (User user : users) {
            updatedList.add(User.newInstance(user.id(), "new_email" + count++));
        }
        expectedUsers.add(users);
        expectedUsers.add(updatedList);
        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        // Should receive all users
        emissionChecker.assertThatNextExpectedValueReceived();

        storIOSQLite
                .put()
                .objects(updatedList)
                .prepare()
                .executeAsBlocking();

        // Should receive updated users
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> usersThatShouldBeSaved = TestFactory.newUsers(10);
        final List<User> usersThatShouldBeDeleted = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>();

        allUsers.addAll(usersThatShouldBeSaved);
        allUsers.addAll(usersThatShouldBeDeleted);

        putUsersBlocking(allUsers);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();

        expectedUsers.add(allUsers);
        expectedUsers.add(usersThatShouldBeSaved);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        // Should receive all users
        emissionChecker.assertThatNextExpectedValueReceived();

        deleteUsersBlocking(usersThatShouldBeDeleted);

        // Should receive users that should be saved
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }
}
