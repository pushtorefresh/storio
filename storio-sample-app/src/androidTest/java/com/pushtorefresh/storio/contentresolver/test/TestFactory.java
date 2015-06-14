package com.pushtorefresh.storio.contentresolver.test;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entity.Tweet;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public final class TestFactory {

    @NonNull
    private static final AtomicInteger TWEETS_COUNTER = new AtomicInteger(0);

    private TestFactory() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static Tweet newTweet() {
        return Tweet.newTweet((long) TWEETS_COUNTER.incrementAndGet(), "somebody", "some content");
    }

    @NonNull
    public static List<Tweet> newTweets(int quantity) {
        final List<Tweet> users = new ArrayList<Tweet>(quantity);

        for (int i = 0; i < quantity; i++) {
            users.add(newTweet());
        }

        return users;
    }
}
