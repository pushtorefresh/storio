package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import java.lang.Override;

/**
 * Generated resolver for Get Operation.
 */
public class BoxedTypesFieldsIgnoreNullStorIOSQLiteGetResolver extends DefaultGetResolver<BoxedTypesFieldsIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public BoxedTypesFieldsIgnoreNull mapFromCursor(@NonNull Cursor cursor) {
        BoxedTypesFieldsIgnoreNull object = new BoxedTypesFieldsIgnoreNull();

        if (!cursor.isNull(cursor.getColumnIndex("field1"))) {
            object.field1 = cursor.getInt(cursor.getColumnIndex("field1")) == 1;
        }
        if (!cursor.isNull(cursor.getColumnIndex("field2"))) {
            object.field2 = cursor.getShort(cursor.getColumnIndex("field2"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field3"))) {
            object.field3 = cursor.getInt(cursor.getColumnIndex("field3"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field4"))) {
            object.field4 = cursor.getLong(cursor.getColumnIndex("field4"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field5"))) {
            object.field5 = cursor.getFloat(cursor.getColumnIndex("field5"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("field6"))) {
            object.field6 = cursor.getDouble(cursor.getColumnIndex("field6"));
        }

        return object;
    }
}