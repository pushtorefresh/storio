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
public class BoxedTypesFieldsStorIOSQLitePutResolver extends DefaultPutResolver<BoxedTypesFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull BoxedTypesFields object) {
        return InsertQuery.builder()
            .table("table")
            .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull BoxedTypesFields object) {
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
    public ContentValues mapToContentValues(@NonNull BoxedTypesFields object) {
        ContentValues contentValues = new ContentValues(6);

        contentValues.put("floatField", object.floatField);
        contentValues.put("longField", object.longField);
        contentValues.put("doubleField", object.doubleField);
        contentValues.put("booleanField", object.booleanField);
        contentValues.put("intField", object.intField);
        contentValues.put("shortField", object.shortField);

        return contentValues;
    }
}
