package com.pushtorefresh.storio2.sample.many_to_many_sample.resolvers;

import android.content.ContentValues;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResolver;
import com.pushtorefresh.storio2.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio2.sqlite.queries.InsertQuery;

import static com.pushtorefresh.storio2.sample.many_to_many_sample.entities.PersonCarRelationTable.TABLE;

public class CarPersonRelationPutResolver extends PutResolver<ContentValues> {

    @Override
    @NonNull
    public PutResult performPut(@NonNull StorIOSQLite storIOSQLite, @NonNull ContentValues contentValues) {
        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

        lowLevel.beginTransaction();
        try {
            final InsertQuery insertQuery = InsertQuery.builder()
                    .table(TABLE)
                    .build();

            final long insertedId = lowLevel.insert(insertQuery, contentValues);
            final PutResult putResult = PutResult.newInsertResult(insertedId, insertQuery.table());

            lowLevel.setTransactionSuccessful();
            return putResult;
        } finally {
            lowLevel.endTransaction();
        }
    }
}
