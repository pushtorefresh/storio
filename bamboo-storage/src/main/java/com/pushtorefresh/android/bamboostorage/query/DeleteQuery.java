package com.pushtorefresh.android.bamboostorage.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public class DeleteQuery {

    @NonNull public final String table;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    public DeleteQuery(@NonNull String table, @Nullable String where, @Nullable String[] whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = whereArgs;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteQuery that = (DeleteQuery) o;

        if (!table.equals(that.table)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        if (!Arrays.equals(whereArgs, that.whereArgs)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? Arrays.hashCode(whereArgs) : 0);
        return result;
    }
}