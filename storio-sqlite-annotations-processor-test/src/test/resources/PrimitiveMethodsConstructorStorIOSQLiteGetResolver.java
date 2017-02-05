package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

/**
 * Generated resolver for Get Operation
 */
public class PrimitiveMethodsConstructorStorIOSQLiteGetResolver extends DefaultGetResolver<PrimitiveMethodsConstructor> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public PrimitiveMethodsConstructor mapFromCursor(@NonNull Cursor cursor) {

        boolean booleanField = cursor.getInt(cursor.getColumnIndex("booleanField")) == 1;
        short shortField = cursor.getShort(cursor.getColumnIndex("shortField"));
        int intField = cursor.getInt(cursor.getColumnIndex("intField"));
        long longField = cursor.getLong(cursor.getColumnIndex("longField"));
        float floatField = cursor.getFloat(cursor.getColumnIndex("floatField"));
        double doubleField = cursor.getDouble(cursor.getColumnIndex("doubleField"));
        String stringField = cursor.getString(cursor.getColumnIndex("stringField"));
        byte[] byteArrayField = cursor.getBlob(cursor.getColumnIndex("byteArrayField"));

        PrimitiveMethodsConstructor object = new PrimitiveMethodsConstructor(booleanField, shortField, intField, longField, floatField, doubleField, stringField, byteArrayField);

        return object;
    }
}
