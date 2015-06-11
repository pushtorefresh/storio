package com.pushtorefresh.storio.sample;

import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.contentresolver.operation.put.PutResult;
import com.pushtorefresh.storio.contentresolver.query.Query;
import com.pushtorefresh.storio.sample.db.entity.Tweet;
import com.pushtorefresh.storio.sample.db.table.TweetContentResolverTableMeta;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class InsertTest extends BaseTest {

    @Test
    public void insertOne() {
        final Tweet tweet = putTweet();
        oneTweetInStorageCheck(tweet);
    }

    @Test
    public void insertCollection() {
        final List<Tweet> tweets = putTweets(3);
        tweetsInStorageCheck(tweets);
    }

    @Test
    public void insertAndDeleteTwice() {
        final Tweet Tweet = TestFactory.newTweet();

        for (int i = 0; i < 2; i++) {
            putTweet(Tweet);
            oneTweetInStorageCheck(Tweet);
            deleteTweet(Tweet);
            noTweetsInStorageCheck();
        }
    }
}
