package com.pushtorefresh.storio.sqlitedb;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

/**
 * Immutable container of information about one or more changes in {@link StorIOSQLiteDb}
 */
public class Changes {

    /**
     * Immutable set of affected tables
     */
    @NonNull
    public final Set<String> affectedTables;

    /**
     * Creates {@link Changes} container with info about changes
     *
     * @param affectedTables set of tables which were affected by these changes
     */
    public Changes(@NonNull Set<String> affectedTables) {
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    /**
     * Creates {@link Changes} container with info about changes
     *
     * @param affectedTable table which was affected by these changes
     */
    public Changes(@NonNull String affectedTable) {
        this(Collections.singleton(affectedTable));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Changes changes = (Changes) o;

        return affectedTables.equals(changes.affectedTables);

    }

    @Override
    public int hashCode() {
        return affectedTables.hashCode();
    }

    @Override
    public String toString() {
        return "Changes{" +
                "affectedTables=" + affectedTables +
                '}';
    }
}
