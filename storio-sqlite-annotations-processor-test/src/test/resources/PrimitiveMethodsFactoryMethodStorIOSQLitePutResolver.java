package com.pushtorefresh.storio.sqlite.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

/**
 * Generated resolver for Put Operation
 */
public class PrimitiveMethodsFactoryMethodStorIOSQLitePutResolver extends DefaultPutResolver<PrimitiveMethodsFactoryMethod> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull PrimitiveMethodsFactoryMethod object) {
        return InsertQuery.builder()
            .table("table")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull PrimitiveMethodsFactoryMethod object) {
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
    public ContentValues mapToContentValues(@NonNull PrimitiveMethodsFactoryMethod object) {
        ContentValues contentValues = new ContentValues(8);

        contentValues.put("floatField", object.getFloatField());
        contentValues.put("longField", object.getLongField());
        contentValues.put("doubleField", object.getDoubleField());
        contentValues.put("booleanField", object.isBooleanField());
        contentValues.put("intField", object.getIntField());
        contentValues.put("stringField", object.getStringField());
        contentValues.put("shortField", object.getShortField());
        contentValues.put("byteArrayField", object.getByteArrayField());

        return contentValues;
    }
}
