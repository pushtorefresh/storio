package com.pushtorefresh.storio2.sample.many_to_many_sample.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.CarStorIOSQLiteGetResolver;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonStorIOSQLiteGetResolver;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonTable;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.queries.RawQuery;

import java.util.List;

public class CarRelationsGetResolver  extends CarStorIOSQLiteGetResolver {

    @NonNull
    private final PersonStorIOSQLiteGetResolver personStorIOSQLiteGetResolver;

    public CarRelationsGetResolver(@NonNull PersonStorIOSQLiteGetResolver personStorIOSQLiteGetResolver) {
        this.personStorIOSQLiteGetResolver = personStorIOSQLiteGetResolver;
    }

    @Override
    @NonNull
    public Car mapFromCursor(@NonNull StorIOSQLite storIOSQLite, @NonNull Cursor cursor) {
        final Car car = super.mapFromCursor(storIOSQLite, cursor);

        final List<Person> persons = storIOSQLite
                .get()
                .listOfObjects(Person.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT "
                                + PersonTable.TABLE + ".*"
                                + " FROM " + PersonTable.TABLE
                                + " JOIN " + PersonCarRelationTable.TABLE
                                + " ON " + PersonTable.COLUMN_ID + " = " + PersonCarRelationTable.COLUMN_PERSON_ID
                                + " AND " + PersonCarRelationTable.COLUMN_CAR_ID + " = ?")
                        .args(car.id())
                        .build())
                .withGetResolver(personStorIOSQLiteGetResolver)   // without relations to prevent cycling
                .prepare()
                .executeAsBlocking();

        return new Car(car.id(), car.mark(), persons);
    }
}