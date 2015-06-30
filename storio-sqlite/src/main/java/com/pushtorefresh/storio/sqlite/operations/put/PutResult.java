package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * Immutable container for result of Put Operation.
 * <p>
 * Instances of this class are immutable.
 */
public class PutResult {

    @Nullable
    private final Long insertedId;

    @Nullable
    private final Integer numberOfRowsUpdated;

    @NonNull
    private final Set<String> affectedTables;

    private PutResult(@Nullable Long insertedId, @Nullable Integer numberOfRowsUpdated, @NonNull Set<String> affectedTables) {
        if (numberOfRowsUpdated != null && numberOfRowsUpdated < 1) {
            throw new IllegalArgumentException("Number of rows updated must be > 0");
        }

        checkNotNull(affectedTables, "affectedTables must not be null");

        if (affectedTables.size() < 1) {
            throw new IllegalArgumentException("affectedTables must contain at least one element");
        }

        for (String table : affectedTables) {
            checkNotEmpty(table, "affectedTable must not be null or empty, affectedTables = " + affectedTables);
        }

        this.insertedId = insertedId;
        this.numberOfRowsUpdated = numberOfRowsUpdated;
        this.affectedTables = unmodifiableSet(affectedTables);
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId     id of new row.
     * @param affectedTables tables that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(long insertedId, @NonNull Set<String> affectedTables) {
        return new PutResult(insertedId, null, affectedTables);
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId    id of new row.
     * @param affectedTable table that was affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(long insertedId, @NonNull String affectedTable) {
        return new PutResult(insertedId, null, singleton(affectedTable));
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be greater than 0.
     * @param affectedTables      tables that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull Set<String> affectedTables) {
        return new PutResult(null, numberOfRowsUpdated, affectedTables);
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be greater than 0.
     * @param affectedTable       table that was affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull String affectedTable) {
        return new PutResult(null, numberOfRowsUpdated, singleton(affectedTable));
    }

    /**
     * Checks whether result of Put Operation was "insert".
     *
     * @return {@code true} if something was inserted into
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} otherwise.
     */
    public boolean wasInserted() {
        return insertedId != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "insert".
     *
     * @return {@code true} if nothing was inserted into
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} if something was inserted.
     */
    public boolean wasNotInserted() {
        return !wasInserted();
    }

    /**
     * Checks whether result of Put Operation was "update".
     *
     * @return {@code true} if something was updated in
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} otherwise.
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "update".
     *
     * @return {@code true} if nothing was updated in
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} if something was updated.
     */
    public boolean wasNotUpdated() {
        return !wasUpdated();
    }

    /**
     * Gets id of inserted row.
     *
     * @return {@code null} if nothing was inserted or id of inserted row.
     */
    @Nullable
    public Long insertedId() {
        return insertedId;
    }

    /**
     * Gets number of rows updated.
     *
     * @return {@code null} if nothing was updated or number of updated rows {@code (> 0)}.
     */
    @Nullable
    public Integer numberOfRowsUpdated() {
        return numberOfRowsUpdated;
    }

    /**
     * Gets names of affected tables.
     *
     * @return non-null unmodifiable set of affected tables.
     */
    @NonNull
    public Set<String> affectedTables() {
        return affectedTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PutResult putResult = (PutResult) o;

        if (insertedId != null ? !insertedId.equals(putResult.insertedId) : putResult.insertedId != null)
            return false;
        if (numberOfRowsUpdated != null ? !numberOfRowsUpdated.equals(putResult.numberOfRowsUpdated) : putResult.numberOfRowsUpdated != null)
            return false;
        return affectedTables.equals(putResult.affectedTables);
    }

    @Override
    public int hashCode() {
        int result = insertedId != null ? insertedId.hashCode() : 0;
        result = 31 * result + (numberOfRowsUpdated != null ? numberOfRowsUpdated.hashCode() : 0);
        result = 31 * result + affectedTables.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PutResult{" +
                "insertedId=" + insertedId +
                ", numberOfRowsUpdated=" + numberOfRowsUpdated +
                ", affectedTables=" + affectedTables +
                '}';
    }
}
