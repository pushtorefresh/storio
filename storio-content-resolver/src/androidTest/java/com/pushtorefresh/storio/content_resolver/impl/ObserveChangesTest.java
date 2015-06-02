package com.pushtorefresh.storio.content_resolver.impl;

import android.support.test.runner.AndroidJUnit4;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

@RunWith(AndroidJUnit4.class)
public class ObserveChangesTest extends BaseTest {

    public class EmissionChecker extends AbstractEmissionChecker<Changes> {

        public EmissionChecker(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Subscription subscribe() {
            return storIOContentResolver
                    .observeChangesOfUri(Uri.parse(UserMeta.CONTENT_URI))
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
    @Ignore("ProviderTestCase2 does not call contentObserver")
    public void insertEmission() {
        final List<User> users = TestFactory.newUsers(10);

        final Queue<Changes> expectedUsers = new LinkedList<Changes>();
        expectedUsers.add(Changes.newInstance(Uri.parse(UserMeta.TABLE)));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        putUsers(users);

        // Should receive changes of Users uri
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    @Ignore("ProviderTestCase2 does not call contentObserver")
    public void updateEmission() {
        final List<User> users = putUsers(10);
        final List<User> updated = new ArrayList<User>(users.size());
        for (User user : users) {
            final Long id = user.id();
            assertNotNull(id);
            updated.add(User.newInstance(id, user.email()));
        }
        final Queue<Changes> expectedUsers = new LinkedList<Changes>();
        expectedUsers.add(Changes.newInstance(Uri.parse((UserMeta.CONTENT_URI))));

        final EmissionChecker emissionChecker = new EmissionChecker(expectedUsers);
        final Subscription subscription = emissionChecker.subscribe();

        storIOContentResolver
                .put()
                .objects(User.class, updated)
                .prepare()
                .executeAsBlocking();

        // Should receive changes of Users uri
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    @Ignore("ProviderTestCase2 does not call contentObserver")
    public void deleteEmission() {
        final List<User> users = putUsers(10);

        final Queue<Changes> expected = new LinkedList<Changes>();
        expected.add(Changes.newInstance(Uri.parse(UserMeta.CONTENT_URI)));

        final EmissionChecker emissionChecker = new EmissionChecker(expected);
        final Subscription subscription = emissionChecker.subscribe();

        deleteUsers(users);

        // Should receive changes of Users uri
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }
}
