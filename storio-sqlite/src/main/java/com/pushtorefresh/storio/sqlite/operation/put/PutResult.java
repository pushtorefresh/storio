package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container for results of Put Operation
 */
public final class PutResult {

    @Nullable
    private final Long insertedId;

    @Nullable
    private final Integer numberOfRowsUpdated;

    @NonNull
    private final Set<String> affectedTables;

    private PutResult(@Nullable Long insertedId, @Nullable Integer numberOfRowsUpdated, @NonNull Set<String> affectedTables) {
        checkNotNull(affectedTables, "Please specify affected tables");
        this.insertedId = insertedId;
        this.numberOfRowsUpdated = numberOfRowsUpdated;
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    /**
     * Creates {@link PutResult} of insert
     *
     * @param insertedId     id of new row
     * @param affectedTables tables that were affected
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newInsertResult(long insertedId, @NonNull Set<String> affectedTables) {
        return new PutResult(insertedId, null, affectedTables);
    }

    /**
     * Creates {@link PutResult} of insert
     *
     * @param insertedId    id of new row
     * @param affectedTable table that was affected
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newInsertResult(long insertedId, @NonNull String affectedTable) {
        return new PutResult(insertedId, null, Collections.singleton(affectedTable));
    }

    /**
     * Creates {@link PutResult} of update
     *
     * @param numberOfRowsUpdated number of rows that were updated
     * @param affectedTables      tables that were affected
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull Set<String> affectedTables) {
        return new PutResult(null, numberOfRowsUpdated, affectedTables);
    }

    /**
     * Creates {@link PutResult} of update
     *
     * @param numberOfRowsUpdated number of rows that were updated
     * @param affectedTable       table that was affected
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull String affectedTable) {
        return new PutResult(null, numberOfRowsUpdated, Collections.singleton(affectedTable));
    }

    /**
     * Checks whether result of Put Operation was "insert"
     *
     * @return true if something was inserted into {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, false otherwise
     */
    public boolean wasInserted() {
        return insertedId != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "insert"
     *
     * @return true if nothing was inserted into {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, false if something was inserted
     */
    public boolean wasNotInserted() {
        return !wasInserted();
    }

    /**
     * Checks whether result of Put Operation was "update"
     *
     * @return true if something was updated in {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, false otherwise
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "update"
     *
     * @return true if nothing was updated in {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, false if something was updated
     */
    public boolean wasNotUpdated() {
        return !wasUpdated();
    }

    /**
     * Gets id of inserted row
     *
     * @return null if nothing was inserted or id of inserted row
     */
    @Nullable
    public Long insertedId() {
        return insertedId;
    }

    /**
     * Gets number of rows updated
     *
     * @return null if nothing was updated or number of updated rows
     */
    @Nullable
    public Integer numberOfRowsUpdated() {
        return numberOfRowsUpdated;
    }

    /**
     * Gets names of affected tables
     *
     * @return non-null unmodifiable set of affected tables
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
