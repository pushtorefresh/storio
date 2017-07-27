package com.pushtorefresh.storio2.sample.many_to_many_sample.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.CarStorIOSQLitePutResolver;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonStorIOSQLitePutResolver;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import java.util.ArrayList;
import java.util.List;

import static com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable.COLUMN_PERSON_ID;

public class PersonRelationsPutResolver extends PersonStorIOSQLitePutResolver {

    @NonNull
    private final CarStorIOSQLitePutResolver carStorIOSQLitePutResolver;

    @NonNull
    private final CarPersonRelationPutResolver carPersonRelationPutResolver;

    public PersonRelationsPutResolver(
            @NonNull CarStorIOSQLitePutResolver carStorIOSQLitePutResolver,
            @NonNull CarPersonRelationPutResolver carPersonRelationPutResolver
    ) {
        this.carStorIOSQLitePutResolver = carStorIOSQLitePutResolver;
        this.carPersonRelationPutResolver = carPersonRelationPutResolver;
    }

    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull Person object) {
        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        lowLevel.beginTransaction();
        try {
            final PutResult putResult = super.performPut(storIOSQLite, object);
            //noinspection ConstantConditions
            final long personId = putResult.wasInserted() ? putResult.insertedId() : object.id();

            storIOSQLite.delete()
                    .byQuery(DeleteQuery.builder().table(PersonCarRelationTable.TABLE)
                            .where(COLUMN_PERSON_ID + " = ?")
                            .whereArgs(personId)
                            .build())
                    .prepare()
                    .executeAsBlocking();

            final List<Car> cars = object.cars();
            if (cars != null) {
                final PutResults<Car> carsPutResults = storIOSQLite
                        .put()
                        .objects(cars)
                        .withPutResolver(carStorIOSQLitePutResolver)
                        .prepare()
                        .executeAsBlocking();

                final List<ContentValues> contentValuesList = new ArrayList<ContentValues>(cars.size());
                for (Car car : cars) {
                    final PutResult carPutResult = carsPutResults.results().get(car);
                    //noinspection ConstantConditions
                    final long carId = carPutResult.wasInserted() ? carPutResult.insertedId() : car.id();

                    final ContentValues cv = new ContentValues(2);
                    cv.put(COLUMN_PERSON_ID, personId);
                    cv.put(PersonCarRelationTable.COLUMN_CAR_ID, carId);
                    contentValuesList.add(cv);
                }
                storIOSQLite
                        .put()
                        .contentValues(contentValuesList)
                        .withPutResolver(carPersonRelationPutResolver)
                        .prepare()
                        .executeAsBlocking();
            }
            lowLevel.setTransactionSuccessful();
            return putResult;
        } finally {
            lowLevel.endTransaction();
        }
    }
}