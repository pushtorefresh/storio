package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import java.lang.Override;

/**
 * Generated resolver for Get Operation.
 */
public class BoxedTypesPrivateFieldsStorIOSQLiteGetResolver extends DefaultGetResolver<BoxedTypesPrivateFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public BoxedTypesPrivateFields mapFromCursor(@NonNull Cursor cursor) {
        BoxedTypesPrivateFields object = new BoxedTypesPrivateFields();

        if (!cursor.isNull(cursor.getColumnIndex("field1"))) {
            object.setField1(cursor.getInt(cursor.getColumnIndex("field1")) == 1);
        }
        if (!cursor.isNull(cursor.getColumnIndex("field2"))) {
            object.setField2(cursor.getShort(cursor.getColumnIndex("field2")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field3"))) {
            object.setField3(cursor.getInt(cursor.getColumnIndex("field3")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field4"))) {
            object.setField4(cursor.getLong(cursor.getColumnIndex("field4")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field5"))) {
            object.setField5(cursor.getFloat(cursor.getColumnIndex("field5")));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field6"))) {
            object.setField6(cursor.getDouble(cursor.getColumnIndex("field6")));
        }

        return object;
    }
}