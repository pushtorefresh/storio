package com.pushtorefresh.storio.contentresolver.integration;

import android.content.ContentResolver;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResults;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Before;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.shadows.ShadowContentResolver;

import java.util.List;

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
    List<Tweet> putTweets(@NonNull List<Tweet> tweetsToPut) {
        for (final Tweet tweet : tweetsToPut) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());
        }

        final List<Tweet> tweets = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .uri(TweetMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertEquals(tweetsToPut, tweets);

        return tweets;
    }
}
