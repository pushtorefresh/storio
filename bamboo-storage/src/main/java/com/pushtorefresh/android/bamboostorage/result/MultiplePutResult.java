package com.pushtorefresh.android.bamboostorage.result;

import android.support.annotation.NonNull;

import com.pushtorefresh.android.bamboostorage.BambooStorableType;
import com.pushtorefresh.android.bamboostorage.BambooStorage;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MultiplePutResult<T extends BambooStorableType> {

    @NonNull private final Map<T, SinglePutResult<T>> putResults = new HashMap<>();

    public MultiplePutResult(@NonNull BambooStorage bambooStorage, @NonNull Iterable<T> objects) {
        for (T object : objects) {
            putResults.put(object, new SinglePutResult<>(bambooStorage, object));
        }
    }

    @NonNull public Map<T, SinglePutResult<T>> getPutResults() {
        return Collections.unmodifiableMap(putResults);
    }
}
