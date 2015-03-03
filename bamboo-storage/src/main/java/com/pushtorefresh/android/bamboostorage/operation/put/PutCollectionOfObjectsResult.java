package com.pushtorefresh.android.bamboostorage.operation.put;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

public class PutCollectionOfObjectsResult<T> {

    @NonNull private final Map<T, PutResult> results;

    public PutCollectionOfObjectsResult(@NonNull Map<T, PutResult> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    @NonNull public Map<T, PutResult> getResults() {
        return results;
    }
}
