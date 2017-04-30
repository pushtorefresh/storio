package com.pushtorefresh.storio.sample.db.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.List;

public final class PersonGetResolver extends GetResolver<Person> {

    // Sorry for this hack :(
    // We will pass you an instance of StorIO
    // into the mapFromCursor() in v2.0.0.
    //
    // At the moment, you can save this instance in performGet() and then null it at the end
    @NonNull
    private final ThreadLocal<StorIOSQLite> storIOSQLiteFromPerformGet = new ThreadLocal<StorIOSQLite>();

    @NonNull
    @Override
    public Person mapFromCursor(@NonNull Cursor cursor) {
        final StorIOSQLite storIOSQLite = storIOSQLiteFromPerformGet.get();

        // BTW, you don't need a transaction here
        // StorIO will wrap mapFromCursor() into the transaction if needed

        try {
            final long personId = cursor.getLong(cursor.getColumnIndexOrThrow(PersonsTable.COLUMN_ID));
            final String personName = cursor.getString(cursor.getColumnIndexOrThrow(PersonsTable.COLUMN_NAME));

            final List<Car> personCars = storIOSQLite
                    .get()
                    .listOfObjects(Car.class)
                    .withQuery(Query.builder()
                            .table(CarsTable.TABLE_NAME)
                            .where(CarsTable.COLUMN_PERSON_ID + "=?")
                            .whereArgs(personId)
                            .build())
                    .prepare()
                    .executeAsBlocking();

            return new Person(personId, personName, personCars);
        } finally {
            // Releasing StorIOSQLite reference
            storIOSQLiteFromPerformGet.set(null);
        }
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return storIOSQLite.internal().rawQuery(rawQuery);
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return storIOSQLite.internal().query(query);
    }
}
