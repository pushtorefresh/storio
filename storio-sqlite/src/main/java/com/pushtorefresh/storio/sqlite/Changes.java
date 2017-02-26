package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static java.util.Collections.singleton;

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
     * Immutable set of affected tags.
     */
    @NonNull
    private final Set<String> affectedTags;

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedTables set of tables which were affected by these changes.
     * @param affectedTags set of tags which were affected by these changes.
     */
    private Changes(@NonNull Set<String> affectedTables, @NonNull Set<String> affectedTags) {
        checkNotNull(affectedTables, "Please specify affected tables");
        checkNotNull(affectedTags, "Please specify affected tags");
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
        this.affectedTags = Collections.unmodifiableSet(affectedTags);
    }

    /**
     * Creates new instance of {@link Changes}.
     *
     * @param affectedTables non-null set of affected tables.
     * @param affectedTags non-null set of affected tags.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(
            @NonNull Set<String> affectedTables,
            @NonNull Set<String> affectedTags
    ) {
        return new Changes(affectedTables, affectedTags);
    }

    /**
     * Creates new instance of {@link Changes}.
     *
     * @param affectedTables non-null set of affected tables.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull Set<String> affectedTables) {
        return new Changes(affectedTables, Collections.<String>emptySet());
    }

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedTable table that was affected.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull String affectedTable) {
        return newInstance(affectedTable, null);
    }

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedTable table that was affected.
     * @param affectedTag tag that was affected.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull String affectedTable, @Nullable String affectedTag) {
        checkNotNull(affectedTable, "Please specify affected table");
        final Set<String> tags = affectedTag == null
                ? Collections.<String>emptySet()
                : singleton(affectedTag);
        return new Changes(Collections.singleton(affectedTable), tags);
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

    /**
     * Gets immutable set of affected tags.
     *
     * @return immutable set of affected tags.
     */
    @NonNull
    public Set<String> affectedTags() {
        return affectedTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Changes changes = (Changes) o;

        if (!affectedTables.equals(changes.affectedTables)) return false;
        return affectedTags.equals(changes.affectedTags);

    }

    @Override
    public int hashCode() {
        int result = affectedTables.hashCode();
        result = 31 * result + affectedTags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Changes{" +
                "affectedTables=" + affectedTables +
                ", affectedTags=" + affectedTags +
                '}';
    }
}
