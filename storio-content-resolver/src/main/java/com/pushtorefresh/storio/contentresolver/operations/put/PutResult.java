package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Immutable container for single result of Put Operation.
 */
public final class PutResult {

    @Nullable
    private final Uri insertedUri;

    @Nullable
    private final Integer numberOfRowsUpdated;

    @NonNull
    private final Uri affectedUri;

    private PutResult(@Nullable Uri insertedUri, @Nullable Integer numberOfRowsUpdated, @NonNull Uri affectedUri) {
        this.insertedUri = insertedUri;
        this.numberOfRowsUpdated = numberOfRowsUpdated;
        this.affectedUri = affectedUri;
    }

    /**
     * Creates {@link PutResult} for insert.
     *
     * @param insertedUri Uri of inserted row.
     * @param affectedUri Uri that was affected by insert.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(@NonNull Uri insertedUri, @NonNull Uri affectedUri) {
        return new PutResult(insertedUri, null, affectedUri);
    }

    /**
     * Creates {@link PutResult} for update.
     *
     * @param numberOfRowsUpdated number of rows that were updated.
     * @param affectedUri         Uri that was affected by update.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull Uri affectedUri) {
        return new PutResult(null, numberOfRowsUpdated, affectedUri);
    }

    /**
     * Checks whether result of Put Operation was "insert".
     *
     * @return {@code true} if something was inserted into
     * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver},
     * {@code false} otherwise.
     */
    public boolean wasInserted() {
        return insertedUri != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "insert".
     *
     * @return {@code true} if nothing was inserted
     * into {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver},
     * {@code false} if something was inserted.
     */
    public boolean wasNotInserted() {
        return !wasInserted();
    }

    /**
     * Checks whether result of Put Operation was "update".
     *
     * @return {@code true} if something was updated in
     * {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver},
     * {@code false} otherwise.
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "update".
     *
     * @return {@code true} if nothing was updated
     * in {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver},
     * {@code false} if something was updated.
     */
    public boolean wasNotUpdated() {
        return !wasUpdated();
    }

    /**
     * Gets id of inserted row.
     *
     * @return null if nothing was inserted or id of inserted row.
     */
    @Nullable
    public Uri insertedUri() {
        return insertedUri;
    }

    /**
     * Gets number of updated rows.
     *
     * @return null if nothing was updated or number of updated rows.
     */
    @Nullable
    public Integer numberOfRowsUpdated() {
        return numberOfRowsUpdated;
    }

    /**
     * Gets Uri that was affected by this Put.
     *
     * @return non-null affected Uri.
     */
    @NonNull
    public Uri affectedUri() {
        return affectedUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PutResult putResult = (PutResult) o;

        if (insertedUri != null ? !insertedUri.equals(putResult.insertedUri) : putResult.insertedUri != null)
            return false;
        if (numberOfRowsUpdated != null ? !numberOfRowsUpdated.equals(putResult.numberOfRowsUpdated) : putResult.numberOfRowsUpdated != null)
            return false;
        return affectedUri.equals(putResult.affectedUri);
    }

    @Override
    public int hashCode() {
        int result = insertedUri != null ? insertedUri.hashCode() : 0;
        result = 31 * result + (numberOfRowsUpdated != null ? numberOfRowsUpdated.hashCode() : 0);
        result = 31 * result + affectedUri.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PutResult{" +
                "insertedUri=" + insertedUri +
                ", numberOfRowsUpdated=" + numberOfRowsUpdated +
                ", affectedUri=" + affectedUri +
                '}';
    }
}
