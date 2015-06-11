package com.pushtorefresh.storio.sample;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;

import com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping;
import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResult;
import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResults;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResults;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.table.TweetContentResolverTableMeta;

import org.junit.Before;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

public abstract class BaseTest {

    @NonNull
    protected StorIOContentResolver storIOContentResolver;

    @Before
    public void setUp() throws Exception {

        storIOContentResolver = new DefaultStorIOContentResolver.Builder()
                .contentResolver(InstrumentationRegistry.getContext().getContentResolver())

                .addTypeMapping(Tweet.class, new ContentResolverTypeMapping.Builder<Tweet>()
                        .putResolver(TweetContentResolverTableMeta.PUT_RESOLVER)
                        .getResolver(TweetContentResolverTableMeta.GET_RESOLVER)
                        .deleteResolver(TweetContentResolverTableMeta.DELETE_RESOLVER)
                        .build())
                .build();

        // clearing before each test case
        storIOContentResolver
                .delete()
                .byQuery(TweetContentResolverTableMeta.DELETE_ALL)
                .prepare()
                .executeAsBlocking();
    }

    @Nullable
    List<Tweet> getAllTweets() {
        return storIOContentResolver
                .get()
                .listOfObjects(Tweet.class)
                .withQuery(new Query.Builder()
                        .uri(TweetContentResolverTableMeta.CONTENT_URI)
                        .build())
                .prepare()
                .executeAsBlocking();
    }

    @NonNull
    Tweet putTweet() {
        return putTweet(TestFactory.newTweet());
    }

    @NonNull
    Tweet putTweet(@NonNull final Tweet tweet) {
        final PutResult putResult = storIOContentResolver
                .put()
                .object(tweet)
                .prepare()
                .executeAsBlocking();

        assertNotNull(putResult);
        assertTrue(putResult.wasInserted());

        return tweet;
    }

    @NonNull
    List<Tweet> putTweets(final int size) {
        return putTweets(TestFactory.newTweets(size));
    }

    @NonNull
    List<Tweet> putTweets(@NonNull final List<Tweet> tweets) {
        final PutResults<Tweet> putResults = storIOContentResolver
                .put()
                .objects(Tweet.class, tweets)
                .prepare()
                .executeAsBlocking();

        assertEquals(tweets.size(), putResults.numberOfInserts());

        return tweets;
    }

    @NonNull DeleteResult deleteTweet(@NonNull final Tweet tweet) {
        final DeleteResult deleteResult = storIOContentResolver
                .delete()
                .object(tweet)
                .prepare()
                .executeAsBlocking();

        assertEquals(1, deleteResult.numberOfRowsDeleted());

        return deleteResult;
    }

    @NonNull DeleteResults<Tweet> deleteTweets(@NonNull final List<Tweet> tweets) {
        final DeleteResults<Tweet> deleteResults = storIOContentResolver
                .delete()
                .objects(Tweet.class, tweets)
                .prepare()
                .executeAsBlocking();

        for (Tweet tweet : tweets) {
            assertTrue(deleteResults.wasDeleted(tweet));
        }
        return deleteResults;
    }

    void oneTweetInStorageCheck(@NonNull final Tweet tweet) {
        final List<Tweet> tweetsFromStorage = getAllTweets();
        assertNotNull(tweetsFromStorage);
        assertEquals(1, tweetsFromStorage.size());
        assertEquals(tweet, tweetsFromStorage.get(0));
    }

    void tweetsInStorageCheck(@NonNull final List<Tweet> tweets) {
        final List<Tweet> tweetsFromStorage = getAllTweets();
        assertNotNull(tweetsFromStorage);
        assertEquals(tweets.size(), tweetsFromStorage.size());
        assertEquals(tweets, tweetsFromStorage);
    }

    void noTweetsInStorageCheck() {
        final List<Tweet> tweetsFromStorage = getAllTweets();
        assertNotNull(tweetsFromStorage);
        assertTrue(tweetsFromStorage.isEmpty());
    }
}
