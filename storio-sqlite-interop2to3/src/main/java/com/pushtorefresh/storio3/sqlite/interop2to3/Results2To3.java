package com.pushtorefresh.storio3.sqlite.interop2to3;

import android.support.annotation.NonNull;

public final class Results2To3 {

    private Results2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio2.sqlite.operations.put.PutResult toV2PutResult(
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.put.PutResult result3
    ) {
        final Long insertedId = result3.insertedId();
        if (insertedId != null) {
            return com.pushtorefresh.storio2.sqlite.operations.put.PutResult.newInsertResult(
                    insertedId,
                    result3.affectedTables(),
                    result3.affectedTags()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio2.sqlite.operations.put.PutResult.newUpdateResult(
                    result3.numberOfRowsUpdated(),
                    result3.affectedTables(),
                    result3.affectedTags()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.operations.put.PutResult toV3PutResult(
            @NonNull final com.pushtorefresh.storio2.sqlite.operations.put.PutResult result2
    ) {
        final Long insertedId = result2.insertedId();
        if (insertedId != null) {
            return com.pushtorefresh.storio3.sqlite.operations.put.PutResult.newInsertResult(
                    insertedId,
                    result2.affectedTables(),
                    result2.affectedTags()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio3.sqlite.operations.put.PutResult.newUpdateResult(
                    result2.numberOfRowsUpdated(),
                    result2.affectedTables(),
                    result2.affectedTags()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult toV2DeleteResult(
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult result3
    ) {
        return com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult.newInstance(
                result3.numberOfRowsDeleted(),
                result3.affectedTables(),
                result3.affectedTags()
        );
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult toV3DeleteResult(
            @NonNull final com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult result2
    ) {
        return com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult.newInstance(
                result2.numberOfRowsDeleted(),
                result2.affectedTables(),
                result2.affectedTags()
        );
    }
}
