package com.pushtorefresh.storio.sqlite.annotations;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

/**
 * Generated resolver for Get Operation
 */
public class PrimitiveFieldsStorIOSQLiteGetResolver extends DefaultGetResolver<PrimitiveFields> {
    /**
     * {@inheritDoc}
     */
    @Override
    @NonNull
    public PrimitiveFields mapFromCursor(@NonNull Cursor cursor) {
        PrimitiveFields object = new PrimitiveFields();

        object.floatField = cursor.getFloat(cursor.getColumnIndex("floatField"));
        object.longField = cursor.getLong(cursor.getColumnIndex("longField"));
        object.doubleField = cursor.getDouble(cursor.getColumnIndex("doubleField"));
        object.booleanField = cursor.getInt(cursor.getColumnIndex("booleanField")) == 1;
        object.intField = cursor.getInt(cursor.getColumnIndex("intField"));
        object.stringField = cursor.getString(cursor.getColumnIndex("stringField"));
        object.shortField = cursor.getShort(cursor.getColumnIndex("shortField"));
        object.byteArrayField = cursor.getBlob(cursor.getColumnIndex("byteArrayField"));

        return object;
    }
}
