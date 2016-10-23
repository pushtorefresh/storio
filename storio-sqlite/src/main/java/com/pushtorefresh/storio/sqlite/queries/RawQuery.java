package com.pushtorefresh.storio.sqlite.queries;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullList;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullSet;

/**
 * Raw SQL query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p>
 * Instances of this class are immutable.
 */
public final class RawQuery {

    @NonNull
    private final String query;

    @NonNull
    private final List<Object> args;

    @NonNull
    private final Set<String> affectsTables;

    @NonNull
    private final Set<String> observesTables;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.queries.RawQuery.Builder}
     * instead of constructor.
     */
    private RawQuery(@NonNull String query, @Nullable List<Object> args,
                     @Nullable Set<String> affectsTables, @Nullable Set<String> observesTables) {
        this.query = query;
        this.args = unmodifiableNonNullList(args);
        this.affectsTables = unmodifiableNonNullSet(affectsTables);
        this.observesTables = unmodifiableNonNullSet(observesTables);
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
     * @return non-null, immutable list of arguments for query.
     */
    @NonNull
    public List<Object> args() {
        return args;
    }

    /**
     * Gets optional immutable set of tables which will be affected by this query.
     * <p>
     * They will be used to notify observers of that tables.
     *
     * @return non-null, immutable set of tables, affected by this query.
     */
    @NonNull
    public Set<String> affectsTables() {
        return affectsTables;
    }

    /**
     * Gets optional immutable set of tables that should be observed by this query.
     * <p>
     * They will be used to observe changes of that tables and re-execute this query.
     *
     * @return non-null, immutable set of tables, that should be observed by this query.
     */
    @NonNull
    public Set<String> observesTables() {
        return observesTables;
    }

    /**
     * Returns the new builder that has the same content as this query.
     * It can be used to create new queries.
     *
     * @return non-null new instance of {@link CompleteBuilder} with content of this query.
     */
    @NonNull
    public CompleteBuilder toBuilder() {
        return new CompleteBuilder(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawQuery rawQuery = (RawQuery) o;

        if (!query.equals(rawQuery.query)) return false;
        if (!args.equals(rawQuery.args)) return false;
        if (!affectsTables.equals(rawQuery.affectsTables)) return false;
        return observesTables.equals(rawQuery.observesTables);
    }

    @Override
    public int hashCode() {
        int result = query.hashCode();
        result = 31 * result + args.hashCode();
        result = 31 * result + affectsTables.hashCode();
        result = 31 * result + observesTables.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RawQuery{" +
                "query='" + query + '\'' +
                ", args=" + args +
                ", affectsTables=" + affectsTables +
                ", observesTables=" + observesTables +
                '}';
    }

    /**
     * Creates new builder for {@link RawQuery}.
     *
     * @return non-null instance of {@link RawQuery.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link RawQuery}.
     */
    public static class Builder {

        /**
         * Please use {@link RawQuery#builder()} instead of this.
         */
        Builder() {
        }

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
    public static class CompleteBuilder {

        @NonNull
        private String query;

        private List<Object> args;

        private Set<String> affectsTables;

        private Set<String> observesTables;

        CompleteBuilder(@NonNull String query) {
            this.query = query;
        }

        CompleteBuilder(@NonNull RawQuery rawQuery) {
            this.query = rawQuery.query;
            this.args = rawQuery.args;
            this.affectsTables = rawQuery.affectsTables;
            this.observesTables = rawQuery.observesTables;
        }

        /**
         * Specifies SQL query.
         *
         * @param query SQL query.
         * @return builder.
         * @see RawQuery#query()
         */
        @NonNull
        public CompleteBuilder query(@NonNull String query) {
            checkNotEmpty(query, "Query is null or empty");
            this.query = query;
            return this;
        }

        /**
         * Optional: Specifies arguments for SQL query,
         * please use arguments to avoid SQL injections.
         * <p>
         * Default value is {@code null}.
         *
         * @param args arguments for SQL query.
         * @return builder.
         * @see RawQuery#args()
         */
        @NonNull
        public <T> CompleteBuilder args(@NonNull T... args) {
            this.args = unmodifiableNonNullList(args);
            return this;
        }

        /**
         * Optional: Specifies set of tables which will be affected by this query.
         * They will be used to notify observers of that tables.
         * <p>
         * Default value is {@code null}.
         *
         * @param tables set of tables which will be affected by this query.
         * @return builder.
         * @see RawQuery#affectsTables()
         */
        @NonNull
        public CompleteBuilder affectsTables(@NonNull String... tables) {
            if (affectsTables == null) {
                affectsTables = new HashSet<String>(tables.length);
            } else {
                affectsTables.clear();
            }

            Collections.addAll(affectsTables, tables);

            return this;
        }

        /**
         * Optional: Specifies set of tables which will be affected by this query.
         * They will be used to notify observers of that tables.
         * <p>
         * Default value is {@code null}.
         *
         * @param tables set of tables which will be affected by this query.
         * @return builder.
         * @see RawQuery#affectsTables()
         */
        @NonNull
        public CompleteBuilder affectsTables(@NonNull Collection<String> tables) {
            if (affectsTables == null) {
                affectsTables = new HashSet<String>(tables.size());
            } else {
                affectsTables.clear();
            }

            affectsTables.addAll(tables);

            return this;
        }

        /**
         * Optional: Specifies set of tables that should be observed by this query.
         * They will be used to re-execute query if one of the tables will be changed.
         * <p>
         * Default values is {@code null}.
         *
         * @param tables set of tables that should be observed by this query.
         * @return builder.
         * @see RawQuery#observesTables()
         */
        @NonNull
        public CompleteBuilder observesTables(@NonNull String... tables) {
            if (observesTables == null) {
                observesTables = new HashSet<String>(tables.length);
            } else {
                observesTables.clear();
            }

            Collections.addAll(this.observesTables, tables);

            return this;
        }

        /**
         * Optional: Specifies set of tables that should be observed by this query.
         * They will be used to re-execute query if one of the tables will be changed.
         * <p>
         * Default values is {@code null}.
         *
         * @param tables set of tables that should be observed by this query.
         * @return builder.
         * @see RawQuery#observesTables()
         */
        @NonNull
        public CompleteBuilder observesTables(@NonNull Collection<String> tables) {
            if (observesTables == null) {
                observesTables = new HashSet<String>(tables.size());
            } else {
                observesTables.clear();
            }

            observesTables.addAll(tables);

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
                    affectsTables,
                    observesTables
            );
        }
    }
}
