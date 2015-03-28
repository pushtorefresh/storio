package com.pushtorefresh.storio.sqlitedb.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Immutable Raw SQL Query
 */
public class RawQuery {

    /**
     * SQL query
     */
    @NonNull
    public final String query;

    /**
     * Optional list of arguments for {@link #query}
     */
    @Nullable
    public final List<String> args;

    /**
     * Optional set of tables which will be changed by this query
     * They will be used to notify observers of that tables
     */
    @Nullable
    public final Set<String> affectedTables;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlitedb.query.RawQuery.Builder} instead of constructor
     */
    protected RawQuery(@NonNull String query, @Nullable List<String> args, @Nullable Set<String> affectedTables) {
        this.query = query;
        this.args = QueryUtil.listToUnmodifiable(args);
        this.affectedTables = affectedTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawQuery rawQuery = (RawQuery) o;

        if (!query.equals(rawQuery.query)) return false;
        if (args != null ? !args.equals(rawQuery.args) : rawQuery.args != null) return false;
        return !(affectedTables != null ? !affectedTables.equals(rawQuery.affectedTables) : rawQuery.affectedTables != null);

    }

    @Override
    public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + (args != null ? args.hashCode() : 0);
        result = 31 * result + (affectedTables != null ? affectedTables.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RawQuery{" +
                "query='" + query + '\'' +
                ", args=" + args +
                ", affectedTables=" + affectedTables +
                '}';
    }

    /**
     * Builder for {@link RawQuery}
     */
    public static class Builder {

        private String query;
        private List<String> args;
        private Set<String> tables;

        /**
         * Specifies SQL query
         *
         * @param query SQL query
         * @return builder
         */
        @NonNull
        public Builder query(@NonNull String query) {
            this.query = query;
            return this;
        }

        /**
         * Optional: specifies arguments for SQL query,
         * please use arguments to avoid SQL injections
         *
         * @param args arguments fro SQL query
         * @return builder
         */
        @NonNull
        public Builder args(@NonNull String... args) {
            this.args = QueryUtil.varargsToList(args);
            return this;
        }

        /**
         * Optional: specifies set of tables which will be affected by this query.
         * They will be used to notify observers of that tables
         *
         * @param tables set of tables which will be affected by this query
         * @return builder
         */
        @NonNull
        public Builder affectedTables(@NonNull String... tables) {
            if (this.tables == null) {
                this.tables = new HashSet<>(tables.length);
            }

            Collections.addAll(this.tables, tables);
            return this;
        }

        /**
         * Builds Raw Query
         *
         * @return immutable instance of {@link RawQuery}
         */
        @NonNull
        public RawQuery build() {
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
