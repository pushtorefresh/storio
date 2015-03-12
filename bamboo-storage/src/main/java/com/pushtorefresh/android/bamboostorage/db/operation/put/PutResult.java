package com.pushtorefresh.android.bamboostorage.db.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Set;

public class PutResult {
    @Nullable private final Long insertedId;
    @Nullable private final Integer numberOfUpdatedRows;
    @NonNull  private final Set<String> affectedTables;

    private PutResult(@Nullable Long insertedId, @Nullable Integer numberOfUpdatedRows, @NonNull Set<String> affectedTables) {
        this.insertedId = insertedId;
        this.numberOfUpdatedRows = numberOfUpdatedRows;
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    @NonNull public static PutResult newInsertResult(long insertedId, @NonNull Set<String> affectedTables) {
        return new PutResult(insertedId, null, affectedTables);
    }

    @NonNull public static PutResult newUpdateResult(int numberOfUpdatedRows, @NonNull Set<String> affectedTables) {
        return new PutResult(null, numberOfUpdatedRows, affectedTables);
    }

    public boolean wasInserted() {
        return insertedId != null;
    }

    public boolean wasNotInserted() {
        return !wasInserted();
    }

    public boolean wasUpdated() {
        return numberOfUpdatedRows != null;
    }

    public boolean wasNotUpdated() {
        return !wasUpdated();
    }

    @Nullable public Long getInsertedId() {
        return insertedId;
    }

    @Nullable public Integer getNumberOfUpdatedRows() {
        return numberOfUpdatedRows;
    }

    @NonNull public Set<String> affectedTables() {
        return affectedTables;
    }
}
