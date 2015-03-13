package com.pushtorefresh.storio.db.operation;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

/**
 * Contains information about one or more changed tables in {@link com.pushtorefresh.storio.db.BambooStorageDb}
 */
public class Changes {

    @NonNull private final Set<String> tables;

    public Changes(@NonNull Set<String> tables) {
        this.tables = Collections.unmodifiableSet(tables);
    }

    public Changes(@NonNull String table) {
        this(Collections.singleton(table));
    }

    /**
     * Returns set of modified tables
     *
     * @return set of modified tables
     */
    @NonNull public Set<String> tables() {
        return tables;
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Changes changes = (Changes) o;

        return tables.equals(changes.tables);

    }

    @Override public int hashCode() {
        return tables.hashCode();
    }

    @Override public String toString() {
        return "Changes{" +
                "tables=" + tables +
                '}';
    }
}
