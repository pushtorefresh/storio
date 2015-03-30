package com.pushtorefresh.storio.sqlitedb.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

/**
 * Delete query for {@link com.pushtorefresh.storio.sqlitedb.StorIOSQLiteDb}
 * <p/>
 * Instances of this class are Immutable
 */
public class DeleteQuery {

    /**
     * Table name
     */
    @NonNull
    public final String table;

    /**
     * Optional filter declaring which rows to return
     * <p/>
     * Formatted as an SQL WHERE clause (excluding the WHERE itself).
     * <p/>
     * Passing null will return all rows for the given table
     */
    @Nullable
    public final String where;

    /**
     * Optional immutable list of arguments for {@link #where} clause
     */
    @Nullable
    public final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlitedb.query.DeleteQuery.Builder} instead of constructor
     */
    protected DeleteQuery(@NonNull String table, @Nullable String where, @Nullable List<String> whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteQuery that = (DeleteQuery) o;

        if (!table.equals(that.table)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        return !(whereArgs != null ? !whereArgs.equals(that.whereArgs) : that.whereArgs != null);

    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
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
     * Builder for {@link DeleteQuery}
     */
    public static class Builder {

        private String table;
        private String where;
        private List<String> whereArgs;

        /**
         * Required: Specifies table name
         * <p/>
         * Default value is <code>null</code>
         *
         * @param table non-null table name
         * @return builder
         */
        @NonNull
        public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        /**
         * Optional: Specifies where clause
         * <p/>
         * Optional filter declaring which rows to return
         * <p/>
         * Formatted as an SQL WHERE clause (excluding the WHERE itself).
         * <p/>
         * Passing null will DELETE all rows for the given table
         * <p/>
         * Default value is <code>null</code>
         *
         * @param where where clause
         * @return builder
         */
        @NonNull
        public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause
         * <p/>
         * Passed objects will be immediately converted to {@link String} via calling {@link Object#toString()}
         * <p/>
         * Default value is <code>null</code>
         *
         * @param whereArgs list of arguments for where clause
         * @return builder
         */
        @NonNull
        public Builder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds immutable instance of {@link DeleteQuery}
         *
         * @return immutable instance of {@link DeleteQuery}
         */
        @NonNull
        public DeleteQuery build() {
            if (table == null || table.length() == 0) {
                throw new IllegalStateException("Please specify table name");
            }

            return new DeleteQuery(
                    table,
                    where,
                    whereArgs
            );
        }
    }
}