package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

public final class DeleteResult {

    @NonNull private final DeleteQuery deleteQuery;
    private final int numberOfDeletedRows;

    public DeleteResult(@NonNull DeleteQuery deleteQuery, int numberOfDeletedRows) {
        this.deleteQuery = deleteQuery;
        this.numberOfDeletedRows = numberOfDeletedRows;
    }

    public int numberOfDeletedRows() {
        return numberOfDeletedRows;
    }
}
