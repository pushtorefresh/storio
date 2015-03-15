package com.pushtorefresh.storio.db.query;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Arrays;

public class DeleteQuery {

    @NonNull public final String table;

    @Nullable public final String where;

    @Nullable public final String[] whereArgs;

    public DeleteQuery(@NonNull String table, @Nullable String where, @Nullable String[] whereArgs) {
        this.table = table;
        this.where = where;
        this.whereArgs = whereArgs;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteQuery that = (DeleteQuery) o;

        if (!table.equals(that.table)) return false;
        if (where != null ? !where.equals(that.where) : that.where != null) return false;
        if (!Arrays.equals(whereArgs, that.whereArgs)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = table.hashCode();
        result = 31 * result + (where != null ? where.hashCode() : 0);
        result = 31 * result + (whereArgs != null ? Arrays.hashCode(whereArgs) : 0);
        return result;
    }

    @Override public String toString() {
        return "DeleteQuery{" +
                "table='" + table + '\'' +
                ", where='" + where + '\'' +
                ", whereArgs=" + Arrays.toString(whereArgs) +
                '}';
    }

    public static class Builder {

        private String table;
        private String where;
        private String[] whereArgs;

        @NonNull public Builder table(@NonNull String table) {
            this.table = table;
            return this;
        }

        @NonNull public Builder where(@Nullable String where) {
            this.where = where;
            return this;
        }

        @NonNull public Builder whereArgs(@Nullable String... whereArgs) {
            this.whereArgs = whereArgs;
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