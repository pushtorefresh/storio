package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.UsersTable;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;

import java.util.ArrayList;
import java.util.List;

public class PersonGetResolver extends DefaultGetResolver<Person> {

    // We expect that cursor will contain both Tweet and User: SQL JOIN
    @NonNull
    @Override
    public Person mapFromCursor(@NonNull Cursor cursor) {
        final Person person = Person.newPerson(
                cursor.getLong(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndexOrThrow(UsersTable.COLUMN_NICK))
        );

        List<Car> cars = new ArrayList<Car>();
        while (cursor.moveToNext()) {
            cars.add(Car.newCar(
                    cursor.getLong(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_ID)),
                    person.getId(),
                    cursor.getString(cursor.getColumnIndexOrThrow(CarsTable.COLUMN_MODEL))
            ));
        }

        person.setCars(cars);

        return person;
    }
}
