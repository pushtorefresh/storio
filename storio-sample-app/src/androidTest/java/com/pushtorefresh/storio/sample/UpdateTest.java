package com.pushtorefresh.storio.sample;

import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResults;
import com.pushtorefresh.storio.sample.db.entity.Tweet;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UpdateTest extends BaseTest {

    @Test
    public void updateOne() {
        final Tweet tweetForInsert = TestFactory.newTweet();

        final PutResult insertResult = storIOContentResolver
                .put()
                .object(tweetForInsert)
                .prepare()
                .executeAsBlocking();

        assertTrue(insertResult.wasInserted());

        final Tweet tweetForUpdate = Tweet.newTweet(
                tweetForInsert.id(),    // using id of already inserted tweet
                "newAuthor@email.com",  // new value
                "new_tweet"
        );

        final PutResult updateResult = storIOContentResolver
                .put()
                .object(tweetForUpdate)
                .prepare()
                .executeAsBlocking();

        assertTrue(updateResult.wasUpdated());

        oneTweetInStorageCheck(tweetForUpdate);
    }

    @Test
    public void updateCollection() {
        final List<Tweet> tweetForInsert = putTweets(3);
        tweetsInStorageCheck(tweetForInsert);

        final List<Tweet> tweetsForUpdate = new ArrayList<Tweet>(tweetForInsert.size());

        for (int i = 0; i < tweetForInsert.size(); i++) {
            tweetsForUpdate.add(Tweet.newTweet(tweetForInsert.get(i).id(), "new" + i + "@email.com", "newContent" + i));
        }

        final PutResults<Tweet> updateResults = storIOContentResolver
                .put()
                .objects(Tweet.class, tweetsForUpdate)
                .prepare()
                .executeAsBlocking();

        assertEquals(tweetsForUpdate.size(), updateResults.numberOfUpdates());

        tweetsInStorageCheck(tweetsForUpdate);
    }
}
