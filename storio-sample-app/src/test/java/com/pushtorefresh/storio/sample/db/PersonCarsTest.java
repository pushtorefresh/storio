package com.pushtorefresh.storio.sample.db;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.sample.db.entities.Car;
import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.resolvers.CarDeleteResolver;
import com.pushtorefresh.storio.sample.db.resolvers.CarGetResolver;
import com.pushtorefresh.storio.sample.db.resolvers.CarPutResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonDeleteResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonGetResolver;
import com.pushtorefresh.storio.sample.db.resolvers.PersonPutResolver;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public final class PersonCarsTest {

    private StorIOSQLite storIOSQLite;

    // Each test case will have clear db state
    @Before
    public void beforeEachTestCase() {
        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DbOpenHelper(RuntimeEnvironment.application))
                .addTypeMapping(Person.class, SQLiteTypeMapping.<Person>builder()
                        .putResolver(new PersonPutResolver())
                        .getResolver(new PersonGetResolver())
                        .deleteResolver(new PersonDeleteResolver())
                        .build())
                .addTypeMapping(Car.class, SQLiteTypeMapping.<Car>builder()
                        .putResolver(new CarPutResolver())
                        .getResolver(new CarGetResolver())
                        .deleteResolver(new CarDeleteResolver())
                        .build())
                .build();
    }

    @Test
    public void insertPersonWithoutCars() {
        Person person = new Person(null, "Rainer-Lang", Collections.<Car>emptyList());

        PutResult putResult = storIOSQLite
                .put()
                .object(person)
                .prepare()
                .executeAsBlocking();

        assertThat(putResult.wasInserted()).isTrue();

        List<Person> personsFromTheDb = storIOSQLite
                .get()
                .listOfObjects(Person.class)
                .withQuery(Query.builder()
                                .table(PersonsTable.TABLE_NAME)
                                .build()
                )
                .prepare()
                .executeAsBlocking();

        assertThat(personsFromTheDb).hasSize(1);

        Person personFromDb = personsFromTheDb.get(0);

        assertThat(personFromDb.id()).isNotNull();
        assertThat(personFromDb.name()).isEqualTo("Rainer-Lang");
        assertThat(personFromDb.cars()).isEqualTo(Collections.emptyList());
    }

    @Test
    public void insertPersonAndThenUpdateWithCars() {
        Person personToInsert = new Person(null, "Rainer-Lang", Collections.<Car>emptyList());

        PutResult putResult = storIOSQLite
                .put()
                .object(personToInsert)
                .prepare()
                .executeAsBlocking();

        assertThat(putResult.wasInserted()).isTrue();

        List<Car> cars = asList(new Car(null, putResult.insertedId(), "BMW 320i"));

        Person personToUpdate = new Person(putResult.insertedId(), "Rainer-Lang", cars);

        storIOSQLite
                .put()
                .object(personToUpdate)
                .prepare()
                .executeAsBlocking();

        List<Person> personsFromDb = storIOSQLite
                .get()
                .listOfObjects(Person.class)
                .withQuery(Query.builder()
                        .table(PersonsTable.TABLE_NAME)
                        .build())
                .prepare()
                .executeAsBlocking();

        assertThat(personsFromDb).hasSize(1);

        Person personFromDb = personsFromDb.get(0);

        assertThat(personFromDb.id()).isNotNull();

        assertThat(personFromDb.cars()).hasSize(1);
        assertThat(personFromDb.cars().get(0).id()).isNotNull();
        assertThat(personFromDb.cars().get(0).personId()).isEqualTo(personFromDb.id());
        assertThat(personFromDb.cars().get(0).model()).isEqualTo("BMW 320i");
    }
}
