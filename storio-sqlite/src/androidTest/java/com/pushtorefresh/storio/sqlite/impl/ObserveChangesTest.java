package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static junit.framework.Assert.assertTrue;

public class ObserveChangesTest extends BaseSubscriptionTest {

    public class EmissionChecker extends BaseEmissionChecker<Changes> {

        public EmissionChecker(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        public Subscription subscribe() {
            return storIOSQLite
                    .observeChangesInTable(UserTableMeta.TABLE)
                    .subscribeOn(Schedulers.io())
                    .subscribe(new Action1<Changes>() {
                        @Override
                        public void call(Changes changes) {
                            onNextObtained(changes);
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> users = TestFactory.newUsers(10);

        final Queue<Changes> expectedUsers = new LinkedList<Changes>();
        expectedUsers.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        putUsers(users);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<User> users = putUsers(10);
        final List<User> updated = new ArrayList<User>(users.size());
        for (User user : users) {
            updated.add(User.newInstance(user.id(), user.email()));
        }
        final Queue<Changes> expectedUsers = new LinkedList<Changes>();
        expectedUsers.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        storIOSQLite
                .put()
                .objects(User.class, updated)
                .prepare()
                .executeAsBlocking();

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> users = putUsers(10);

        final Queue<Changes> expected = new LinkedList<Changes>();
        expected.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expected);
        final Subscription subscription = emissionChecker.subscribe();

        deleteUsers(users);

        assertTrue(emissionChecker.syncWait());

        subscription.unsubscribe();
    }
}
