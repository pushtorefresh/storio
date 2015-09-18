package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

public final class CarGetResolver extends DefaultGetResolver<Car> {
    @NonNull
    @Override
    public Car mapFromCursor(@NonNull Cursor cursor) {
        return new Car(
                cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_ID)),
                cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_PERSON_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_MODEL))
        );
    }
}