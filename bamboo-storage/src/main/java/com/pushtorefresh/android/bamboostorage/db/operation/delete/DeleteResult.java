package com.pushtorefresh.android.bamboostorage.db.operation.delete;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

public class DeleteResult {

    private final int numberOfDeletedRows;
    private final @NonNull Set<String> affectedTables;

    private DeleteResult(int numberOfDeletedRows, @NonNull Set<String> affectedTables) {
        this.numberOfDeletedRows = numberOfDeletedRows;
        this.affectedTables = Collections.unmodifiableSet(affectedTables);
    }

    @NonNull public static DeleteResult newDeleteResult(int numberOfDeletedRows, @NonNull Set<String> affectedTables) {
        return new DeleteResult(numberOfDeletedRows, affectedTables);
    }

    public int numberOfDeletedRows() {
        return numberOfDeletedRows;
    }

    @NonNull public Set<String> affectedTables() {
        return affectedTables;
    }
}
