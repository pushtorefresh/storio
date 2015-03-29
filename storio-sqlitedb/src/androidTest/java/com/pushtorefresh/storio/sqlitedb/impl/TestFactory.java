package com.pushtorefresh.storio.sqlitedb.impl;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TestFactory {

    private TestFactory() {
    }

    private static final AtomicInteger USERS_COUNTER = new AtomicInteger(0);
    private static final AtomicInteger TWEETS_COUNTER = new AtomicInteger(0);

    @NonNull public static User newUser() {
        return new User(null, "user" + USERS_COUNTER.incrementAndGet() + "@example.com");
    }

    @NonNull public static List<User> newUsers(int quantity) {
        final List<User> users = new ArrayList<>(quantity);

        for (int i = 0; i < quantity; i++) {
            users.add(newUser());
        }

        return users;
    }

    @NonNull public static Tweet newTweet(@NonNull Long userId) {
        return new Tweet(null, userId, "tweet_" + TWEETS_COUNTER.incrementAndGet());
    }
}
