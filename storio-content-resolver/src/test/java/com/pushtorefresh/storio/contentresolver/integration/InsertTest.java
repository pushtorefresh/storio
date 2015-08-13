package com.pushtorefresh.storio.contentresolver.integration;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.Query;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class InsertTest extends IntegrationTest {

    @Test
    public void insertOne() {
        final User user = insertUser();
        checkThatThereIsOnlyOneUserInStorage(user);
    }

    @Test
    public void insertCollection() {
        final List<User> users = insertUsers(3);
        checkThatTheseUsersInStorage(users);
    }

    @Test
    public void insertAndDeleteTwice() {
        final User user = TestFactory.newUser();

        for (int i = 0; i < 2; i++) {
            insertUser(user);
            checkThatThereIsOnlyOneUserInStorage(user);
            deleteUser(user);
            checkThatThereAreNoUsersInStorage();
        }
    }

    /**
     * Check inserting item with custom internal id field name
     */
    @Test
    public void insertCollectionWithCustomId() {
        final List<User> users = insertUsers(1);
        final User user = users.get(0);

        assertThat(user.id()).isNotNull();

        final Tweet tweet = TestFactory.newTweet();

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweet)
                .prepare()
                .executeAsBlocking();

        assertThat(putResult).isNotNull();
        assertThat(putResult.wasInserted()).isTrue();

        final List<Tweet> tweetsFromStorage = storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(Query.builder()
                        .uri(TweetMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(tweetsFromStorage).isNotNull();
        assertThat(tweetsFromStorage.size()).isEqualTo(1);
        assertThat(tweetsFromStorage.get(0)).isEqualTo(tweet);
    }
}
