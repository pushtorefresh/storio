package com.pushtorefresh.storio.contentresolver.test;

import android.support.annotation.NonNull;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operations.put.PutResult;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.provider.meta.TweetMeta;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
                    .observeChangesOfUri(TweetMeta.CONTENT_URI)
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
    public void shouldReceiveChangeAfterEachInsert() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        // Count of changes should be equals to count of inserted objects
        for (int i = 0, count = tweets.size(); i < count; i++) {
            expectedChanges.add(Changes.newInstance(TweetMeta.CONTENT_URI));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Subscription subscription = emissionChecker.subscribe();

        for (final Tweet tweet : tweets) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasInserted());

            emissionChecker.assertThatNextExpectedValueReceived();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void shouldReceiveChangeAfterEachUpdate() {
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

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        // Count of changes should be equals to count of updated objects
        for (int i = 0, count = tweets.size(); i < count; i++) {
            expectedChanges.add(Changes.newInstance(TweetMeta.CONTENT_URI));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Subscription subscription = emissionChecker.subscribe();

        for (final Tweet tweet : tweets) {
            final PutResult putResult = storIOContentResolver
                    .put()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertTrue(putResult.wasUpdated());

            emissionChecker.assertThatNextExpectedValueReceived();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }

    @Test
    public void shouldReceiveChangeAfterEachDelete() {
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

        final Queue<Changes> expectedChanges = new LinkedList<Changes>();

        // Count of changes should be equals to count of updated objects
        for (int i = 0, count = tweets.size(); i < count; i++) {
            expectedChanges.add(Changes.newInstance(TweetMeta.CONTENT_URI));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expectedChanges);
        final Subscription subscription = emissionChecker.subscribe();

        for (final Tweet tweet : tweets) {
            final DeleteResult deleteResult = storIOContentResolver
                    .delete()
                    .object(tweet)
                    .prepare()
                    .executeAsBlocking();

            assertEquals(1, deleteResult.numberOfRowsDeleted());

            emissionChecker.assertThatNextExpectedValueReceived();
        }

        emissionChecker.assertThatNoExpectedValuesLeft();
        subscription.unsubscribe();
    }
}
