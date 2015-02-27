package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class DeleteQueryBuilder {

    private String table;
    private String where;
    private String[] whereArgs;

    @NonNull public DeleteQueryBuilder table(@NonNull String table) {
        this.table = table;
        return this;
    }

    @NonNull public DeleteQueryBuilder where(@Nullable String where) {
        this.where = where;
        return this;
    }

    @NonNull public DeleteQueryBuilder whereArgs(@Nullable String... whereArgs) {
        this.whereArgs = whereArgs;
        return this;
    }

    @NonNull public DeleteQuery build() {
        return new DeleteQuery(
                table,
                where,
                whereArgs
        );
    }
}
