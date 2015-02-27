package com.pushtorefresh.android.bamboostorage.operation.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

public class DeleteCollectionOfObjectsResult<T> {

    @NonNull private final Map<T, DeleteByQueryResult> results;

    public DeleteCollectionOfObjectsResult(@NonNull Map<T, DeleteByQueryResult> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    @NonNull public Map<T, DeleteByQueryResult> getResults() {
        return results;
    }
}
