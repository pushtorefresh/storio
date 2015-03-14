package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RawQueryBuilder {

    private String query;
    private String[] args;
    private Set<String> tables;

    @NonNull public RawQueryBuilder query(@NonNull String query) {
        this.query = query;
        return this;
    }

    @NonNull public RawQueryBuilder args(@NonNull String... args) {
        this.args = args;
        return this;
    }

    @NonNull public RawQueryBuilder tables(@NonNull String... tables) {
        if (this.tables == null) {
            this.tables = new HashSet<>(tables.length);
        }

        Collections.addAll(this.tables, tables);
        return this;
    }

    @NonNull public RawQuery build() {
        return new RawQuery(
                query,
                args,
                tables
        );
    }
}
