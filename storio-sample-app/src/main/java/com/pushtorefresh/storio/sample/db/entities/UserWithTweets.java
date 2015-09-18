package com.pushtorefresh.storio.sample.db.entities;

import android.support.annotation.NonNull;

import java.util.List;

import static java.util.Collections.unmodifiableList;

/**
 * Example of entity with linked sub-entities!
 *
 * It's a User with his tweets.
 *
 * Main idea of this example is to show you that
 * StorIO can solve ORM problems but still be a DAO.
 *
 * Moreover, we will write GetResolver in such manner
 * that you will be able to write use any Query with it,
 * but at the same time you could optimize any frequent case
 * with JOIN and other SQL things directly in GetResolver.
 */
public final class UserWithTweets {

    @NonNull
    private final User user;

    @NonNull
    private final List<Tweet> tweets;

    public UserWithTweets(@NonNull User user, @NonNull List<Tweet> tweets) {
        this.user = user;
        this.tweets = unmodifiableList(tweets); // We prefer immutable entities
    }

    @NonNull
    public User user() {
        return user;
    }

    @NonNull
    public List<Tweet> tweets() {
        return tweets;
    }
}
