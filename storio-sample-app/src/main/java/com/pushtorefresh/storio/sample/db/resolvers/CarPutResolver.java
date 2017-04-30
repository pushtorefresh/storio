package com.pushtorefresh.storio.sample.db.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

public final class CarPutResolver extends DefaultPutResolver<Car> {

    @NonNull
    @Override
    protected InsertQuery mapToInsertQuery(@NonNull Car object) {
        // You can save this as static final!
        return InsertQuery.builder()
                .table(CarsTable.TABLE_NAME)
                .build();
    }

    @NonNull
    @Override
    protected UpdateQuery mapToUpdateQuery(@NonNull Car car) {
        return UpdateQuery.builder()
                .table(CarsTable.TABLE_NAME)
                .where(CarsTable.COLUMN_ID + "=?")
                .whereArgs(car.id())
                .build();
    }

    @NonNull
    @Override
    protected ContentValues mapToContentValues(@NonNull Car car) {
        final ContentValues contentValues = new ContentValues(3);

        contentValues.put(CarsTable.COLUMN_ID, car.id());
        contentValues.put(CarsTable.COLUMN_PERSON_ID, car.personId());
        contentValues.put(CarsTable.COLUMN_MODEL, car.model());

        return contentValues;
    }
}
