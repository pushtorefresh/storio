package com.pushtorefresh.storio.sqlite.operation.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Representation of result of Put Operation for collection of objects
 * <p/>
 * Instances of this class are Immutable
 *
 * @param <T> type of objects
 */
public class PutResults<T> {

    @NonNull
    private final Map<T, PutResult> results;

    @Nullable
    private volatile Integer numberOfInsertsCache;

    @Nullable
    private volatile Integer numberOfUpdatesCache;

    private PutResults(@NonNull Map<T, PutResult> putResults) {
        this.results = Collections.unmodifiableMap(putResults);
    }

    /**
     * Creates new instance of {@link PutResults}
     *
     * @param putResults results of Put Operation
     * @param <T>        type of objects
     * @return immutable instance of {@link PutResults}
     */
    @NonNull
    public static <T> PutResults<T> newInstance(@NonNull Map<T, PutResult> putResults) {
        return new PutResults<T>(putResults);
    }

    /**
     * Returns immutable Map of pairs (object, PutResult)
     *
     * @return immutable Map of pairs (object, PutResult)
     */
    @NonNull
    public Map<T, PutResult> results() {
        return results;
    }

    /**
     * Returns number of inserts from all {@link #results()}
     *
     * @return number of inserts from all {@link #results()}
     */
    public int numberOfInserts() {
        final Integer cachedValue = numberOfInsertsCache;

        if (cachedValue != null) {
            return cachedValue;
        }

        int numberOfInserts = 0;

        for (T object : results.keySet()) {
            if (results.get(object).wasInserted()) {
                numberOfInserts++;
            }
        }

        numberOfInsertsCache = numberOfInserts;

        return numberOfInserts;
    }

    /**
     * Returns number of updates from all {@link #results()}
     *
     * @return number of updates from all {@link #results()}
     */
    @SuppressWarnings("ConstantConditions")
    public int numberOfUpdates() {
        final Integer cachedValue = numberOfUpdatesCache;

        if (cachedValue != null) {
            return cachedValue;
        }

        int numberOfUpdates = 0;

        for (T object : results.keySet()) {
            final PutResult putResult = results.get(object);

            if (putResult.wasUpdated()) {
                numberOfUpdates += putResult.numberOfRowsUpdated();
            }
        }

        numberOfUpdatesCache = numberOfUpdates;

        return numberOfUpdates;
    }
}
