package com.pushtorefresh.storio3.sqlite.interop1to3;

import android.support.annotation.NonNull;

public final class Results1To3 {

    private Results1To3() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static com.pushtorefresh.storio.sqlite.operations.put.PutResult toV1PutResult(
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.put.PutResult result3
    ) {
        final Long insertedId = result3.insertedId();
        if (insertedId != null) {
            return com.pushtorefresh.storio.sqlite.operations.put.PutResult.newInsertResult(
                    insertedId,
                    result3.affectedTables(),
                    result3.affectedTags()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio.sqlite.operations.put.PutResult.newUpdateResult(
                    result3.numberOfRowsUpdated(),
                    result3.affectedTables(),
                    result3.affectedTags()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.operations.put.PutResult toV3PutResult(
            @NonNull final com.pushtorefresh.storio.sqlite.operations.put.PutResult result1
    ) {
        final Long insertedId = result1.insertedId();
        if (insertedId != null) {
            return com.pushtorefresh.storio3.sqlite.operations.put.PutResult.newInsertResult(
                    insertedId,
                    result1.affectedTables(),
                    result1.affectedTags()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio3.sqlite.operations.put.PutResult.newUpdateResult(
                    result1.numberOfRowsUpdated(),
                    result1.affectedTables(),
                    result1.affectedTags()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult toV1DeleteResult(
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult result3
    ) {
        return com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult.newInstance(
                result3.numberOfRowsDeleted(),
                result3.affectedTables(),
                result3.affectedTags()
        );
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult toV3DeleteResult(
            @NonNull final com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult result1
    ) {
        return com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult.newInstance(
                result1.numberOfRowsDeleted(),
                result1.affectedTables(),
                result1.affectedTags()
        );
    }
}
