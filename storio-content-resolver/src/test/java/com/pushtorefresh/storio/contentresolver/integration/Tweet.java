package com.pushtorefresh.storio.contentresolver.integration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

class Tweet {

    @Nullable
    private Long id; // Id field is not used for equals() and hashCode(), because it's db id, not logical id

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
    static Tweet newInstance(@Nullable Long id, @NonNull Long authorId, @NonNull String contentText) {
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

        if (!authorId.equals(tweet.authorId)) return false;
        return contentText.equals(tweet.contentText);
    }

    @Override
    public int hashCode() {
        int result = authorId.hashCode();
        result = 31 * result + contentText.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Tweet{" +
                "authorId=" + authorId +
                ", contentText='" + contentText + '\'' +
                ", id=" + id +
                '}';
    }
}
