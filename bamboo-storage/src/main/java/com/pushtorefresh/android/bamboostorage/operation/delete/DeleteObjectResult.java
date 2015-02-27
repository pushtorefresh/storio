package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.query.DeleteQuery;

public class DeleteObjectResult<T> {

    @NonNull private final T object;
    @NonNull private final DeleteQuery deleteQuery;
    private final int countOfDeletedRows;

    public DeleteObjectResult(@NonNull T object, @NonNull DeleteQuery deleteQuery, int countOfDeletedRows) {
        this.object = object;
        this.deleteQuery = deleteQuery;
        this.countOfDeletedRows = countOfDeletedRows;
    }
}
