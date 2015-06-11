package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Observable;

@RunWith(AndroidJUnit4.class)
public class ObservableStreamTest extends BaseTest {

    private class EmissionChecker extends AbstractEmissionChecker<List<User>> {

        public EmissionChecker(@NonNull Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Observable<List<User>> newObservable() {
            return storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(UserTableMeta.QUERY_ALL)
                    .prepare()
                    .createObservable();
        }
    }

    @Override public void setUp() throws Exception {
        super.setUp();
        // initial users for all tests
        putUsersBlocking(10);
    }

    @Test
    public void insertEmission() {
        final List<User> initialUsers = getAllUsersBlocking();
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(initialUsers.size() + usersForInsert.size());

        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        // Should receive initial users firstly
        expectedUsers.add(initialUsers);
        // after then all = initial + inserted users
        expectedUsers.add(allUsers);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        emissionChecker.beginSubscription();

        putUsersBlocking(usersForInsert);

        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<User> initialUsers = getAllUsersBlocking();
        final List<User> updatedList = new ArrayList<User>(initialUsers.size());

        int count = 1;
        for (User user : initialUsers) {
            updatedList.add(User.newInstance(user.id(), "new_email" + count++));
        }

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(updatedList);
        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        emissionChecker.beginSubscription();

        storIOSQLite
                .put()
                .objects(User.class, updatedList)
                .prepare()
                .executeAsBlocking();

        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> allUsers = getAllUsersBlocking();
        final List<User> usersThatShouldBeSaved = new ArrayList<User>();
        final List<User> usersThatShouldBeDeleted = new ArrayList<User>();

        int pos = 0;
        for (User user : allUsers) {
            // will delete last part
            final boolean save = pos < 5;
            pos++;
            if (save) {
                usersThatShouldBeSaved.add(user);
            } else {
                usersThatShouldBeDeleted.add(user);
            }
        }

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(usersThatShouldBeSaved);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        emissionChecker.beginSubscription();

        deleteUsersBlocking(usersThatShouldBeDeleted);

        emissionChecker.waitAllAndUnsubscribe();
    }
}
