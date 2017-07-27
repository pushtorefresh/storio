package com.pushtorefresh.storio2.contentresolver.annotations;

import android.support.annotation.NonNull;
import com.pushtorefresh.storio2.contentresolver.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio2.contentresolver.queries.DeleteQuery;
import java.lang.Override;

/**
 * Generated resolver for Delete Operation.
 */
public class BoxedTypesMethodsFactoryMethodStorIOContentResolverDeleteResolver extends DefaultDeleteResolver<BoxedTypesMethodsFactoryMethod> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public DeleteQuery mapToDeleteQuery(@NonNull BoxedTypesMethodsFactoryMethod object) {
        return DeleteQuery.builder()
            .uri("content://uri")
            .where("field4 = ?")
            .whereArgs(object.getField4())
            .build();
    }
}