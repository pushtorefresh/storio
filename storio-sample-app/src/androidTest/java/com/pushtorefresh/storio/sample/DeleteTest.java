package com.pushtorefresh.storio.sample;

import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResults;
import com.pushtorefresh.storio.sample.db.entity.Tweet;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

@RunWith(AndroidJUnit4.class)
public class DeleteTest extends BaseTest {

    @Test
    public void deleteOne() {
        final Tweet tweet = putTweet();
        oneTweetInStorageCheck(tweet);
        deleteTweet(tweet);
        noTweetsInStorageCheck();
    }

    @Test
    public void deleteCollection() {
        final List<Tweet> allTweets = putTweets(10);

        final List<Tweet> tweetsToDelete = new ArrayList<Tweet>();

        for (int i = 0; i < allTweets.size(); i += 2) {  // Delete every second
            tweetsToDelete.add(allTweets.get(i));
        }

        final DeleteResults<Tweet> deleteResults = storIOContentResolver
                .delete()
                .objects(Tweet.class, tweetsToDelete)
                .prepare()
                .executeAsBlocking();

        final List<Tweet> existTweets = getAllTweets();

        assertNotNull(existTweets);

        for (Tweet tweet : allTweets) {
            final boolean shouldBeDeleted = tweetsToDelete.contains(tweet);

            // Check that we deleted what we going to.
            assertEquals(shouldBeDeleted, deleteResults.wasDeleted(tweet));

            // Check that everything that should be kept exist
            assertEquals(!shouldBeDeleted, existTweets.contains(tweet));
        }
    }
}
