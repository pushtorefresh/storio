package com.pushtorefresh.storio.sample.many_to_many_sample.resolvers;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Car;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.CarStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.CarTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio.sample.many_to_many_sample.entities.PersonStorIOSQLiteGetResolver;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import java.util.List;

public class PersonRelationsGetResolver extends PersonStorIOSQLiteGetResolver {

    @NonNull
    private final CarStorIOSQLiteGetResolver carStorIOSQLiteGetResolver;

    // Sorry for this hack :(
    // We will pass you an instance of StorIO
    // into the mapFromCursor() in v2.0.0.
    //
    // At the moment, you can save this instance in performGet() and then null it at the end
    @NonNull
    private final ThreadLocal<StorIOSQLite> storIOSQLiteFromPerformGet = new ThreadLocal<StorIOSQLite>();

    public PersonRelationsGetResolver(@NonNull CarStorIOSQLiteGetResolver carStorIOSQLiteGetResolver) {
        this.carStorIOSQLiteGetResolver = carStorIOSQLiteGetResolver;
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull RawQuery rawQuery) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return super.performGet(storIOSQLite, rawQuery);
    }

    @NonNull
    @Override
    public Cursor performGet(@NonNull StorIOSQLite storIOSQLite, @NonNull Query query) {
        storIOSQLiteFromPerformGet.set(storIOSQLite);
        return super.performGet(storIOSQLite, query);
    }

    @Override
    @NonNull
    public Person mapFromCursor(@NonNull Cursor cursor) {
        final StorIOSQLite storIOSQLite = storIOSQLiteFromPerformGet.get();
        final Person person = super.mapFromCursor(cursor);

        final List<Car> cars = storIOSQLite
                .get()
                .listOfObjects(Car.class)
                .withQuery(RawQuery.builder()
                        .query("SELECT "
                                + CarTable.TABLE + ".*"
                                + " FROM " + CarTable.TABLE
                                + " JOIN " + PersonCarRelationTable.TABLE
                                + " ON " + CarTable.COLUMN_ID + " = " + PersonCarRelationTable.COLUMN_CAR_ID
                                + " AND " + PersonCarRelationTable.COLUMN_PERSON_ID + " = ?")
                        .args(person.id())
                        .build())
                .withGetResolver(carStorIOSQLiteGetResolver)
                .prepare()
                .executeAsBlocking();

        return new Person(person.id(), person.name(), cars);
    }
}