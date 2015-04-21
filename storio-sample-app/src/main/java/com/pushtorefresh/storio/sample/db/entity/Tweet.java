package com.pushtorefresh.storio.sample.db.entity;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotation.StorIOSQLiteType;

/**
 * Just for demonstration, real Tweet structure is more complex
 */
@StorIOSQLiteType(table = "tweets")
public class Tweet {

    // if object was not inserted into db, id will be null
    @Nullable
    @StorIOSQLiteColumn(name = "_id", key = true)
    Long id;

    @NonNull
    @StorIOSQLiteColumn(name = "author")
    String author;

    @NonNull
    @StorIOSQLiteColumn(name = "content")
    String content;

    private Tweet(@Nullable Long id, @NonNull String author, @NonNull String content) {
        this.id = id;
        this.author = author;
        this.content = content;
    }

    @NonNull
    public static Tweet newTweet(@Nullable Long id, @NonNull String author, @NonNull String content) {
        return new Tweet(id, author, content);
    }

    @NonNull
    public static Tweet newTweet(@NonNull String author, @NonNull String content) {
        return new Tweet(null, author, content);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String author() {
        return author;
    }

    @NonNull
    public String content() {
        return content;
    }

}
