package com.pushtorefresh.storio.sqlite.queries;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Insert query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p>
 * Instances of this class are immutable.
 */
public final class InsertQuery {

    @NonNull
    private final String table;

    @Nullable
    private final String nullColumnHack;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.queries.InsertQuery.Builder}
     * instead of constructor.
     */
    private InsertQuery(@NonNull String table, @Nullable String nullColumnHack) {
        this.table = table;
        this.nullColumnHack = nullColumnHack;
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
     * Gets tricky-wiki hack for {@code null} columns in
     * {@link android.database.sqlite.SQLiteDatabase}.
     * <p>
     * SQL doesn't allow inserting a completely empty row
     * without naming at least one column name. If your provided values
     * are empty, no column names are known and an empty row can't be
     * inserted. If not set to {@code null}, the {@code nullColumnHack}
     * parameter provides the name of nullable column name to explicitly
     * insert a {@code NULL} into in the case where your values is empty.
     *
     * @return nullable hack for {@code NULL} columns.
     */
    @Nullable
    public String nullColumnHack() {
        return nullColumnHack;
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

    @Override
    public String toString() {
        return "InsertQuery{" +
                "table='" + table + '\'' +
                ", nullColumnHack='" + nullColumnHack + '\'' +
                '}';
    }

    /**
     * Creates new builder for {@link InsertQuery}.
     *
     * @return non-null instance of {@link InsertQuery.Builder}.
     */
    @NonNull
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for {@link InsertQuery}.
     */
    public static final class Builder {

        /**
         * Please use {@link InsertQuery#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see InsertQuery#table()
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
        private String nullColumnHack;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        CompleteBuilder(@NonNull InsertQuery insertQuery) {
            this.table = insertQuery.table;
            this.nullColumnHack = insertQuery.nullColumnHack;
        }

        /**
         * Specifies table name.
         *
         * @param table non-null and not empty table name.
         * @return builder.
         * @see InsertQuery#table()
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            this.table = table;
            return this;
        }

        /**
         * Optional: Specifies {@code NULL} column hack.
         * <p>
         * SQL doesn't allow inserting a completely empty row without naming at least one column name.
         * If your provided values are empty, no column names are known and an empty row can't be inserted.
         * If not set to null, the nullColumnHack parameter provides the name of nullable column name
         * to explicitly insert a NULL into in the case where your values is empty.
         * <p>
         * Default value is {@code null}.
         *
         * @param nullColumnHack optional null column hack.
         * @return builder.
         * @see InsertQuery#nullColumnHack()
         */
        @NonNull
        public CompleteBuilder nullColumnHack(@Nullable String nullColumnHack) {
            this.nullColumnHack = nullColumnHack;
            return this;
        }

        /**
         * Builds immutable instance of {@link InsertQuery}.
         *
         * @return immutable instance of {@link InsertQuery}.
         */
        @NonNull
        public InsertQuery build() {
            return new InsertQuery(
                    table,
                    nullColumnHack
            );
        }
    }
}
