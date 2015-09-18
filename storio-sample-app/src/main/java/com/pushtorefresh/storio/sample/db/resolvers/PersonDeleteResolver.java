package com.pushtorefresh.storio.sample.db.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;

import java.util.HashSet;
import java.util.Set;

public final class PersonDeleteResolver extends DeleteResolver<Person> {
    @NonNull
    @Override
    public DeleteResult performDelete(@NonNull StorIOSQLite storIOSQLite, @NonNull Person person) {
//        if (person.id() == null) {
//            throw new IllegalStateException("Can not delete person without id! Person = " + person);
//        }

        // For consistency and performance (we are going to affect two tables)
        // we will open transaction
        storIOSQLite.internal().beginTransaction();

        try {
            storIOSQLite
                    .internal()
                    .delete(DeleteQuery.builder()
                                    .table(PersonsTable.TABLE_NAME)
//                                    .where(PersonsTable.COLUMN_ID)
//                                    .whereArgs(person.id())
                                    .where(PersonsTable.COLUMN_UUID)
                                    .whereArgs(person.uuid())
                                    .build()
                    );

            final Set<String> affectedTables = new HashSet<>(2);
            affectedTables.add(PersonsTable.TABLE_NAME);

            if (!person.cars().isEmpty()) {
                storIOSQLite
                        .delete()
                        .objects(person.cars())
                        .prepare()
                        .executeAsBlocking();

                affectedTables.add(CarsTable.TABLE_NAME);
            }

            storIOSQLite.internal().setTransactionSuccessful();

            return DeleteResult.newInstance(1, affectedTables);
        } finally {
            storIOSQLite.internal().endTransaction();
        }
    }
}