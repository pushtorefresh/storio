package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

/**
 * Generated resolver for Get Operation
 */
public class BoxedTypesFieldsIgnoreNullStorIOSQLiteGetResolver extends DefaultGetResolver<BoxedTypesFieldsIgnoreNull> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public BoxedTypesFieldsIgnoreNull mapFromCursor(@NonNull Cursor cursor) {
        BoxedTypesFieldsIgnoreNull object = new BoxedTypesFieldsIgnoreNull();

        if (!cursor.isNull(cursor.getColumnIndex("floatField"))) {
            object.floatField = cursor.getFloat(cursor.getColumnIndex("floatField"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("longField"))) {
            object.longField = cursor.getLong(cursor.getColumnIndex("longField"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("doubleField"))) {
            object.doubleField = cursor.getDouble(cursor.getColumnIndex("doubleField"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("booleanField"))) {
            object.booleanField = cursor.getInt(cursor.getColumnIndex("booleanField")) == 1;
        }
        if (!cursor.isNull(cursor.getColumnIndex("intField"))) {
            object.intField = cursor.getInt(cursor.getColumnIndex("intField"));
        }
        if (!cursor.isNull(cursor.getColumnIndex("shortField"))) {
            object.shortField = cursor.getShort(cursor.getColumnIndex("shortField"));
        }

        return object;
    }
}
