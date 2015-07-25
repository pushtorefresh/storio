package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGetCursor;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
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

        clearContentProvider();
    }

    private void clearContentProvider() {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>(
                asList(
                        Changes.newInstance(UserMeta.CONTENT_URI),
                        Changes.newInstance(TweetMeta.CONTENT_URI)
                )
        );

        final AbstractEmissionChecker<Changes> emissionChecker = new AbstractEmissionChecker<Changes>(expectedChanges) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return storIOContentResolver
                        .observeChangesOfUris(new HashSet<Uri>(asList(UserMeta.CONTENT_URI, TweetMeta.CONTENT_URI)))
                        .subscribe(new Action1<Changes>() {
                            @Override
                            public void call(Changes changes) {
                                onNextObtained(changes);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        // clearing before each test case
        storIOContentResolver
                .delete()
                .byQuery(UserMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        emissionChecker.awaitNextExpectedValue();

        storIOContentResolver
                .delete()
                .byQuery(TweetMeta.DELETE_QUERY_ALL)
                .prepare()
                .executeAsBlocking();

        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
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
    User insertUser() {
        return insertUser(TestFactory.newUser());
    }

    @NonNull
    User insertUser(@NonNull final User user) {
        return insertUsers(singletonList(user)).get(0);
    }

    @NonNull
    List<User> insertUsers(final int size) {
        return insertUsers(TestFactory.newUsers(size));
    }

    @NonNull
    List<User> insertUsers(@NonNull final List<User> users) {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        for (int i = 0; i < users.size(); i++) {
            // One change per insert
            expectedChanges.add(Changes.newInstance(UserMeta.CONTENT_URI));
        }

        final AbstractEmissionChecker<Changes> emissionChecker = new AbstractEmissionChecker<Changes>(expectedChanges) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return storIOContentResolver
                        .observeChangesOfUri(UserMeta.CONTENT_URI)
                        .subscribe(new Action1<Changes>() {
                            @Override
                            public void call(Changes changes) {
                                onNextObtained(changes);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        for (User user : users) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(user)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());

            emissionChecker.awaitNextExpectedValue();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();

        final List<User> storedUsers = storIOContentResolver
                .get()
                .listOfObjects(User.class)
                .withQuery(Query.builder()
                        .uri(UserMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(users, storedUsers);

        return users;
    }

    @NonNull
    DeleteResult deleteUser(@NonNull final User user) {
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

        return deleteResult;
    }

    void checkThatThereIsOnlyOneUserInStorage(@NonNull final User user) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(1, usersFromStorage.size());
        assertEquals(user, usersFromStorage.get(0));
    }

    void checkThatTheseUsersInStorage(@NonNull final List<User> users) {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertEquals(users.size(), usersFromStorage.size());
        assertEquals(users, usersFromStorage);
    }

    void checkThatThereAreNoUsersInStorage() {
        final List<User> usersFromStorage = getAllUsers();
        assertNotNull(usersFromStorage);
        assertTrue(usersFromStorage.isEmpty());
    }

    @NonNull
    List<Tweet> insertTweets(@NonNull List<Tweet> tweets) {
        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        for (int i = 0; i < tweets.size(); i++) {
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

        for (final Tweet tweet : tweets) {
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

        final List<Tweet> storedTweets = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .uri(TweetMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(tweets, storedTweets);

        return storedTweets;
    }

    @NonNull
    PreparedGetCursor createQueryWithNullResult() {
        final ContentResolver internalContentResolver = mock(ContentResolver.class);

        when(internalContentResolver
                .query(any(Uri.class),
                        any(String[].class),
                        anyString(),
                        any(String[].class),
                        anyString()))
                .thenReturn(null);

        final DefaultStorIOContentResolver defaultStorIOContentResolver = DefaultStorIOContentResolver.builder()
                .contentResolver(internalContentResolver)
                .addTypeMapping(User.class, ContentResolverTypeMapping.<User>builder()
                        .putResolver(UserMeta.PUT_RESOLVER)
                        .getResolver(UserMeta.GET_RESOLVER)
                        .deleteResolver(UserMeta.DELETE_RESOLVER)
                        .build())
                .build();

        return defaultStorIOContentResolver
                .get()
                .cursor()
                .withQuery(Query.builder()
                        .uri(mock(Uri.class))
                        .build())
                .prepare();
    }
}
