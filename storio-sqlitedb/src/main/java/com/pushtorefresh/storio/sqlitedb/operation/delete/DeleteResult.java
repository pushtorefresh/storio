package com.pushtorefresh.storio.sqlitedb.operation.delete;

import android.support.annotation.NonNull;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Immutable container for result of Delete Operation
 * <p>
 * Instances of this class are Immutable
 */
public class DeleteResult {

    private final int numberOfRowsDeleted;

    @NonNull
    private final String affectedTable;

    private DeleteResult(int numberOfRowsDeleted, @NonNull String affectedTable) {
        checkNotNull(affectedTable, "Please specify affected table");

        this.numberOfRowsDeleted = numberOfRowsDeleted;
        this.affectedTable = affectedTable;
    }

    /**
     * Creates new instance of immutable container for result of Delete Operation
     *
     * @param numberOfRowsDeleted number of rows that were deleted
     * @param affectedTable       tables that was affected
     * @return new instance of immutable container for result of Delete Operation
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull String affectedTable) {
        return new DeleteResult(numberOfRowsDeleted, affectedTable);
    }

    /**
     * Returns number of rows that were deleted
     *
     * @return number of rows that were deleted
     */
    public int numberOfRowsDeleted() {
        return numberOfRowsDeleted;
    }

    /**
     * Returns name of the table that was affected by Delete Operation
     *
     * @return name of affected table
     */
    @NonNull
    public String affectedTable() {
        return affectedTable;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteResult that = (DeleteResult) o;

        if (numberOfRowsDeleted != that.numberOfRowsDeleted) return false;
        return affectedTable.equals(that.affectedTable);
    }

    @Override
    public int hashCode() {
        int result = numberOfRowsDeleted;
        result = 31 * result + affectedTable.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeleteResult{" +
                "numberOfRowsDeleted=" + numberOfRowsDeleted +
                ", affectedTable='" + affectedTable + '\'' +
                '}';
    }
}
