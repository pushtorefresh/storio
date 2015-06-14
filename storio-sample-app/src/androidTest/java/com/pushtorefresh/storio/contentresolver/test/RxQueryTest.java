package com.pushtorefresh.storio.contentresolver.test;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.provider.meta.TweetMeta;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class RxQueryTest extends BaseTest {

    private class EmissionChecker extends AbstractEmissionChecker<List<Tweet>> {

        public EmissionChecker(@NonNull Queue<List<Tweet>> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Subscription subscribe() {
            return storIOContentResolver
                    .get()
                    .listOfObjects(Tweet.class)
                    .withQuery(Query.builder()
                            .uri(TweetMeta.CONTENT_URI)
                            .build())
                    .prepare()
                    .createObservable()
                    .subscribe(new Action1<List<Tweet>>() {
                        @Override
                        public void call(List<Tweet> tweets) {
                            onNextObtained(tweets);
                        }
                    });
        }
    }

    @Test
    public void queryShouldBeUpdatedAfterInsert() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        // First of all -> insert all tweets into the Content Provider
        for (final Tweet tweet : tweets) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());
        }

        final Queue<List<Tweet>> expectedTweets = new LinkedList<List<Tweet>>();

        // First emission: 10 tweets
        expectedTweets.add(tweets);

        final Tweet tweet = TestFactory.newTweet();

        final List<Tweet> tweetsAfterInsert = new ArrayList<Tweet>(tweets);
        tweetsAfterInsert.add(tweet);

        // Second emission: 11 tweets
        expectedTweets.add(tweetsAfterInsert);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        final Subscription subscription = emissionChecker.subscribe();

        // We should receive 10 tweets after subscription
        emissionChecker.assertThatNextExpectedValueReceived();

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweet)
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasInserted());

        // Then we should receive 11 tweets
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void queryShouldBeUpdatedAfterUpdate() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        // First of all -> insert all tweets into the Content Provider
        for (final Tweet tweet : tweets) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());
        }

        final Queue<List<Tweet>> expectedTweets = new LinkedList<List<Tweet>>();

        // First emission: 10 tweets
        expectedTweets.add(tweets);

        final List<Tweet> tweetsAfterUpdate = new ArrayList<Tweet>(tweets);

        final Tweet tweetToUpdate = tweetsAfterUpdate.get(0);
        tweetsAfterUpdate.set(0, Tweet.newTweet(tweetToUpdate.id(), "New author", "New Content"));

        // Second emission: 10 tweets, where 1 tweet updated
        expectedTweets.add(tweetsAfterUpdate);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        final Subscription subscription = emissionChecker.subscribe();

        // We should receive 10 tweets after subscription
        emissionChecker.assertThatNextExpectedValueReceived();

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweetsAfterUpdate.get(0))
                .prepare()
                .executeAsBlocking();

        assertTrue(putResult.wasUpdated());

        // Then we should receive 10 tweets after update, but first tweet should be changed
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void queryShouldBeUpdatedAfterDelete() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        // First of all -> insert all tweets into the Content Provider
        for (final Tweet tweet : tweets) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());
        }

        final Queue<List<Tweet>> expectedTweets = new LinkedList<List<Tweet>>();

        // First emission: 10 tweets
        expectedTweets.add(tweets);

        final List<Tweet> tweetsAfterDelete = new ArrayList<Tweet>(tweets);
        tweetsAfterDelete.remove(0);

        // Second emission: 9 tweets, first tweet should be deleted
        expectedTweets.add(tweetsAfterDelete);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        final Subscription subscription = emissionChecker.subscribe();

        // We should receive 10 tweets after subscription
        emissionChecker.assertThatNextExpectedValueReceived();

        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(tweets.get(0))
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        // Then we should receive 9 tweets after delete, first tweet should be deleted
        emissionChecker.assertThatNextExpectedValueReceived();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }
}
