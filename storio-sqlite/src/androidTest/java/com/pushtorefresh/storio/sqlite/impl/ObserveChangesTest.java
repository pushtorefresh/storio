package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Observable;

public class ObserveChangesTest extends BaseTest {

    public class EmissionChecker extends AbstractEmissionChecker<Changes> {

        public EmissionChecker(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Observable<Changes> newObservable() {
            return storIOSQLite.observeChangesInTable(UserTableMeta.TABLE);
        }
    }

    @Test
    public void insertEmission() {
        final List<User> users = TestFactory.newUsers(10);

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);

        emissionChecker.beginSubscription();

        putUsersBlocking(users);

        // Should receive changes of Users table
        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void updateEmission() {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE));
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);

        emissionChecker.beginSubscription();

        final List<User> users = putUsersBlocking(10);
        final List<User> updated = new ArrayList<User>(users.size());

        for (User user : users) {
            updated.add(User.newInstance(user.id(), user.email()));
        }

        storIOSQLite
                .put()
                .objects(User.class, updated)
                .prepare()
                .executeAsBlocking();

        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void deleteEmission() {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>();
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE));
        expectedChanges.add(Changes.newInstance(UserTableMeta.TABLE));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        emissionChecker.beginSubscription();

        final List<User> users = putUsersBlocking(10);

        emissionChecker.waitOne();
        deleteUsersBlocking(users);

        emissionChecker.waitAllAndUnsubscribe();
    }
}
