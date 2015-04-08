package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Immutable container for results of Put Operation
 */
public class PutResult {

    @Nullable
    private final Long insertedId;

    @Nullable
    private final Integer numberOfRowsUpdated;

    @NonNull
    private final String affectedTable;

    private PutResult(@Nullable Long insertedId, @Nullable Integer numberOfRowsUpdated, @NonNull String affectedTable) {
        this.insertedId = insertedId;
        this.numberOfRowsUpdated = numberOfRowsUpdated;
        this.affectedTable = affectedTable;
    }

    /**
     * Creates {@link PutResult} for insert
     *
     * @param insertedId    id of new row
     * @param affectedTable affected table
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newInsertResult(long insertedId, @NonNull String affectedTable) {
        return new PutResult(insertedId, null, affectedTable);
    }

    /**
     * Creates {@link PutResult} for update
     *
     * @param numberOfRowsUpdated number of rows that were updated
     * @param affectedTable       affected table
     * @return new {@link PutResult} instance
     */
    @NonNull
    public static PutResult newUpdateResult(int numberOfRowsUpdated, @NonNull String affectedTable) {
        return new PutResult(null, numberOfRowsUpdated, affectedTable);
    }

    /**
     * Checks whether result of Put Operation was "insert"
     *
     * @return true if something was inserted into {@link com.pushtorefresh.storio.sqlite.StorIOSQLiteDb}, false otherwise
     */
    public boolean wasInserted() {
        return insertedId != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "insert"
     *
     * @return true if nothing was inserted into {@link com.pushtorefresh.storio.sqlite.StorIOSQLiteDb}, false if something was inserted
     */
    public boolean wasNotInserted() {
        return !wasInserted();
    }

    /**
     * Checks whether result of Put Operation was "update"
     *
     * @return true if something was updated in {@link com.pushtorefresh.storio.sqlite.StorIOSQLiteDb}, false otherwise
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "update"
     *
     * @return true if nothing was updated in {@link com.pushtorefresh.storio.sqlite.StorIOSQLiteDb}, false if something was updated
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
     * Gets name of affected table
     *
     * @return non-null name of affected table
     */
    @NonNull
    public String affectedTable() {
        return affectedTable;
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
        return affectedTable.equals(putResult.affectedTable);
    }

    @Override
    public int hashCode() {
        int result = insertedId != null ? insertedId.hashCode() : 0;
        result = 31 * result + (numberOfRowsUpdated != null ? numberOfRowsUpdated.hashCode() : 0);
        result = 31 * result + affectedTable.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PutResult{" +
                "insertedId=" + insertedId +
                ", numberOfRowsUpdated=" + numberOfRowsUpdated +
                ", affectedTable='" + affectedTable + '\'' +
                '}';
    }
}
