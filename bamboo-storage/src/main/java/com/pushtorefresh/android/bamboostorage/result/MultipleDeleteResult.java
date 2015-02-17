package com.pushtorefresh.android.bamboostorage.result;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;

import java.util.HashMap;
import java.util.Map;

public class MultipleDeleteResult<T extends BambooStorableType> {

    private final Map<T, SingleDeleteResult<T>> deleteResults = new HashMap<>();

    public MultipleDeleteResult(@NonNull BambooStorage bambooStorage, @NonNull Iterable<T> objects) {
        for (T object : objects) {
            deleteResults.put(object, new SingleDeleteResult<>(bambooStorage, object));
        }
    }

    @NonNull public Map<T, SingleDeleteResult<T>> getDeleteResults() {
        return deleteResults;
    }
}
