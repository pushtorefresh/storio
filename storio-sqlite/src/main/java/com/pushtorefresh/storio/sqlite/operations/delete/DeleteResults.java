package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container for results of Delete Operation
 * <p>
 * Instances of this class are Immutable
 *
 * @param <T> type of objects
 */
public final class DeleteResults<T> {

    @NonNull
    private final Map<T, DeleteResult> results;

    private DeleteResults(@NonNull Map<T, DeleteResult> results) {
        checkNotNull(results, "Please specify Delete Operation results");
        this.results = Collections.unmodifiableMap(results);
    }

    /**
     * Creates new instance of immutable container for results of Delete Operation
     *
     * @param results map with results of Delete Operation
     * @param <T>     type of objects
     * @return new instance of {@link DeleteResults}
     */
    @NonNull
    public static <T> DeleteResults<T> newInstance(@NonNull Map<T, DeleteResult> results) {
        return new DeleteResults<T>(results);
    }

    /**
     * Returns immutable Map of pairs (object, deleteResult)
     *
     * @return immutable Map of pairs (object, deleteResult)
     */
    @NonNull
    public Map<T, DeleteResult> results() {
        return results;
    }

    /**
     * Checks whether particular object was deleted
     *
     * @param object object to check
     * @return true if object was deleted, false otherwise
     */
    public boolean wasDeleted(@NonNull T object) {
        final DeleteResult result = results.get(object);
        return result != null && result.numberOfRowsDeleted() > 0;
    }

    /**
     * Checks whether particular object was NOT deleted
     *
     * @param object object to check
     * @return true if object was NOT deleter, false if it was deleted
     */
    public boolean wasNotDeleted(@NonNull T object) {
        return !wasDeleted(object);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DeleteResults<?> that = (DeleteResults<?>) o;

        return results.equals(that.results);
    }

    @Override
    public int hashCode() {
        return results.hashCode();
    }

    @Override
    public String toString() {
        return "DeleteResults{" +
                "results=" + results +
                '}';
    }
}
