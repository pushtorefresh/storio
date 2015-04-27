package com.pushtorefresh.storio.operation.group;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;

import java.util.Collections;
import java.util.Map;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * Immutable container for results of {@link PreparedGroupOperation}
 * Thread-safe
 * <p/>
 * Unfortunately, we can not generify it because each Operation can return different type of result
 */
public final class GroupOperationResults {

    @NonNull
    private final Map<PreparedOperation<?>, Object> results;

    /**
     * Creates new {@link GroupOperationResults}
     *
     * @param results non-null map of pairs (operation, resultOfOperation)
     */
    private GroupOperationResults(@NonNull Map<PreparedOperation<?>, Object> results) {
        this.results = Collections.unmodifiableMap(results);
    }

    /**
     * Creates new instance of {@link GroupOperationResults}
     *
     * @param results non-null map of pairs (operation, resultOfOperation)
     * @return immutable container for results of {@link PreparedGroupOperation}
     */
    @NonNull
    static GroupOperationResults newInstance(@NonNull Map<PreparedOperation<?>, Object> results) {
        checkNotNull(results, "Please specify results of Group Operation");
        return new GroupOperationResults(results);
    }

    /**
     * Gets immutable map of pairs (operation, resultOfOperation)
     *
     * @return non-null map of pairs (operation, resultOfOperation)
     */
    @NonNull
    public Map<PreparedOperation<?>, Object> results() {
        return results;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupOperationResults that = (GroupOperationResults) o;

        return results.equals(that.results);
    }

    @Override
    public int hashCode() {
        return results.hashCode();
    }

    @Override
    public String toString() {
        return "GroupOperationResults{" +
                "results=" + results +
                '}';
    }
}
