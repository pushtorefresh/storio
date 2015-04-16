package com.pushtorefresh.storio.contentresolver.design;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Test class that represents an object stored in ContentProvider
 */
class Article {

    @Nullable
    private final Long id;

    @NonNull
    private final String title;

    private Article(@Nullable Long id, @NonNull String title) {
        this.id = id;
        this.title = title;
    }

    @NonNull
    public static Article newInstance(@Nullable Long id, @NonNull String title) {
        return new Article(id, title);
    }

    @Nullable
    public Long id() {
        return id;
    }

    @NonNull
    public String title() {
        return title;
    }
}
