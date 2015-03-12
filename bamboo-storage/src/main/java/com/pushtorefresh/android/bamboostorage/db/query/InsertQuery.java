package com.pushtorefresh.android.bamboostorage.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InsertQuery {

    @NonNull public final String table;

    @Nullable public final String nullColumnHack;

    public InsertQuery(@NonNull String table, @Nullable String nullColumnHack) {
        this.table = table;
        this.nullColumnHack = nullColumnHack;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InsertQuery that = (InsertQuery) o;

        if (nullColumnHack != null ? !nullColumnHack.equals(that.nullColumnHack) : that.nullColumnHack != null)
            return false;
        if (!table.equals(that.table)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (nullColumnHack != null ? nullColumnHack.hashCode() : 0);
        return result;
    }
}
