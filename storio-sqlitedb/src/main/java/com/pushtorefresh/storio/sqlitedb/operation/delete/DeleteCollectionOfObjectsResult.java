package com.pushtorefresh.storio.sqlitedb.operation.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

public class DeleteCollectionOfObjectsResult<T> {

    @NonNull private final Map<T, DeleteResult> results;

    public DeleteCollectionOfObjectsResult(@NonNull Map<T, DeleteResult> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    @NonNull public Map<T, DeleteResult> results() {
        return results;
    }

    public boolean wasDeleted(@NonNull T object) {
        return results.containsKey(object);
    }

    public boolean wasNotDeleted(@NonNull T object) {
        return !results.containsKey(object);
    }
}
