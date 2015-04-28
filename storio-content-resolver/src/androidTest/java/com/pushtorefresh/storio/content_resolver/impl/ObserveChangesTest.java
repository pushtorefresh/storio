package com.pushtorefresh.storio.content_resolver.impl;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ObserveChangesTest extends BaseSubscriptionTest {

    public class SubscribeStub extends BaseSubscribeStub<Changes> {

        public SubscribeStub(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        public Subscription subscribe() {
            return storIOContentResolver
                    .observeChangesOfUri(Uri.parse(UserMeta.CONTENT_URI))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Changes>() {

                        @Override
                        public void onError(Throwable e) {
                            fail("Error occurred: " + e);
                        }

                        @Override
                        public void onNext(Changes changes) {
                            onNextObtained(changes);
                        }

                        @Override
                        public void onCompleted() {
                        }
                    });
        }
    }

    @Test
    public void insertEmission() {
        final List<User> users = TestFactory.newUsers(10);

        final Queue<Changes> expectedUsers = new LinkedList<Changes>();
        expectedUsers.add(Changes.newInstance(Uri.parse(UserMeta.TABLE)));

        final SubscribeStub subscribeStub = new SubscribeStub(expectedUsers);
        final Subscription subscription = subscribeStub.subscribe();

        putUsers(users);

        assertTrue(subscribeStub.syncWait());

        subscription.unsubscribe();
    }

    @Test
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

        final SubscribeStub subscribeStub = new SubscribeStub(expectedUsers);
        final Subscription subscription = subscribeStub.subscribe();

        storIOContentResolver
                .put()
                .objects(User.class, updated)
                .prepare()
                .executeAsBlocking();

        assertTrue(subscribeStub.syncWait());

        subscription.unsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<User> users = putUsers(10);

        final Queue<Changes> expected = new LinkedList<Changes>();
        expected.add(Changes.newInstance(Uri.parse(UserMeta.CONTENT_URI)));

        final SubscribeStub subscribeStub = new SubscribeStub(expected);
        final Subscription subscription = subscribeStub.subscribe();

        deleteUsers(users);

        assertTrue(subscribeStub.syncWait());

        subscription.unsubscribe();
    }
}
