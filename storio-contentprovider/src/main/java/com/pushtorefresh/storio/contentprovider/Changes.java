package com.pushtorefresh.storio.contentprovider;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

/**
 * Immutable container of information about one or more changes in {@link StorIOContentProvider}
 */
public class Changes {

    /**
     * Immutable set of affected Uris
     */
    @NonNull
    public final Set<Uri> affectedUris;

    /**
     * Creates {@link Changes} container with info about changes
     *
     * @param affectedUris set of Uris which were affected by these changes
     */
    public Changes(@NonNull Set<Uri> affectedUris) {
        this.affectedUris = Collections.unmodifiableSet(affectedUris);
    }

    /**
     * Creates {@link Changes} container with info about changes
     *
     * @param affectedUri Uri which was affected by these changes
     */
    public Changes(@NonNull Uri affectedUri) {
        this(Collections.singleton(affectedUri));
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
