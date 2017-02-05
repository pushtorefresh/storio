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
public class BoxedTypesMethodsFactoryMethodStorIOSQLitePutResolver extends DefaultPutResolver<BoxedTypesMethodsFactoryMethod> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesMethodsFactoryMethod object) {
        return InsertQuery.builder()
            .table("table")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesMethodsFactoryMethod object) {
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
    public ContentValues mapToContentValues(@NonNull BoxedTypesMethodsFactoryMethod object) {
        ContentValues contentValues = new ContentValues(6);

        contentValues.put("floatField", object.getFloatField());
        contentValues.put("longField", object.getLongField());
        contentValues.put("doubleField", object.getDoubleField());
        contentValues.put("booleanField", object.isBooleanField());
        contentValues.put("intField", object.getIntField());
        contentValues.put("shortField", object.getShortField());

        return contentValues;
    }
}
