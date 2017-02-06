package com.pushtorefresh.storio.sqlite.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;
import java.lang.Override;

/**
 * Generated resolver for Put Operation
 */
public class BoxedTypesMethodsConstructorIgnoreNullStorIOSQLitePutResolver extends DefaultPutResolver<BoxedTypesMethodsConstructorIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        return InsertQuery.builder()
            .table("table")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        return UpdateQuery.builder()
            .table("table")
            .where("longField = ?")
            .whereArgs(object.getLongField())
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull BoxedTypesMethodsConstructorIgnoreNull object) {
        ContentValues contentValues = new ContentValues(6);

        if (object.getFloatField() != null) {
            contentValues.put("floatField", object.getFloatField());
        }
        if (object.getLongField() != null) {
            contentValues.put("longField", object.getLongField());
        }
        if (object.getDoubleField() != null) {
            contentValues.put("doubleField", object.getDoubleField());
        }
        if (object.isBooleanField() != null) {
            contentValues.put("booleanField", object.isBooleanField());
        }
        if (object.getIntField() != null) {
            contentValues.put("intField", object.getIntField());
        }
        if (object.getShortField() != null) {
            contentValues.put("shortField", object.getShortField());
        }

        return contentValues;
    }
}
