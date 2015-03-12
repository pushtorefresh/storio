package com.pushtorefresh.android.bamboostorage.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InsertQueryBuilder {

    private String table;
    private String nullColumnHack;

    @NonNull public InsertQueryBuilder table(@NonNull String table) {
        this.table = table;
        return this;
    }

    @NonNull public InsertQueryBuilder nullColumnHack(@Nullable String nullColumnHack) {
        this.nullColumnHack = nullColumnHack;
        return this;
    }

    @NonNull public InsertQuery build() {
        return new InsertQuery(
                table,
                nullColumnHack
        );
    }
}
