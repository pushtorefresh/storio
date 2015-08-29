package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class PersonPutResolver extends PutResolver<Person> {

    @NonNull
    @Override
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull Person person) {
        // We can even reuse StorIO methods
        final PutResults<Object> putResults = storIOSQLite
                .put()

                // TODO here I need help
                .objects(asList(person, person.getCars()))

                .prepare() // BTW: it will use transaction!
                .executeAsBlocking();

        final Set<String> affectedTables = new HashSet<String>(2);

        affectedTables.add(PersonsTable.TABLE);
        affectedTables.add(CarsTable.TABLE);

        // Actually, it's not very clear what PutResult should we return here…
        // Because there is no table for this pair of tweet and user
        // So, let's just return Update Result
        return PutResult.newUpdateResult(putResults.numberOfUpdates(), affectedTables);
    }
}
