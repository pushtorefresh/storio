package com.pushtorefresh.storio.sample;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entity.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFactory {

    private static final AtomicInteger TWEETS_COUNTER = new AtomicInteger(0);

    private TestFactory() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    static Tweet newTweet() {
        return Tweet.newTweet(null, "author@example.com", "tweet" + TWEETS_COUNTER.incrementAndGet());
    }

    @NonNull
    static List<Tweet> newTweets(int quantity) {
        final List<Tweet> tweets = new ArrayList<Tweet>(quantity);

        for (int i = 0; i < quantity; i++) {
            tweets.add(newTweet());
        }

        return tweets;
    }
}
