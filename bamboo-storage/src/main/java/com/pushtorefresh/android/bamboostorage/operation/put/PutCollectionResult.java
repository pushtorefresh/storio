package com.pushtorefresh.android.bamboostorage.operation.put;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

public class PutCollectionResult<T> {

    @NonNull private final Map<T, PutResult> results;

    public PutCollectionResult(@NonNull Map<T, PutResult> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    @NonNull public Map<T, PutResult> results() {
        return results;
    }

    // TODO cache this value?
    public int numberOfInserts() {
        int numberOfInserts = 0;

        for (T object : results.keySet()) {
            if (results.get(object).wasInserted()) {
                numberOfInserts++;
            }
        }

        return numberOfInserts;
    }

    public int numberOfUpdates() {
        int numberOfUpdates = 0;

        for (T object : results.keySet()) {
            if (results.get(object).wasUpdated()) {
                numberOfUpdates++;
            }
        }

        return numberOfUpdates;
    }
}
