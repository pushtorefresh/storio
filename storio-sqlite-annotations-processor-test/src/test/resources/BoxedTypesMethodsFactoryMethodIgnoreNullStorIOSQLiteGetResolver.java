package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import java.lang.Boolean;
import java.lang.Double;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Override;
import java.lang.Short;

/**
 * Generated resolver for Get Operation
 */
public class BoxedTypesMethodsFactoryMethodIgnoreNullStorIOSQLiteGetResolver extends DefaultGetResolver<BoxedTypesMethodsFactoryMethodIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public BoxedTypesMethodsFactoryMethodIgnoreNull mapFromCursor(@NonNull Cursor cursor) {

        Boolean booleanField = null;
        if (!cursor.isNull(cursor.getColumnIndex("booleanField"))) {
            booleanField = cursor.getInt(cursor.getColumnIndex("booleanField")) == 1;
        }
        Short shortField = null;
        if (!cursor.isNull(cursor.getColumnIndex("shortField"))) {
            shortField = cursor.getShort(cursor.getColumnIndex("shortField"));
        }
        Integer intField = null;
        if (!cursor.isNull(cursor.getColumnIndex("intField"))) {
            intField = cursor.getInt(cursor.getColumnIndex("intField"));
        }
        Long longField = null;
        if (!cursor.isNull(cursor.getColumnIndex("longField"))) {
            longField = cursor.getLong(cursor.getColumnIndex("longField"));
        }
        Float floatField = null;
        if (!cursor.isNull(cursor.getColumnIndex("floatField"))) {
            floatField = cursor.getFloat(cursor.getColumnIndex("floatField"));
        }
        Double doubleField = null;
        if (!cursor.isNull(cursor.getColumnIndex("doubleField"))) {
            doubleField = cursor.getDouble(cursor.getColumnIndex("doubleField"));
        }

        BoxedTypesMethodsFactoryMethodIgnoreNull object = BoxedTypesMethodsFactoryMethodIgnoreNull.create(booleanField, shortField, intField, longField, floatField, doubleField);

        return object;
    }
}
