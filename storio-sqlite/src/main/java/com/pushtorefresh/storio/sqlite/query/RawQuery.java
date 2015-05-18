package com.pushtorefresh.storio.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Raw SQL query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p/>
 * Instances of this class are immutable.
 */
public final class RawQuery {

    @NonNull
    private final String query;

    @Nullable
    private final List<String> args;

    @Nullable
    private final Set<String> affectedTables;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.query.RawQuery.Builder}
     * instead of constructor.
     */
    private RawQuery(@NonNull String query, @Nullable List<String> args, @Nullable Set<String> affectedTables) {
        this.query = query;
        this.args = Queries.listToUnmodifiable(args);
        this.affectedTables = affectedTables;
    }

    /**
     * Raw SQL query. Can contain {@code ?} for binding arguments.
     *
     * @return non-null SQL query.
     */
    @NonNull
    public String query() {
        return query;
    }

    /**
     * Gets optional immutable list of arguments for {@link #query()}.
     *
     * @return nullable immutable list of arguments for query.
     */
    @Nullable
    public List<String> args() {
        return args;
    }

    /**
     * Gets optional immutable set of tables which will be affected by this query.
     * <p/>
     * They will be used to notify observers of that tables.
     *
     * @return nullable immutable set of tables, affected by this query.
     */
    @Nullable
    public Set<String> affectedTables() {
        return affectedTables;
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
     * Builder for {@link RawQuery}.
     */
    public static final class Builder {

        /**
         * Required: Specifies SQL query.
         *
         * @param query SQL query.
         * @return builder.
         * @see RawQuery#query()
         */
        @NonNull
        public CompleteBuilder query(@NonNull String query) {
            checkNotEmpty(query, "Query is null or empty");
            return new CompleteBuilder(query);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final String query;

        private List<String> args;

        private Set<String> tables;

        CompleteBuilder(@NonNull String query) {
            this.query = query;
        }

        /**
         * Optional: Specifies arguments for SQL query,
         * please use arguments to avoid SQL injections.
         * <p/>
         * Passed objects will be immediately converted
         * to list of {@link String} via calling {@link Object#toString()}.
         * <p/>
         * Default value is {@code null}.
         *
         * @param args arguments fro SQL query.
         * @return builder.
         * @see RawQuery#args()
         */
        @NonNull
        public CompleteBuilder args(@NonNull Object... args) {
            this.args = Queries.varargsToList(args);
            return this;
        }

        /**
         * Optional: Specifies set of tables which will be affected by this query.
         * They will be used to notify observers of that tables.
         * <p/>
         * Default value is {@code null}.
         *
         * @param tables set of tables which will be affected by this query.
         * @return builder.
         * @see RawQuery#affectedTables()
         */
        @NonNull
        public CompleteBuilder affectedTables(@NonNull String... tables) {
            if (this.tables == null) {
                this.tables = new HashSet<String>(tables.length);
            }

            Collections.addAll(this.tables, tables);
            return this;
        }

        /**
         * Builds immutable instance of {@link RawQuery}.
         *
         * @return immutable instance of {@link RawQuery}.
         */
        @NonNull
        public RawQuery build() {
            return new RawQuery(
                    query,
                    args,
                    tables
            );
        }
    }
}
