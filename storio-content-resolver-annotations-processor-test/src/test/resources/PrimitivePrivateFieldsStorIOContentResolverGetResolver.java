package com.pushtorefresh.storio.contentresolver.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import java.lang.Override;

/**
 * Generated resolver for Get Operation
 */
public class PrimitivePrivateFieldsStorIOContentResolverGetResolver extends DefaultGetResolver<PrimitivePrivateFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public PrimitivePrivateFields mapFromCursor(@NonNull Cursor cursor) {
        PrimitivePrivateFields object = new PrimitivePrivateFields();

        object.setField1(cursor.getInt(cursor.getColumnIndex("field1")) == 1);
        object.setField2(cursor.getShort(cursor.getColumnIndex("field2")));
        object.setField3(cursor.getInt(cursor.getColumnIndex("field3")));
        object.setField4(cursor.getLong(cursor.getColumnIndex("field4")));
        object.setField5(cursor.getFloat(cursor.getColumnIndex("field5")));
        object.setField6(cursor.getDouble(cursor.getColumnIndex("field6")));
        object.setField7(cursor.getString(cursor.getColumnIndex("field7")));
        object.setField8(cursor.getBlob(cursor.getColumnIndex("field8")));

        return object;
    }
}