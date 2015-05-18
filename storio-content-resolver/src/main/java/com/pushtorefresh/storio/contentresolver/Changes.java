package com.pushtorefresh.storio.contentresolver;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container of information about one or more changes in {@link StorIOContentResolver}.
 */
public final class Changes {

    /**
     * Immutable set of affected Uris.
     */
    @NonNull
    private final Set<Uri> affectedUris;

    /**
     * Creates {@link Changes} container with info about changes.
     *
     * @param affectedUris set of Uris which were affected by these changes.
     */
    private Changes(@NonNull Set<Uri> affectedUris) {
        this.affectedUris = Collections.unmodifiableSet(affectedUris);
    }

    /**
     * Creates immutable container of information about one
     * or more changes in {@link StorIOContentResolver}.
     *
     * @param affectedUris non-null set of affected Uris.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull Set<Uri> affectedUris) {
        checkNotNull(affectedUris, "Please specify affected Uris");
        return new Changes(affectedUris);
    }

    /**
     * Creates immutable container of information about one
     * or more changes in {@link StorIOContentResolver}.
     *
     * @param affectedUri non-null Uri that was affected.
     * @return new immutable instance of {@link Changes}.
     */
    @NonNull
    public static Changes newInstance(@NonNull Uri affectedUri) {
        checkNotNull(affectedUri, "Please specify affected Uri");
        return new Changes(Collections.singleton(affectedUri));
    }

    /**
     * Gets immutable set of affected Uris.
     *
     * @return immutable set of affected Uris.
     */
    @NonNull
    public Set<Uri> affectedUris() {
        return affectedUris;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Changes changes = (Changes) o;

        return affectedUris.equals(changes.affectedUris);
    }

    @Override
    public int hashCode() {
        return affectedUris.hashCode();
    }

    @Override
    public String toString() {
        return "Changes{" +
                "affectedUris=" + affectedUris +
                '}';
    }
}
