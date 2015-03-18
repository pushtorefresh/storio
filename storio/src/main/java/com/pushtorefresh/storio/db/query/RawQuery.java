package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.db.StorIODb;
import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RawQuery {

    @NonNull  public final String query;

    @Nullable public final List<String> args;

    /**
     * Set of tables which are participated in {@link #query},
     * they can be used for Reactive Streams in {@link StorIODb#get()} operation
     */
    @Nullable public final Set<String> tables;

    /**
     * Please use {@link com.pushtorefresh.storio.db.query.RawQuery.Builder} instead of constructor
     */
    protected RawQuery(@NonNull String query, @Nullable List<String> args, @Nullable Set<String> tables) {
        this.query = query;
        this.args = QueryUtil.listToUnmodifiable(args);
        this.tables = tables;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawQuery rawQuery = (RawQuery) o;

        if (!query.equals(rawQuery.query)) return false;
        if (args != null ? !args.equals(rawQuery.args) : rawQuery.args != null) return false;
        return !(tables != null ? !tables.equals(rawQuery.tables) : rawQuery.tables != null);

    }

    @Override public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (tables != null ? tables.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "RawQuery{" +
                "query='" + query + '\'' +
                ", args=" + args +
                ", tables=" + tables +
                '}';
    }

    public static class Builder {

        private String query;
        private List<String> args;
        private Set<String> tables;

        @NonNull public Builder query(@NonNull String query) {
            this.query = query;
            return this;
        }

        @NonNull public Builder args(@NonNull String... args) {
            this.args = QueryUtil.varargsToList(args);
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
