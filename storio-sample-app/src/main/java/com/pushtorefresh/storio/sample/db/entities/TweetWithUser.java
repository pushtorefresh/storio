package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;

/**
 * Example of entity with another entity linked together!
 *
 * But our Annotation Processor can not handle such things,
 * so we need to wrote our own PutResolver, GetResolver and DeleteResolver
 */
public class TweetWithUser {

    @NonNull
    private final Tweet tweet;

    @NonNull
    private final User user;

    public TweetWithUser(@NonNull Tweet tweet, @NonNull User user) {
        this.tweet = tweet;
        this.user = user;
    }

    @NonNull
    public Tweet tweet() {
        return tweet;
    }

    @NonNull
    public User user() {
        return user;
    }
}
