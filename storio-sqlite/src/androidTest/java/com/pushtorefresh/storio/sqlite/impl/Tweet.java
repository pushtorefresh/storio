package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Test class with custom internal id field name.
 */
public class Tweet {

    @Nullable
    private volatile Long id;

    @NonNull
    private final Long authorId;

    @NonNull
    private final String contentText;

    private Tweet(@Nullable Long id, @NonNull Long authorId, @NonNull String contentText) {
        this.id = id;
        this.authorId = authorId;
        this.contentText = contentText;
    }

    @NonNull
    public static Tweet newInstance(@Nullable Long id, @NonNull Long authorId, @NonNull String contentText) {
        return new Tweet(id, authorId, contentText);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public Long authorId() {
        return authorId;
    }

    @NonNull
    public String contentText() {
        return contentText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tweet tweet = (Tweet) o;

        if (id != null ? !id.equals(tweet.id) : tweet.id != null) return false;
        if (!authorId.equals(tweet.authorId)) return false;
        return contentText.equals(tweet.contentText);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + authorId.hashCode();
        result = 31 * result + contentText.hashCode();
        return result;
    }
}
