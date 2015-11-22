package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

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
        if (numberOfRowsUpdated != null && numberOfRowsUpdated < 0) {
            throw new IllegalStateException("Number of rows updated must be >= 0");
        }

        checkNotNull(affectedUri, "affectedUri must not be null");

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
        checkNotNull(insertedUri, "insertedUri must not be null");
        return new PutResult(insertedUri, null, affectedUri);
    }

    /**
     * Creates {@link PutResult} for update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be {@code >= 0}.
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
     * {@code false} if it was "insert" or {@code 0} rows were updated
     * (for example: your custom {@link PutResolver}
     * may check that there is already stored row with same columns, so no insert will be done,
     * and no actual update should be performed).
     * But also, keep in mind, that {@link DefaultPutResolver} will return same value
     * that will return {@link android.content.ContentResolver}.
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null && numberOfRowsUpdated > 0;
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
     * @return {@code null} if nothing was updated or number of rows updated {@code (>= 0)}.
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
