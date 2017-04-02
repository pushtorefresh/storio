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
public class BoxedTypesFieldsIgnoreNullStorIOContentResolverPutResolver extends DefaultPutResolver<BoxedTypesFieldsIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesFieldsIgnoreNull object) {
        return InsertQuery.builder()
            .uri("content://uri")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesFieldsIgnoreNull object) {
        return UpdateQuery.builder()
            .uri("content://uri")
            .where("field4 = ?")
            .whereArgs(object.field4)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull BoxedTypesFieldsIgnoreNull object) {
        ContentValues contentValues = new ContentValues(6);

        if (object.field1 != null) {
            contentValues.put("field1", object.field1);
        }
        if (object.field2 != null) {
            contentValues.put("field2", object.field2);
        }
        if (object.field3 != null) {
            contentValues.put("field3", object.field3);
        }
        if (object.field4 != null) {
            contentValues.put("field4", object.field4);
        }
        if (object.field5 != null) {
            contentValues.put("field5", object.field5);
        }
        if (object.field6 != null) {
            contentValues.put("field6", object.field6);
        }

        return contentValues;
    }
}