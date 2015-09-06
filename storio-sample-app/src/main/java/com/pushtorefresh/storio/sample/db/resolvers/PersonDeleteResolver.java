package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;

import java.util.HashSet;
import java.util.Set;

import static java.util.Arrays.asList;

public class PersonDeleteResolver extends DeleteResolver<Person> {
    @NonNull
    @Override
    public DeleteResult performDelete(@NonNull StorIOSQLite storIOSQLite, @NonNull Person person) {
        // We can even reuse StorIO methods
        
        storIOSQLite.internal().beginTransaction();

        // first delete person
        storIOSQLite
                .internal()
                .delete(DeleteQuery.builder()
                        .table(PersonsTable.TABLE)
                        .where(PersonsTable.COLUMN_ID + " = ?")
                        .whereArgs(person.getId())
                        .build());

        // delete cars
        storIOSQLite
                .delete()
                .objects(person.getCars())
                .prepare()
                .executeAsBlocking();

        storIOSQLite.internal().endTransaction();

        final Set<String> affectedTables = new HashSet<String>(2);

        affectedTables.add(PersonsTable.TABLE);
        affectedTables.add(CarsTable.TABLE);

        return DeleteResult.newInstance(2, affectedTables);
    }
}
