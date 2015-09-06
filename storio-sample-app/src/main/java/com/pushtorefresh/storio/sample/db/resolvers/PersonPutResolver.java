package com.pushtorefresh.storio.sample.db.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sample.db.entities.Person;
import com.pushtorefresh.storio.sample.db.tables.CarsTable;
import com.pushtorefresh.storio.sample.db.tables.PersonsTable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import java.util.HashSet;
import java.util.Set;

public class PersonPutResolver extends PutResolver<Person> {

    @NonNull
    @Override
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull Person person) {
        // We can even reuse StorIO methods

        // TODO putResults?
        final PutResults<Object> putResults = null;

        if (person.getId() == null ) {
            // insert
            storIOSQLite
                    .internal()
                    .insert(mapToInsertQuery(person), mapToContentValues(person));
        }
        else {
            // update
            storIOSQLite
                    .internal()
                    .update(mapToUpdateQuery(person), mapToContentValues(person));
        }

        final Set<String> affectedTables = new HashSet<String>(2);

        affectedTables.add(PersonsTable.TABLE);
        affectedTables.add(CarsTable.TABLE);

        // Actually, it's not very clear what PutResult should we return here…
        // Because there is no table for this pair of tweet and user
        // So, let's just return Update Result
        return PutResult.newUpdateResult(putResults.numberOfUpdates(), affectedTables);
    }

    @NonNull
    protected InsertQuery mapToInsertQuery(@NonNull Person object) {
        return InsertQuery.builder()
                .table(PersonsTable.TABLE)
                .build();
    }

    @NonNull
    protected UpdateQuery mapToUpdateQuery(@NonNull Person object) {
        return UpdateQuery.builder()
                .table(PersonsTable.TABLE)
                .where(PersonsTable.COLUMN_ID + " = ?")
                .whereArgs(object.id)
                .build();
    }

    @NonNull
    public ContentValues mapToContentValues(@NonNull Person object) {
        ContentValues contentValues = new ContentValues(2);

        contentValues.put(PersonsTable.COLUMN_NAME, object.name);
        contentValues.put(PersonsTable.COLUMN_ID, object.id);

        return contentValues;
    }
}
