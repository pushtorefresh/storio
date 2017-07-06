package com.pushtorefresh.storio.sample.many_to_many_sample.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.CarStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonStorIOSQLitePutResolver;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import java.util.ArrayList;
import java.util.List;

import static com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonCarRelationTable.COLUMN_CAR_ID;
import static com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonCarRelationTable.COLUMN_PERSON_ID;

public class CarRelationsPutResolver extends CarStorIOSQLitePutResolver {

    @NonNull
    private final PersonStorIOSQLitePutResolver personStorIOSQLitePutResolver;

    @NonNull
    private final CarPersonRelationPutResolver carPersonRelationPutResolver;

    public CarRelationsPutResolver(
            @NonNull PersonStorIOSQLitePutResolver personStorIOSQLitePutResolver,
            @NonNull CarPersonRelationPutResolver carPersonRelationPutResolver
    ) {
        this.personStorIOSQLitePutResolver = personStorIOSQLitePutResolver;
        this.carPersonRelationPutResolver = carPersonRelationPutResolver;
    }

    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull Car object) {
        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        lowLevel.beginTransaction();
        try {
            final PutResult putResult = super.performPut(storIOSQLite, object);
            //noinspection ConstantConditions
            final long carId = putResult.wasInserted() ? putResult.insertedId() : object.id();

            storIOSQLite.delete()
                    .byQuery(DeleteQuery.builder().table(PersonCarRelationTable.TABLE)
                            .where(COLUMN_CAR_ID + " = ?")
                            .whereArgs(carId)
                            .build())
                    .prepare()
                    .executeAsBlocking();

            final List<Person> persons = object.persons();
            if (persons != null) {
                storIOSQLite.put()
                        .objects(persons)
                        .withPutResolver(personStorIOSQLitePutResolver)
                        .prepare()
                        .executeAsBlocking();

                final List<ContentValues> contentValuesList = new ArrayList<ContentValues>(persons.size());

                for (Person person : persons) {
                    final ContentValues cv = new ContentValues(2);
                    cv.put(COLUMN_CAR_ID, carId);
                    cv.put(COLUMN_PERSON_ID, person.id());
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