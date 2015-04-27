package com.pushtorefresh.storio.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.QueryUtil;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Delete query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}
 * <p/>
 * Instances of this class are Immutable
 */
public final class DeleteQuery {

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
     * Please use {@link com.pushtorefresh.storio.sqlite.query.DeleteQuery.Builder} instead of constructor
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

        private String where;

        private List<String> whereArgs;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
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
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for where clause
         * <p/>
         * Passed objects will be immediately converted to list of {@link String} via calling {@link Object#toString()}
         * <p/>
         * Default value is <code>null</code>
         *
         * @param whereArgs list of arguments for where clause
         * @return builder
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
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
            return new DeleteQuery(
                    table,
                    where,
                    whereArgs
            );
        }
    }
}