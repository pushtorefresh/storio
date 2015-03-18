package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.util.QueryUtil;

import java.util.List;

public class DeleteQuery {

    @NonNull public final String table;

    @Nullable public final String where;

    @Nullable public final List<String> whereArgs;

    /**
     * Please use {@link com.pushtorefresh.storio.db.query.DeleteQuery.Builder} instead of constructor
     */
    protected DeleteQuery(@NonNull String table, @Nullable String where, @Nullable List<String> whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = QueryUtil.listToUnmodifiable(whereArgs);
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteQuery that = (DeleteQuery) o;

        if (!table.equals(that.table)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        return !(whereArgs != null ? !whereArgs.equals(that.whereArgs) : that.whereArgs != null);

    }

    @Override public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? whereArgs.hashCode() : 0);
        return result;
    }

    @Override public String toString() {
        return "DeleteQuery{" +
                "table='" + table + '\'' +
                ", where='" + where + '\'' +
                ", whereArgs=" + whereArgs +
                '}';
    }

    public static class Builder {

        private String table;
        private String where;
        private List<String> whereArgs;

        @NonNull public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        @NonNull public Builder whereArgs(@Nullable String... whereArgs) {
            this.whereArgs = QueryUtil.varargsToList(whereArgs);
            return this;
        }

        @NonNull public DeleteQuery build() {
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