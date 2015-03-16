package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InsertQuery {

    @NonNull public final String table;

    @Nullable public final String nullColumnHack;

    /**
     * Please use {@link com.pushtorefresh.storio.db.query.InsertQuery.Builder} instead of constructor
     */
    protected InsertQuery(@NonNull String table, @Nullable String nullColumnHack) {
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

    @Override public String toString() {
        return "InsertQuery{" +
                "table='" + table + '\'' +
                ", nullColumnHack='" + nullColumnHack + '\'' +
                '}';
    }

    public static class Builder {

        private String table;
        private String nullColumnHack;

        @NonNull public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder nullColumnHack(@Nullable String nullColumnHack) {
            this.nullColumnHack = nullColumnHack;
            return this;
        }

        @NonNull public InsertQuery build() {
            if (table == null || table.length() == 0) {
                throw new IllegalStateException("Please specify table name");
            }

            return new InsertQuery(
                    table,
                    nullColumnHack
            );
        }
    }
}
