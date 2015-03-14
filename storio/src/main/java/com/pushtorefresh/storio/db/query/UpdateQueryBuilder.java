package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class UpdateQueryBuilder {

    private String table;
    private String where;
    private String[] whereArgs;

    @NonNull public UpdateQueryBuilder table(@NonNull String table) {
        this.table = table;
        return this;
    }

    @NonNull public UpdateQueryBuilder where(@Nullable String where) {
        this.where = where;
        return this;
    }

    @NonNull public UpdateQueryBuilder whereArgs(@Nullable String... whereArgs) {
        this.whereArgs = whereArgs;
        return this;
    }

    @NonNull public UpdateQuery build() {
        return new UpdateQuery(
                table,
                where,
                whereArgs
        );
    }
}
