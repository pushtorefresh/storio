package com.pushtorefresh.storio.contentresolver.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;
import java.lang.Override;

/**
 * Generated resolver for Put Operation.
 */
public class BoxedTypesMethodsConstructorIgnoreNullStorIOContentResolverPutResolver extends DefaultPutResolver<BoxedTypesMethodsConstructorIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        return InsertQuery.builder()
            .uri("content://uri")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        return UpdateQuery.builder()
            .uri("content://uri")
            .where("field4 = ?")
            .whereArgs(object.getField4())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        ContentValues contentValues = new ContentValues(6);

        if (object.getField1() != null) {
            contentValues.put("field1", object.getField1());
        }
        if (object.getField2() != null) {
            contentValues.put("field2", object.getField2());
        }
        if (object.getField3() != null) {
            contentValues.put("field3", object.getField3());
        }
        if (object.getField4() != null) {
            contentValues.put("field4", object.getField4());
        }
        if (object.getField5() != null) {
            contentValues.put("field5", object.getField5());
        }
        if (object.getField6() != null) {
            contentValues.put("field6", object.getField6());
        }

        return contentValues;
    }
}