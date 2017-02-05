package com.pushtorefresh.storio.sqlite.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

/**
 * Generated resolver for Put Operation
 */
public class PrimitiveFieldsStorIOSQLitePutResolver extends DefaultPutResolver<PrimitiveFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull PrimitiveFields object) {
        return InsertQuery.builder()
                .table("table")
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull PrimitiveFields object) {
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
    public ContentValues mapToContentValues(@NonNull PrimitiveFields object) {
        ContentValues contentValues = new ContentValues(8);

        contentValues.put("floatField", object.floatField);
        contentValues.put("longField", object.longField);
        contentValues.put("doubleField", object.doubleField);
        contentValues.put("booleanField", object.booleanField);
        contentValues.put("intField", object.intField);
        contentValues.put("stringField", object.stringField);
        contentValues.put("shortField", object.shortField);
        contentValues.put("byteArrayField", object.byteArrayField);

        return contentValues;
    }
}
