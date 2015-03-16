package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.db.StorIODb;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class RawQuery {

    @NonNull  public final String query;

    @Nullable public final String[] args;

    /**
     * Set of tables which are participated in {@link #query},
     * they can be used for Reactive Streams in {@link StorIODb#get()} operation
     */
    @Nullable public final Set<String> tables;

    /**
     * Please use {@link com.pushtorefresh.storio.db.query.RawQuery.Builder} instead of constructor
     */
    protected RawQuery(@NonNull String query, @Nullable String[] args, @Nullable Set<String> tables) {
        this.query = query;
        this.args = args;
        this.tables = tables;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawQuery rawQuery = (RawQuery) o;

        if (!Arrays.equals(args, rawQuery.args)) return false;
        if (!query.equals(rawQuery.query)) return false;
        if (tables != null ? !tables.equals(rawQuery.tables) : rawQuery.tables != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + (args != null ? Arrays.hashCode(args) : 0);
        result = 31 * result + (tables != null ? tables.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "RawQuery{" +
                "query='" + query + '\'' +
                ", args=" + Arrays.toString(args) +
                ", tables=" + tables +
                '}';
    }

    public static class Builder {

        private String query;
        private String[] args;
        private Set<String> tables;

        @NonNull public Builder query(@NonNull String query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder args(@NonNull String... args) {
            this.args = args;
            return this;
        }

        @NonNull public Builder tables(@NonNull String... tables) {
            if (this.tables == null) {
                this.tables = new HashSet<>(tables.length);
            }

            Collections.addAll(this.tables, tables);
            return this;
        }

        @NonNull public RawQuery build() {
            if (query == null || query.length() == 0) {
                throw new IllegalStateException("Please specify query string");
            }

            return new RawQuery(
                    query,
                    args,
                    tables
            );
        }
    }
}
