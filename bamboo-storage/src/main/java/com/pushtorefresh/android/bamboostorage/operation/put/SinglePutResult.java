package com.pushtorefresh.android.bamboostorage.operation.put;

import android.support.annotation.Nullable;

public class SinglePutResult {
    @Nullable private final Long insertedId;
    @Nullable private final Integer updatedRowsCount;

    SinglePutResult(@Nullable Long insertedId, @Nullable Integer updatedRowsCount) {
        this.insertedId = insertedId;
        this.updatedRowsCount = updatedRowsCount;
    }

    public boolean wasInserted() {
        return insertedId != null;
    }

    public boolean wasUpdated() {
        return updatedRowsCount != null;
    }

    @Nullable public Long getInsertedId() {
        return insertedId;
    }

    @Nullable public Integer getUpdatedRowsCount() {
        return updatedRowsCount;
    }
}
