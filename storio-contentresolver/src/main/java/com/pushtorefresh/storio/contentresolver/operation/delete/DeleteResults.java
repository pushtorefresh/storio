package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Map;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Immutable container for multiple results of Delete Operation
 * <p>
 * Instances of this class are immutable
 */
public final class DeleteResults<T> {

    @NonNull
    private final Map<T, DeleteResult> results;

    private DeleteResults(@NonNull Map<T, DeleteResult> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    @NonNull
    public static <T> DeleteResults<T> newInstance(@NonNull Map<T, DeleteResult> results) {
        checkNotNull(results, "Please specify delete results map");
        return new DeleteResults<>(results);
    }

    /**
     * Gets immutable Map of pairs (object, DeleteResult)
     *
     * @return immutable Map of pairs (object, DeleteResult)
     */
    @NonNull
    public Map<T, DeleteResult> results() {
        return results;
    }
}
