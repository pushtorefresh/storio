package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

public final class CarDeleteResolver extends DefaultDeleteResolver<Car> {
    @NonNull
    @Override
    protected DeleteQuery mapToDeleteQuery(@NonNull Car car) {
        return DeleteQuery.builder()
                .table(CarsTable.TABLE_NAME)
//                .where(CarsTable.COLUMN_ID + "=?")
//                .whereArgs(car.id())
                .where(CarsTable.COLUMN_UUID + "=?")
                .whereArgs(car.uuid())
                .build();
    }
}