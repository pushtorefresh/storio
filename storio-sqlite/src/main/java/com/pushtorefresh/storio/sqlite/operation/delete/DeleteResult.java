package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Immutable container for result of Delete Operation
 * <p/>
 * Instances of this class are Immutable
 */
public class DeleteResult {

    private final int numberOfRowsDeleted;

    @NonNull
    private final Set<String> affectedTables;

    private DeleteResult(int numberOfRowsDeleted, @NonNull Set<String> affectedTables) {
        checkNotNull(affectedTables, "Please specify affected tables");
        this.numberOfRowsDeleted = numberOfRowsDeleted;
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation
     *
     * @param numberOfRowsDeleted number of rows that were deleted
     * @param affectedTables      tables that were affected
     * @return new instance of immutable container for result of Delete Operation
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull Set<String> affectedTables) {
        checkNotNull(affectedTables, "Please specify affected tables");
        return new DeleteResult(numberOfRowsDeleted, affectedTables);
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation
     *
     * @param numberOfRowsDeleted number of rows that were deleted
     * @param affectedTable       table that was affected
     * @return new instance of immutable container for results of Delete Operation
     */
    @NonNull
    public static DeleteResult newInstance(int numberOfRowsDeleted, @NonNull String affectedTable) {
        checkNotNull(affectedTable, "Please specify affected table");
        return new DeleteResult(numberOfRowsDeleted, Collections.singleton(affectedTable));
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
     * Gets names of the tables that wer affected by Delete Operation
     *
     * @return unmodifiable set of tables that were affected
     */
    @NonNull
    public Set<String> affectedTables() {
        return affectedTables;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteResult that = (DeleteResult) o;

        if (numberOfRowsDeleted != that.numberOfRowsDeleted) return false;
        return affectedTables.equals(that.affectedTables);
    }

    @Override
    public int hashCode() {
        int result = numberOfRowsDeleted;
        result = 31 * result + affectedTables.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "DeleteResult{" +
                "numberOfRowsDeleted=" + numberOfRowsDeleted +
                ", affectedTables=" + affectedTables +
                '}';
    }
}
