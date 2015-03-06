package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class RawQueryBuilder {

    private String query;
    private String[] args;

    @NonNull public RawQueryBuilder query(@NonNull String query) {
        this.query = query;
        return this;
    }

    @NonNull public RawQueryBuilder args(@Nullable String... args) {
        this.args = args;
        return this;
    }

    @NonNull public RawQuery build() {
        return new RawQuery(
                query,
                args
        );
    }
}
