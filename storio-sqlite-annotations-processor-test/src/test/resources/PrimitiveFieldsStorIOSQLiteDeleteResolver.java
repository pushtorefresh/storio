package com.pushtorefresh.storio.sqlite.annotations;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import java.lang.Override;

/**
 * Generated resolver for Delete Operation.
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
                .where("field4 = ?")
                .whereArgs(object.field4)
                .build();
    }
}