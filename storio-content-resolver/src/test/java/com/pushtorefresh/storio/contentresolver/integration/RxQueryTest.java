package com.pushtorefresh.storio.contentresolver.integration;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.get.PreparedGetCursor;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.contentresolver.queries.Query;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.observers.TestSubscriber;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class RxQueryTest extends IntegrationTest {

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
        // First of all -> insert some tweets into the Content Provider
        final List<Tweet> tweets = insertTweets(TestFactory.newTweets(10));

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
        emissionChecker.awaitNextExpectedValue();

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweet)
                .prepare()
                .executeAsBlocking();

        assertThat(putResult.wasInserted()).isTrue();

        // Then we should receive 11 tweets
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void queryShouldBeUpdatedAfterUpdate() {
        // First of all -> insert some tweets into the Content Provider
        final List<Tweet> tweets = insertTweets(TestFactory.newTweets(10));

        final Queue<List<Tweet>> expectedTweets = new LinkedList<List<Tweet>>();

        // First emission: 10 tweets
        expectedTweets.add(tweets);

        final List<Tweet> tweetsAfterUpdate = new ArrayList<Tweet>(tweets);

        final Tweet tweetToUpdate = tweetsAfterUpdate.get(0);
        tweetsAfterUpdate.set(0, Tweet.newInstance(tweetToUpdate.id(), 1L, "New Content"));

        // Second emission: 10 tweets, where 1 tweet updated
        expectedTweets.add(tweetsAfterUpdate);

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        final Subscription subscription = emissionChecker.subscribe();

        // We should receive 10 tweets after subscription
        emissionChecker.awaitNextExpectedValue();

        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweetsAfterUpdate.get(0))
                .prepare()
                .executeAsBlocking();

        assertThat(putResult.wasUpdated()).isTrue();

        // Then we should receive 10 tweets after update, but first tweet should be changed
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void queryShouldBeUpdatedAfterDelete() {
        // First of all -> insert some tweets into the Content Provider
        final List<Tweet> tweets = insertTweets(TestFactory.newTweets(10));

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
        emissionChecker.awaitNextExpectedValue();

        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(tweets.get(0))
                .prepare()
                .executeAsBlocking();

        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(1);

        // Then we should receive 9 tweets after delete, first tweet should be deleted
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void shouldThrowExceptionIfCursorNullObservable() {
        final PreparedGetCursor queryWithNullResult = createQueryWithNullResult();

        final TestSubscriber<Cursor> testSubscriber = new TestSubscriber<Cursor>();

        queryWithNullResult
                .createObservable()
                .subscribe(testSubscriber);

        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoValues();

        Throwable expected = testSubscriber.getOnErrorEvents().get(0);
        assertThat(expected).isInstanceOf(StorIOException.class);
        assertThat(expected.getCause())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("Cursor returned by content provider is null");
    }
}
