package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.Map;

/**
 * Immutable container for results of Put Operation.
 * <p>
 * Instances of this class are Immutable
 *
 * @param <T> type of objects that were put.
 */
public final class PutResults<T> {

    @NonNull
    private final Map<T, PutResult> results;

    @Nullable
    private transient volatile Integer numberOfInsertsCache;

    @Nullable
    private transient volatile Integer numberOfUpdatesCache;

    private PutResults(@NonNull Map<T, PutResult> putResults) {
        this.results = Collections.unmodifiableMap(putResults);
    }

    /**
     * Creates new instance of {@link PutResults}.
     *
     * @param putResults results of Put Operation.
     * @param <T>        type of objects.
     * @return immutable instance of {@link PutResults}.
     */
    @NonNull
    public static <T> PutResults<T> newInstance(@NonNull Map<T, PutResult> putResults) {
        return new PutResults<T>(putResults);
    }

    /**
     * Returns immutable Map of pairs {@code (object, PutResult)}.
     *
     * @return immutable Map of pairs {@code (object, PutResult)}.
     */
    @NonNull
    public Map<T, PutResult> results() {
        return results;
    }

    /**
     * Returns number of inserts from all {@link #results()}.
     *
     * @return number of inserts from all {@link #results()}.
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
     * Returns number of updates from all {@link #results()}.
     *
     * @return number of updates from all {@link #results()}.
     */
    public int numberOfUpdates() {
        final Integer cachedValue = numberOfUpdatesCache;

        if (cachedValue != null) {
            return cachedValue;
        }

        int numberOfUpdates = 0;

        for (T object : results.keySet()) {
            final PutResult putResult = results.get(object);

            if (putResult.wasUpdated()) {
                //noinspection ConstantConditions
                numberOfUpdates += putResult.numberOfRowsUpdated();
            }
        }

        numberOfUpdatesCache = numberOfUpdates;

        return numberOfUpdates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PutResults<?> that = (PutResults<?>) o;

        return results.equals(that.results);
    }

    @Override
    public int hashCode() {
        return results.hashCode();
    }

    @Override
    public String toString() {
        return "PutResults{" +
                "results=" + results +
                '}';
    }
}
