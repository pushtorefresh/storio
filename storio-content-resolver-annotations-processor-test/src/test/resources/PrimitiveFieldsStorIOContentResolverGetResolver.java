package com.pushtorefresh.storio.contentresolver.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import java.lang.Override;

/**
 * Generated resolver for Get Operation.
 */
public class PrimitiveFieldsStorIOContentResolverGetResolver extends DefaultGetResolver<PrimitiveFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public PrimitiveFields mapFromCursor(@NonNull Cursor cursor) {
        PrimitiveFields object = new PrimitiveFields();

        object.field1 = cursor.getInt(cursor.getColumnIndex("field1")) == 1;
        object.field2 = cursor.getShort(cursor.getColumnIndex("field2"));
        object.field3 = cursor.getInt(cursor.getColumnIndex("field3"));
        object.field4 = cursor.getLong(cursor.getColumnIndex("field4"));
        object.field5 = cursor.getFloat(cursor.getColumnIndex("field5"));
        object.field6 = cursor.getDouble(cursor.getColumnIndex("field6"));
        object.field7 = cursor.getString(cursor.getColumnIndex("field7"));
        object.field8 = cursor.getBlob(cursor.getColumnIndex("field8"));

        return object;
    }
}