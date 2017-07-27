package com.pushtorefresh.storio2.sqlite.annotations;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio2.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;
import java.lang.Override;

/**
 * Generated resolver for Delete Operation.
 */
public class BoxedTypesMethodsConstructorStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<BoxedTypesMethodsConstructor> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public DeleteQuery mapToDeleteQuery(@NonNull BoxedTypesMethodsConstructor object) {
        return DeleteQuery.builder()
            .table("table")
            .where("field4 = ?")
            .whereArgs(object.getField4())
            .build();
    }
}