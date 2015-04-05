package com.pushtorefresh.storio.sqlitedb.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.pushtorefresh.storio.util.Checks.checkNotEmpty;

/**
 * Insert query for {@link com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb}
 * <p/>
 * Instances of this class are Immutable
 */
public class InsertQuery {

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
     * Please use {@link com.pushtorefresh.storio.sqlitedb.query.InsertQuery.Builder} instead of constructor
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
    public static class Builder {

        private String table;
        private String nullColumnHack;

        /**
         * Required: Specifies table name
         *
         * @param table table name
         * @return builder
         */
        @NonNull
        public Builder table(@NonNull String table) {
            this.table = table;
            return this;
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
        public Builder nullColumnHack(@Nullable String nullColumnHack) {
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
            checkNotEmpty(table, "Please specify table name");

            return new InsertQuery(
                    table,
                    nullColumnHack
            );
        }
    }
}
