package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container of information about one or more changes happened in {@link StorIOSQLite}.
 */
public final class Changes {

    /**
     * Immutable set of affected tables.
     */
    @NonNull
    private final Set<String> affectedTables;

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedTables set of tables which were affected by these changes.
     */
    private Changes(@NonNull Set<String> affectedTables) {
        checkNotNull(affectedTables, "Please specify affected tables");
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    /**
     * Creates new instance of {@link Changes}.
     *
     * @param affectedTables non-null set of affected tables.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull Set<String> affectedTables) {
        return new Changes(affectedTables);
    }

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedTable table that was affected.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull String affectedTable) {
        checkNotNull(affectedTable, "Please specify affected table");
        return new Changes(Collections.singleton(affectedTable));
    }

    /**
     * Gets immutable set of affected tables.
     *
     * @return immutable set of affected tables.
     */
    @NonNull
    public Set<String> affectedTables() {
        return affectedTables;
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
