package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.net.Uri;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Immutable container for results of Delete Operation
 * <p>
 * Instances of this class are Immutable
 */
public class DeleteResult {

    private final int numberOfRowsDeleted;

    @NonNull
    private final Uri affectedUri;

    private DeleteResult(int numberOfRowsDeleted, @NonNull Uri affectedUri) {
        this.numberOfRowsDeleted = numberOfRowsDeleted;
        this.affectedUri = affectedUri;
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation
     *
     * @param numberOfRowsDeleted number of rows that were deleted
     * @param affectedUri         non-null Uri that was affected
     * @return new instance of immutable container for results of Delete Operation
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull Uri affectedUri) {
        checkNotNull(affectedUri, "Please specify affected Uri");
        return new DeleteResult(numberOfRowsDeleted, affectedUri);
    }

    /**
     * Gets number of rows that were deleted
     *
     * @return number of rows that were deleted
     */
    public int numberOfRowsDeleted() {
        return numberOfRowsDeleted;
    }

    /**
     * Gets affected Uri
     *
     * @return affected Uri
     */
    @NonNull
    public Uri affectedUri() {
        return affectedUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteResult that = (DeleteResult) o;

        if (numberOfRowsDeleted != that.numberOfRowsDeleted) return false;
        return affectedUri.equals(that.affectedUri);
    }

    @Override
    public int hashCode() {
        int result = numberOfRowsDeleted;
        result = 31 * result + affectedUri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeleteResult{" +
                "numberOfRowsDeleted=" + numberOfRowsDeleted +
                ", affectedUri=" + affectedUri +
                '}';
    }
}
