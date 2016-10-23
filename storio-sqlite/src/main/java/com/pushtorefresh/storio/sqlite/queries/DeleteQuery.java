package com.pushtorefresh.storio.sqlite.queries;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.InternalQueries.nonNullString;
import static com.pushtorefresh.storio.internal.InternalQueries.unmodifiableNonNullListOfStrings;

/**
 * Delete query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p>
 * Instances of this class are immutable.
 */
public final class DeleteQuery {

    @NonNull
    private final String table;

    @NonNull
    private final String where;

    @NonNull
    private final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.queries.DeleteQuery.Builder}
     * instead of constructor.
     */
    private DeleteQuery(@NonNull String table, @Nullable String where, @Nullable List<String> whereArgs) {
        this.table = table;
        this.where = nonNullString(where);
        this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
    }

    /**
     * Gets table name.
     *
     * @return non-null table name.
     */
    @NonNull
    public String table() {
        return table;
    }

    /**
     * Gets {@code WHERE} clause.
     * <p>
     * Optional filter declaring which rows to return.
     * <p>
     * Formatted as an SQL {@code WHERE} clause (excluding the {@code WHERE} itself).
     * <p>
     * If empty — Query will delete all rows for the given table.
     *
     * @return non-null {@code WHERE} clause.
     */
    @NonNull
    public String where() {
        return where;
    }

    /**
     * Gets optional immutable list of arguments for {@link #where()} clause.
     *
     * @return non-null, immutable list of arguments for {@code WHERE} clause.
     */
    @NonNull
    public List<String> whereArgs() {
        return whereArgs;
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

        DeleteQuery that = (DeleteQuery) o;

        if (!table.equals(that.table)) return false;
        if (!where.equals(that.where)) return false;
        return whereArgs.equals(that.whereArgs);
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + where.hashCode();
        result = 31 * result + whereArgs.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeleteQuery{" +
                "table='" + table + '\'' +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                '}';
    }

    /**
     * Creates new builder for {@link DeleteQuery}.
     *
     * @return non-null instance of {@link DeleteQuery.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link DeleteQuery}.
     */
    public static final class Builder {

        /**
         * Please use {@link DeleteQuery#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see DeleteQuery#table()
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            return new CompleteBuilder(table);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}.
     */
    public static final class CompleteBuilder {

        @NonNull
        private String table;

        @Nullable
        private String where;

        @Nullable
        private List<String> whereArgs;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        CompleteBuilder(@NonNull DeleteQuery deleteQuery) {
            this.table = deleteQuery.table;
            this.where = deleteQuery.where;
            this.whereArgs = deleteQuery.whereArgs;
        }

        /**
         * Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see DeleteQuery#table()
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            this.table = table;
            return this;
        }

        /**
         * Optional: Specifies {@code WHERE} clause.
         * <p>
         * Optional filter declaring which rows to return.
         * <p>
         * Formatted as an SQL WHERE clause (excluding the WHERE itself).
         * <p>
         * Passing null will DELETE all rows for the given table.
         * <p>
         * Default value is {@code null}.
         *
         * @param where {@code WHERE} clause.
         * @return builder.
         * @see DeleteQuery#where()
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for {@code WHERE} clause.
         * <p>
         * Passed objects will be immediately converted
         * to list of {@link String} via calling {@link Object#toString()}.
         * <p>
         * Default value is {@code null}.
         *
         * @param whereArgs list of arguments for {@code WHERE} clause.
         * @return builder.
         * @see DeleteQuery#whereArgs()
         */
        @NonNull
        public <T> CompleteBuilder whereArgs(@Nullable T... whereArgs) {
            this.whereArgs = unmodifiableNonNullListOfStrings(whereArgs);
            return this;
        }

        /**
         * Builds immutable instance of {@link DeleteQuery}.
         *
         * @return immutable instance of {@link DeleteQuery}.
         */
        @NonNull
        public DeleteQuery build() {
            if (where == null && whereArgs != null && !whereArgs.isEmpty()) {
                throw new IllegalStateException("You can not use whereArgs without where clause");
            }

            return new DeleteQuery(
                    table,
                    where,
                    whereArgs
            );
        }
    }
}