package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

public class PersonGetResolver extends DefaultGetResolver<Person> {

//    @NonNull
//    @Override
//    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
//
//        // TODO should be used to get persons AND cars
//
//        RawQuery rQ = RawQuery.builder()
//                .query("select p._id, p.name, c._id, c.model from persons p left join cars c on p.id=c.id_person")
//                .build();
//
//        return storIOSQLite.internal().rawQuery(rQ);
//    }
//
//    @NonNull
//    @Override
//    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
//
//        // TODO should not be used to get persons AND cars
//
//        return storIOSQLite.internal().query(query);
//    }

    @NonNull
    @Override
    public Person mapFromCursor(@NonNull Cursor cursor) {
        final Person person = Person.newPerson(
                cursor.getLong(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_NICK))
        );

//        List<Car> cars = new ArrayList<>();
//        while (cursor.moveToNext()) {
//            cars.add(Car.newCar(
//                    cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_ID)),
//                    cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_MODEL)),
//                    person.getId()
//            ));
//        }
//        person.setCars(cars);

        return person;
    }
}
