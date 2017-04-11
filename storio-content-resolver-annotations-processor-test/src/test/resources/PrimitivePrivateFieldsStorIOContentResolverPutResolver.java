package com.pushtorefresh.storio.contentresolver.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.contentresolver.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.contentresolver.queries.InsertQuery;
import com.pushtorefresh.storio.contentresolver.queries.UpdateQuery;
import java.lang.Override;

/**
 * Generated resolver for Put Operation
 */
public class PrimitivePrivateFieldsStorIOContentResolverPutResolver extends DefaultPutResolver<PrimitivePrivateFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public InsertQuery mapToInsertQuery(@NonNull PrimitivePrivateFields object) {
        return InsertQuery.builder()
                .uri("content://uri")
                .build();}

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public UpdateQuery mapToUpdateQuery(@NonNull PrimitivePrivateFields object) {
        return UpdateQuery.builder()
                .uri("content://uri")
                .where("field4 = ?")
                .whereArgs(object.getField4())
                .build();}

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull PrimitivePrivateFields object) {
        ContentValues contentValues = new ContentValues(8);

        contentValues.put("field1", object.isField1());
        contentValues.put("field2", object.getField2());
        contentValues.put("field3", object.getField3());
        contentValues.put("field4", object.getField4());
        contentValues.put("field5", object.getField5());
        contentValues.put("field6", object.getField6());
        contentValues.put("field7", object.getField7());
        contentValues.put("field8", object.getField8());

        return contentValues;
    }
}