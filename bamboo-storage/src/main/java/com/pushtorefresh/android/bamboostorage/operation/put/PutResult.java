package com.pushtorefresh.android.bamboostorage.operation.put;

import android.support.annotation.Nullable;

public class PutResult {
    @Nullable private final Long insertedId;
    @Nullable private final Integer numberOfUpdatedRows;

    PutResult(@Nullable Long insertedId, @Nullable Integer numberOfUpdatedRows) {
        this.insertedId = insertedId;
        this.numberOfUpdatedRows = numberOfUpdatedRows;
    }

    public boolean wasInserted() {
        return insertedId != null;
    }

    public boolean wasUpdated() {
        return numberOfUpdatedRows != null;
    }

    @Nullable public Long getInsertedId() {
        return insertedId;
    }

    @Nullable public Integer getNumberOfUpdatedRows() {
        return numberOfUpdatedRows;
    }
}
