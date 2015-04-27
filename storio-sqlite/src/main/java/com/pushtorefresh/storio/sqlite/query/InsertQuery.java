package com.pushtorefresh.storio.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Insert query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}
 * <p/>
 * Instances of this class are Immutable
 */
public final class InsertQuery {

    /**
     * Table name
     */
    @NonNull
    public final String table;

    /**
     * Tricky-wiki hack for null columns in {@link android.database.sqlite.SQLiteDatabase}
     * <p/>
     * SQL doesn't allow inserting a completely empty row without naming at least one column name.
     * If your provided values are empty, no column names are known and an empty row can't be inserted.
     * If not set to null, the nullColumnHack parameter provides the name of nullable column name
     * to explicitly insert a NULL into in the case where your values is empty.
     */
    @Nullable
    public final String nullColumnHack;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.query.InsertQuery.Builder} instead of constructor
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

    @Override
    public String toString() {
        return "InsertQuery{" +
                "table='" + table + '\'' +
                ", nullColumnHack='" + nullColumnHack + '\'' +
                '}';
    }

    /**
     * Builder for {@link InsertQuery}
     */
    public static final class Builder {

        /**
         * Required: Specifies table name
         *
         * @param table non-null and not empty table name
         * @return builder
         */
        @NonNull
        public CompleteBuilder table(@NonNull String table) {
            checkNotEmpty(table, "Table name is null or empty");
            return new CompleteBuilder(table);
        }
    }

    /**
     * Compile-time safe part of builder for {@link DeleteQuery}
     */
    public static final class CompleteBuilder {

        @NonNull
        private final String table;

        private String nullColumnHack;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        /**
         * Optional: Specifies null column hack
         * <p/>
         * SQL doesn't allow inserting a completely empty row without naming at least one column name.
         * If your provided values are empty, no column names are known and an empty row can't be inserted.
         * If not set to null, the nullColumnHack parameter provides the name of nullable column name
         * to explicitly insert a NULL into in the case where your values is empty.
         * <p/>
         * Default value is <code>null</code>
         *
         * @param nullColumnHack optional null column hack
         * @return builder
         */
        @NonNull
        public CompleteBuilder nullColumnHack(@Nullable String nullColumnHack) {
            this.nullColumnHack = nullColumnHack;
            return this;
        }

        /**
         * Builds immutable instance of {@link InsertQuery}
         *
         * @return immutable instance of {@link InsertQuery}
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
