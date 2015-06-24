package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResults;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public abstract class IntegrationTest {

    @NonNull // Initialized in setUp()
    protected StorIOContentResolver storIOContentResolver;

    @Before
    public void setUp() throws Exception {
        final ContentResolver contentResolver = RuntimeEnvironment.application.getContentResolver();

        final TestContentProvider testContentProvider = new TestContentProvider();
        testContentProvider.onCreate();

        ShadowContentResolver.registerProvider(TestContentProvider.AUTHORITY, testContentProvider);

        storIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(contentResolver)
                .addTypeMapping(Tweet.class, ContentResolverTypeMapping.<Tweet>builder()
                        .putResolver(TweetMeta.PUT_RESOLVER)
                        .getResolver(TweetMeta.GET_RESOLVER)
                        .deleteResolver(TweetMeta.DELETE_RESOLVER)
                        .build())
                .addTypeMapping(User.class, ContentResolverTypeMapping.<User>builder()
                        .putResolver(UserMeta.PUT_RESOLVER)
                        .getResolver(UserMeta.GET_RESOLVER)
                        .deleteResolver(UserMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // clearing before each test case
        storIOContentResolver
                .delete()
                .byQuery(UserMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        storIOContentResolver
                .delete()
                .byQuery(TweetMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @Nullable
    List<User> getAllUsers() {
        return storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    User putUser() {
        return putUser(TestFactory.newUser());
    }

    @NonNull
    User putUser(@NonNull final User user) {
        final PutResult putResult = storIOContentResolver
                .put()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertNotNull(putResult);
        assertTrue(putResult.wasInserted());

        return user;
    }

    @NonNull
    List<User> putUsers(final int size) {
        return putUsers(TestFactory.newUsers(size));
    }

    @NonNull
    List<User> putUsers(@NonNull final List<User> users) {
        final PutResults<User> putResults = storIOContentResolver
                .put()
                .objects(users)
                .prepare()
                .executeAsBlocking();

        assertEquals(users.size(), putResults.numberOfInserts());

        return users;
    }

    @NonNull
    DeleteResult deleteUser(@NonNull final User user) {
        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(user)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        return deleteResult;
    }

    void oneUserInStorageCheck(@NonNull final User user) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(1, usersFromStorage.size());
        assertEquals(user, usersFromStorage.get(0));
    }

    void usersInStorageCheck(@NonNull final List<User> users) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(users.size(), usersFromStorage.size());
        assertEquals(users, usersFromStorage);
    }

    void noUsersInStorageCheck() {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertTrue(usersFromStorage.isEmpty());
    }

    @NonNull
    List<Tweet> insertTweets(@NonNull List<Tweet> tweetsToInsert) {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        for (int i = 0; i < tweetsToInsert.size(); i++) {
            // One change per insert
            expectedChanges.add(Changes.newInstance(TweetMeta.CONTENT_URI));
        }

        final AbstractEmissionChecker<Changes> emissionChecker = new AbstractEmissionChecker<Changes>(expectedChanges) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return storIOContentResolver
                        .observeChangesOfUri(TweetMeta.CONTENT_URI)
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

        for (final Tweet tweet : tweetsToInsert) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());

            emissionChecker.awaitNextExpectedValue();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();

        final List<Tweet> tweets = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .uri(TweetMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(tweetsToInsert, tweets);



        return tweets;
    }
}
