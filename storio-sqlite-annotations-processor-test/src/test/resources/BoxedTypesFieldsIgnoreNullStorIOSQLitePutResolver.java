package com.pushtorefresh.storio.sqlite.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

/**
 * Generated resolver for Put Operation
 */
public class BoxedTypesFieldsIgnoreNullStorIOSQLitePutResolver extends DefaultPutResolver<BoxedTypesFieldsIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesFieldsIgnoreNull object) {
        return InsertQuery.builder()
            .table("table")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesFieldsIgnoreNull object) {
        return UpdateQuery.builder()
            .table("table")
            .where("longField = ?")
            .whereArgs(object.longField)
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull BoxedTypesFieldsIgnoreNull object) {
        ContentValues contentValues = new ContentValues(6);

        if (object.floatField != null) {
            contentValues.put("floatField", object.floatField);
        }
        if (object.longField != null) {
            contentValues.put("longField", object.longField);
        }
        if (object.doubleField != null) {
            contentValues.put("doubleField", object.doubleField);
        }
        if (object.booleanField != null) {
            contentValues.put("booleanField", object.booleanField);
        }
        if (object.intField != null) {
            contentValues.put("intField", object.intField);
        }
        if (object.shortField != null) {
            contentValues.put("shortField", object.shortField);
        }

        return contentValues;
    }
}
