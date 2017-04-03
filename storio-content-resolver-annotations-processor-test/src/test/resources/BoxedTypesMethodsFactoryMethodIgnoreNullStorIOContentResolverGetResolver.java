package com.pushtorefresh.storio.contentresolver.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.contentresolver.operations.get.DefaultGetResolver;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.Short;

/**
 * Generated resolver for Get Operation.
 */
public class BoxedTypesMethodsFactoryMethodIgnoreNullStorIOContentResolverGetResolver extends DefaultGetResolver<BoxedTypesMethodsFactoryMethodIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public BoxedTypesMethodsFactoryMethodIgnoreNull mapFromCursor(@NonNull Cursor cursor) {

        Boolean field1 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field1"))) {
            field1 = cursor.getInt(cursor.getColumnIndex("field1")) == 1;
        }
        Short field2 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field2"))) {
            field2 = cursor.getShort(cursor.getColumnIndex("field2"));
        }
        Integer field3 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field3"))) {
            field3 = cursor.getInt(cursor.getColumnIndex("field3"));
        }
        Long field4 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field4"))) {
            field4 = cursor.getLong(cursor.getColumnIndex("field4"));
        }
        Float field5 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field5"))) {
            field5 = cursor.getFloat(cursor.getColumnIndex("field5"));
        }
        Double field6 = null;
        if (!cursor.isNull(cursor.getColumnIndex("field6"))) {
            field6 = cursor.getDouble(cursor.getColumnIndex("field6"));
        }

        BoxedTypesMethodsFactoryMethodIgnoreNull object = BoxedTypesMethodsFactoryMethodIgnoreNull.create(field1, field2, field3, field4, field5, field6);

        return object;
    }
}