package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class ObservableStreamTest extends BaseSubscriptionTest {

    public class EmissionChecker extends BaseEmissionChecker<List<User>> {

        public EmissionChecker(@NonNull Queue<List<User>> expected) {
            super(expected);
        }

        @Override
        public Subscription subscribe() {
            return storIOSQLite
                    .get()
                    .listOfObjects(User.class)
                    .withQuery(UserTableMeta.QUERY_ALL)
                    .prepare()
                    .createObservableStream()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<User>>() {

                        @Override
                        public void onError(Throwable e) {
                            fail("Error occurred: " + e);
                        }

                        @Override
                        public void onNext(List<User> users) {
                            onNextObtained(users);
                        }

                        @Override
                        public void onCompleted() {
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> initialUsers = putUsers(10);
        final List<User> usersForInsert = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(initialUsers.size() + usersForInsert.size());

        allUsers.addAll(initialUsers);
        allUsers.addAll(usersForInsert);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(initialUsers);
        expectedUsers.add(allUsers);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        putUsers(usersForInsert);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsers(10);

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

        storIOSQLite
                .put()
                .objects(User.class, updatedList)
                .prepare()
                .executeAsBlocking();

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> usersForSave = TestFactory.newUsers(10);
        final List<User> usersForRemove = TestFactory.newUsers(10);
        final List<User> allUsers = new ArrayList<User>(usersForSave.size() + usersForRemove.size());

        allUsers.addAll(usersForSave);
        allUsers.addAll(usersForRemove);

        putUsers(allUsers);

        final Queue<List<User>> expectedUsers = new LinkedList<List<User>>();
        expectedUsers.add(allUsers);
        expectedUsers.add(usersForSave);
        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        deleteUsers(usersForRemove);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }
}
