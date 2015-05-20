package com.pushtorefresh.storio.sqlite.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.internal.Queries;

import java.util.List;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;

/**
 * Update query for {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}.
 * <p/>
 * Instances of this class are immutable.
 */
public final class UpdateQuery {

    @NonNull
    private final String table;

    @Nullable
    private final String where;

    @Nullable
    private final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.sqlite.query.UpdateQuery.Builder}
     * instead of constructor.
     */
    private UpdateQuery(@NonNull String table, @Nullable String where, @Nullable List<String> whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = Queries.unmodifiableNullableList(whereArgs);
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
     * <p/>
     * Optional filter declaring which rows to update.
     * <p/>
     * Formatted as an SQL {@code WHERE} clause (excluding the {@code WHERE} itself).
     * <p/>
     * If it's {@code null} — Query will update all rows for the given table.
     *
     * @return nullable {@code WHERE} clause.
     */
    @Nullable
    public String where() {
        return where;
    }

    /**
     * Gets optional immutable list of arguments for {@link #where()} clause.
     *
     * @return nullable immutable list of arguments for {@code WHERE} clause.
     */
    @Nullable
    public List<String> whereArgs() {
        return whereArgs;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UpdateQuery that = (UpdateQuery) o;

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
        return "UpdateQuery{" +
                "table='" + table + '\'' +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                '}';
    }

    /**
     * Builder for {@link UpdateQuery}.
     */
    public static final class Builder {

        /**
         * Required: Specifies table name.
         *
         * @param table non-null table name.
         * @return builder.
         * @see UpdateQuery#table()
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
        private final String table;

        private String where;

        private List<String> whereArgs;

        CompleteBuilder(@NonNull String table) {
            this.table = table;
        }

        /**
         * Optional: Specifies {@code WHERE} clause.
         * <p/>
         * Optional filter declaring which rows to return.
         * <p/>
         * Formatted as an SQL WHERE clause (excluding the {@code WHERE} itself).
         * <p/>
         * Passing {@code null} will UPDATE all rows for the given table.
         * <p/>
         * Default value is {@code null}.
         *
         * @param where {@code WHERE} clause.
         * @return builder.
         * @see UpdateQuery#where()
         */
        @NonNull
        public CompleteBuilder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        /**
         * Optional: Specifies arguments for {@code WHERE} clause.
         * <p/>
         * Passed objects will be immediately converted
         * to list of {@link String} via calling {@link Object#toString()}.
         * <p/>
         * Default value is {@code null}.
         *
         * @param whereArgs list of arguments for {@code WHERE} clause.
         * @return builder.
         * @see UpdateQuery#whereArgs()
         */
        @NonNull
        public CompleteBuilder whereArgs(@Nullable Object... whereArgs) {
            this.whereArgs = Queries.varargsToList(whereArgs);
            return this;
        }

        /**
         * Builds immutable instance of {@link UpdateQuery}.
         *
         * @return immutable instance of {@link UpdateQuery}.
         */
        @NonNull
        public UpdateQuery build() {
            return new UpdateQuery(
                    table,
                    where,
                    whereArgs
            );
        }
    }
}
