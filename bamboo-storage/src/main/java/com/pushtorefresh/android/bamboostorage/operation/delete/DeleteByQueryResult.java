package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

public class DeleteByQueryResult {

    @NonNull private final DeleteQuery deleteQuery;
    private final int countOfDeletedRows;

    public DeleteByQueryResult(@NonNull DeleteQuery deleteQuery, int countOfDeletedRows) {
        this.deleteQuery = deleteQuery;
        this.countOfDeletedRows = countOfDeletedRows;
    }

    @NonNull public DeleteQuery getDeleteQuery() {
        return deleteQuery;
    }

    public int getCountOfDeletedRows() {
        return countOfDeletedRows;
    }
}
