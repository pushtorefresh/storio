package com.pushtorefresh.storio.sqlite.annotations;

import android.content.ContentValues;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;
import java.lang.Override;

/**
 * Generated resolver for Put Operation.
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
                .where("field4 = ?")
                .whereArgs(object.field4)
                .build();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public ContentValues mapToContentValues(@NonNull PrimitiveFields object) {
        ContentValues contentValues = new ContentValues(8);

        contentValues.put("field1", object.field1);
        contentValues.put("field2", object.field2);
        contentValues.put("field3", object.field3);
        contentValues.put("field4", object.field4);
        contentValues.put("field5", object.field5);
        contentValues.put("field6", object.field6);
        contentValues.put("field7", object.field7);
        contentValues.put("field8", object.field8);

        return contentValues;
    }
}