package com.pushtorefresh.storio.sqlite.integration;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFactory {

    private static final AtomicInteger USERS_COUNTER = new AtomicInteger(0);
    private static final AtomicInteger TWEETS_COUNTER = new AtomicInteger(0);
    private TestFactory() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static User newUser() {
        return User.newInstance(null, "user" + USERS_COUNTER.incrementAndGet() + "@example.com");
    }

    @NonNull
    public static List<User> newUsers(int quantity) {
        final List<User> users = new ArrayList<User>(quantity);

        for (int i = 0; i < quantity; i++) {
            users.add(newUser());
        }

        return users;
    }

    @NonNull
    public static Tweet newTweet(@NonNull Long userId) {
        return Tweet.newInstance(null, userId, "tweet_" + TWEETS_COUNTER.incrementAndGet());
    }
}
