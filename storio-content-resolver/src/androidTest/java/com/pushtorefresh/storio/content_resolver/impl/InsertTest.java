package com.pushtorefresh.storio.content_resolver.impl;

import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.Query;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class InsertTest extends BaseTest {

    @Test public void insertOne() {
        final User user = putUser();
        oneUserInStorageCheck(user);
    }

    @Test public void insertCollection() {
        final List<User> users = putUsers(3);
        usersInStorageCheck(users);
    }

    @Test public void insertAndDeleteTwice() {
        final User user = TestFactory.newUser();

        for (int i = 0; i < 2; i++) {
            putUser(user);
            oneUserInStorageCheck(user);
            deleteUser(user);
            noUsersInStorageCheck();
        }
    }

    /**
     * Check inserting item with custom internal id field name
     */
    @Test public void insertCollectionWithCustomId() {
        final List<User> users = putUsers(1);
        final User user = users.get(0);

        assertNotNull(user.getId());

        final Tweet tweet = TestFactory.newTweet(user.getId());

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweet)
                .withMapFunc(Tweet.MAP_TO_CONTENT_VALUES)
                .withPutResolver(Tweet.PUT_RESOLVER)
                .prepare()
                .executeAsBlocking();

        assertNotNull(putResult);
        assertTrue(putResult.wasInserted());

        final List<Tweet> tweetsFromStorage = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(new Query.Builder()
                        .uri(Tweet.CONTENT_URI)
                        .build())
                .withMapFunc(Tweet.MAP_FROM_CURSOR)
                .prepare()
                .executeAsBlocking();

        assertNotNull(tweetsFromStorage);
        assertEquals(1, tweetsFromStorage.size());
        assertEquals(tweet, tweetsFromStorage.get(0));
    }
}
