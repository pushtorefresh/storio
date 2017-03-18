package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Collection;
import java.util.Set;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static com.pushtorefresh.storio.internal.InternalQueries.nonNullSet;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableSet;

/**
 * Immutable container for result of Put Operation.
 * <p>
 * Instances of this class are immutable.
 */
public final class PutResult {

    @Nullable
    private final Long insertedId;

    @Nullable
    private final Integer numberOfRowsUpdated;

    @NonNull
    private final Set<String> affectedTables;

    @NonNull
    private final Set<String> affectedTags;

    private PutResult(
            @Nullable Long insertedId,
            @Nullable Integer numberOfRowsUpdated,
            @NonNull Set<String> affectedTables,
            @NonNull Set<String> affectedTags
    ) {
        if (numberOfRowsUpdated != null && numberOfRowsUpdated < 0) {
            throw new IllegalArgumentException("Number of rows updated must be >= 0, but was: " + numberOfRowsUpdated);
        }

        checkNotNull(affectedTables, "affectedTables must not be null");

        if (affectedTables.size() < 1) {
            throw new IllegalArgumentException("affectedTables must contain at least one element");
        }

        for (String table : affectedTables) {
            checkNotEmpty(table, "affectedTable must not be null or empty, affectedTables = " + affectedTables);
        }

        for (String tag : affectedTags) {
            checkNotEmpty(tag, "affectedTag must not be null or empty, affectedTags = " + affectedTags);
        }

        this.insertedId = insertedId;
        this.numberOfRowsUpdated = numberOfRowsUpdated;
        this.affectedTables = unmodifiableSet(affectedTables);
        this.affectedTags = unmodifiableSet(affectedTags);
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId     id of new row.
     * @param affectedTables tables that were affected.
     * @param affectedTags   notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(
            long insertedId,
            @NonNull Set<String> affectedTables,
            @Nullable Collection<String> affectedTags
    ) {
        return new PutResult(insertedId, null, affectedTables, nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId     id of new row.
     * @param affectedTables tables that were affected.
     * @param affectedTags   notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(
            long insertedId,
            @NonNull Set<String> affectedTables,
            @Nullable String... affectedTags
    ) {
        return newInsertResult(insertedId, affectedTables, nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId    id of new row.
     * @param affectedTable table that was affected.
     * @param affectedTags  notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(
            long insertedId,
            @NonNull String affectedTable,
            @Nullable Collection<String> affectedTags
    ) {
        checkNotNull(affectedTable, "Please specify affected table");
        return new PutResult(insertedId, null, singleton(affectedTable), nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of insert.
     *
     * @param insertedId    id of new row.
     * @param affectedTable table that was affected.
     * @param affectedTags  notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newInsertResult(
            long insertedId,
            @NonNull String affectedTable,
            @Nullable String... affectedTags
    ) {
        checkNotNull(affectedTable, "Please specify affected table");
        return newInsertResult(insertedId, singleton(affectedTable), nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be {@code >= 0}.
     * @param affectedTables      tables that were affected.
     * @param affectedTags        notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(
            int numberOfRowsUpdated,
            @NonNull Set<String> affectedTables,
            @Nullable Collection<String> affectedTags
    ) {
        return new PutResult(null, numberOfRowsUpdated, affectedTables, nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be {@code >= 0}.
     * @param affectedTables      tables that were affected.
     * @param affectedTags        notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(
            int numberOfRowsUpdated,
            @NonNull Set<String> affectedTables,
            @Nullable String... affectedTags
    ) {
        return newUpdateResult(numberOfRowsUpdated, affectedTables, nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be {@code >= 0}.
     * @param affectedTable       table that was affected.
     * @param affectedTags        notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(
            int numberOfRowsUpdated,
            @NonNull String affectedTable,
            @Nullable Collection<String> affectedTags
    ) {
        checkNotNull(affectedTable, "Please specify affected table");
        return newUpdateResult(numberOfRowsUpdated, singleton(affectedTable), nonNullSet(affectedTags));
    }

    /**
     * Creates {@link PutResult} of update.
     *
     * @param numberOfRowsUpdated number of rows that were updated, must be {@code >= 0}.
     * @param affectedTable       table that was affected.
     * @param affectedTags        notification tags that were affected.
     * @return new {@link PutResult} instance.
     */
    @NonNull
    public static PutResult newUpdateResult(
            int numberOfRowsUpdated,
            @NonNull String affectedTable,
            @Nullable String... affectedTags
    ) {
        return newUpdateResult(numberOfRowsUpdated, affectedTable, nonNullSet(affectedTags));
    }

    /**
     * Checks whether result of Put Operation was "insert".
     *
     * @return {@code true} if something was inserted into
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} otherwise.
     */
    public boolean wasInserted() {
        return insertedId != null;
    }

    /**
     * Checks whether result of Put Operation was NOT "insert".
     *
     * @return {@code true} if nothing was inserted into
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} if something was inserted.
     */
    public boolean wasNotInserted() {
        return !wasInserted();
    }

    /**
     * Checks whether result of Put Operation was "update".
     *
     * @return {@code true} if something was updated in
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite},
     * {@code false} if it was "insert" or {@code 0} rows in database were updated
     * (for example: your custom {@link PutResolver} may check that there is already stored row
     * with same columns, so no insert will be done, and no actual update should be performed).
     * But also, keep in mind, that {@link DefaultPutResolver} will return same value
     * that will return {@link android.database.sqlite.SQLiteDatabase}, which will return {@code 1}
     * even if all columns were same.
     */
    public boolean wasUpdated() {
        return numberOfRowsUpdated != null && numberOfRowsUpdated > 0;
    }

    /**
     * Checks whether result of Put Operation was NOT "update".
     *
     * @return {@code true} if nothing was updated in
     * {@link com.pushtorefresh.storio.sqlite.StorIOSQLite}, {@code false} if something was updated.
     */
    public boolean wasNotUpdated() {
        return !wasUpdated();
    }

    /**
     * Gets id of inserted row.
     *
     * @return {@code null} if nothing was inserted or id of inserted row.
     */
    @Nullable
    public Long insertedId() {
        return insertedId;
    }

    /**
     * Gets number of rows updated.
     *
     * @return {@code null} if nothing was updated or number of updated rows {@code (>= 0)}.
     */
    @Nullable
    public Integer numberOfRowsUpdated() {
        return numberOfRowsUpdated;
    }

    /**
     * Gets names of affected tables.
     *
     * @return non-null unmodifiable set of affected tables.
     */
    @NonNull
    public Set<String> affectedTables() {
        return affectedTables;
    }

    /**
     * Gets notification tags which were affected.
     *
     * @return non-null unmodifiable set of affected tags.
     */
    @NonNull
    public Set<String> affectedTags() {
        return affectedTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PutResult putResult = (PutResult) o;

        if (insertedId != null ? !insertedId.equals(putResult.insertedId) : putResult.insertedId != null)
            return false;
        if (numberOfRowsUpdated != null ? !numberOfRowsUpdated.equals(putResult.numberOfRowsUpdated) : putResult.numberOfRowsUpdated != null)
            return false;
        if (!affectedTables.equals(putResult.affectedTables)) return false;
        return affectedTags.equals(putResult.affectedTags);

    }

    @Override
    public int hashCode() {
        int result = insertedId != null ? insertedId.hashCode() : 0;
        result = 31 * result + (numberOfRowsUpdated != null ? numberOfRowsUpdated.hashCode() : 0);
        result = 31 * result + affectedTables.hashCode();
        result = 31 * result + affectedTags.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PutResult{" +
                "insertedId=" + insertedId +
                ", numberOfRowsUpdated=" + numberOfRowsUpdated +
                ", affectedTables=" + affectedTables +
                ", affectedTags=" + affectedTags +
                '}';
    }
}
