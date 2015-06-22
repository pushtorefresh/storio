package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container for results of Delete Operation.
 * <p>
 * Instances of this class are Immutable.
 */
public final class DeleteResult {

    private final int numberOfRowsDeleted;

    @NonNull
    private final Set<Uri> affectedUris;

    private DeleteResult(int numberOfRowsDeleted, @NonNull Set<Uri> affectedUris) {
        this.numberOfRowsDeleted = numberOfRowsDeleted;
        this.affectedUris = Collections.unmodifiableSet(affectedUris);
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation.
     *
     * @param numberOfRowsDeleted number of rows that were deleted.
     * @param affectedUris        non-null set of Uris that wer affected.
     * @return new instance of immutable container for results of Delete Operation.
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull Set<Uri> affectedUris) {
        checkNotNull(affectedUris, "Please specify affected Uris");
        return new DeleteResult(numberOfRowsDeleted, affectedUris);
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation.
     *
     * @param numberOfRowsDeleted number of rows that were deleted.
     * @param affectedUri         non-null Uri that was affected.
     * @return new instance of immutable container for results of Delete Operation.
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull Uri affectedUri) {
        checkNotNull(affectedUri, "Please specify affected Uri");
        return new DeleteResult(numberOfRowsDeleted, Collections.singleton(affectedUri));
    }

    /**
     * Gets number of rows that were deleted.
     *
     * @return number of rows that were deleted.
     */
    public int numberOfRowsDeleted() {
        return numberOfRowsDeleted;
    }

    /**
     * Gets immutable set of affected Uris.
     *
     * @return affected Uris.
     */
    @NonNull
    public Set<Uri> affectedUris() {
        return affectedUris;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteResult that = (DeleteResult) o;

        if (numberOfRowsDeleted != that.numberOfRowsDeleted) return false;
        return affectedUris.equals(that.affectedUris);
    }

    @Override
    public int hashCode() {
        int result = numberOfRowsDeleted;
        result = 31 * result + affectedUris.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeleteResult{" +
                "numberOfRowsDeleted=" + numberOfRowsDeleted +
                ", affectedUris=" + affectedUris +
                '}';
    }
}
