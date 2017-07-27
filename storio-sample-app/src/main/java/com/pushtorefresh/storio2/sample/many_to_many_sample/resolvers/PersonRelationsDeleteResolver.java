package com.pushtorefresh.storio2.sample.many_to_many_sample.resolvers;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.Person;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable;
import com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonStorIOSQLiteDeleteResolver;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio2.sqlite.queries.DeleteQuery;

import static com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable.COLUMN_PERSON_ID;

public class PersonRelationsDeleteResolver extends PersonStorIOSQLiteDeleteResolver {

    @Override
    @NonNull
    public DeleteResult performDelete(@NonNull StorIOSQLite storIOSQLite, @NonNull Person object) {
        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();
        lowLevel.beginTransaction();
        try {
            final DeleteResult deleteResult = super.performDelete(storIOSQLite, object);

            storIOSQLite.delete()
                    .byQuery(DeleteQuery.builder()
                            .table(PersonCarRelationTable.TABLE)
                            .where(COLUMN_PERSON_ID + " = ?")
                            .whereArgs(object.id())
                            .build())
                    .prepare()
                    .executeAsBlocking();

            lowLevel.setTransactionSuccessful();

            return deleteResult;
        } finally {
            lowLevel.endTransaction();
        }
    }
}