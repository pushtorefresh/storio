package com.pushtorefresh.storio.sqlite.annotations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

/**
 * Generated resolver for Delete Operation
 */
public class PrimitiveFieldsStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<PrimitiveFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public DeleteQuery mapToDeleteQuery(@NonNull PrimitiveFields object) {
        return DeleteQuery.builder()
                .table("table")
                .where("longField = ?")
                .whereArgs(object.longField)
                .build();
    }
}
