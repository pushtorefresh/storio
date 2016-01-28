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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TweetWithUser that = (TweetWithUser) o;

        if (!tweet.equals(that.tweet)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = tweet.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "TweetWithUser{" +
                "tweet=" + tweet +
                ", user=" + user +
                '}';
    }
}
