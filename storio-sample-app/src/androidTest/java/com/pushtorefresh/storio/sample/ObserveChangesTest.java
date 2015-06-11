package com.pushtorefresh.storio.sample;

import android.support.test.runner.AndroidJUnit4;

import android.net.Uri;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.Changes;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.table.TweetContentResolverTableMeta;
import com.pushtorefresh.storio.test.AbstractEmissionChecker;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rx.Observable;

import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class ObserveChangesTest extends BaseTest {

    public class EmissionChecker extends AbstractEmissionChecker<Changes> {

        public EmissionChecker(@NonNull Queue<Changes> expected) {
            super(expected);
        }

        @Override
        @NonNull
        public Observable<Changes> newObservable() {
            return storIOContentResolver
                    .observeChangesOfUri(Uri.parse(TweetContentResolverTableMeta.CONTENT_URI));
        }
    }

    @Test
    public void insertEmission() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        final Queue<Changes> expectedTweets = new LinkedList<Changes>();
        for (int i = 0; i < tweets.size(); i++) {
            expectedTweets.add(Changes.newInstance(Uri.parse(TweetContentResolverTableMeta.CONTENT_URI)));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        emissionChecker.beginSubscription();

        putTweets(tweets);

        // Should receive changes of Users uri
        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void updateEmission() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        final Queue<Changes> expectedTweets = new LinkedList<Changes>();
        for (int i = 0; i < tweets.size(); i++) {
            expectedTweets.add(Changes.newInstance(Uri.parse(TweetContentResolverTableMeta.CONTENT_URI)));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expectedTweets);
        emissionChecker.beginSubscription();

        putTweets(tweets);

        emissionChecker.waitOne();

        final List<Tweet> updated = new ArrayList<Tweet>(tweets.size());
        for (Tweet tweet : tweets) {
            final Long id = tweet.id();
            assertNotNull(id);
            updated.add(Tweet.newTweet(id, tweet.author(), tweet.content()));
        }

        storIOContentResolver
                .put()
                .objects(Tweet.class, updated)
                .prepare()
                .executeAsBlocking();

        // Should receive changes of Users uri
        emissionChecker.waitAllAndUnsubscribe();
    }

    @Test
    public void deleteEmission() {
        final List<Tweet> tweets = TestFactory.newTweets(10);

        final Queue<Changes> expected = new LinkedList<Changes>();
        for (int i = 0; i < tweets.size(); i++) {
            expected.add(Changes.newInstance(Uri.parse(TweetContentResolverTableMeta.CONTENT_URI)));
        }

        final EmissionChecker emissionChecker = new EmissionChecker(expected);
        emissionChecker.beginSubscription();

        putTweets(tweets);

        for (int i = 0; i < tweets.size(); i++) {
            emissionChecker.waitOne();
        }

        deleteTweets(tweets);

        // Should receive changes of Tweets uri
        emissionChecker.waitAllAndUnsubscribe();
    }
}
