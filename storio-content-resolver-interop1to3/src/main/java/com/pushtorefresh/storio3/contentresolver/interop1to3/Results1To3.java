package com.pushtorefresh.storio3.contentresolver.interop1to3;

import android.net.Uri;
import android.support.annotation.NonNull;

public final class Results1To3 {

    private Results1To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio.contentresolver.operations.put.PutResult toV1PutResult(
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.put.PutResult result3
    ) {
        final Uri insertedUri = result3.insertedUri();
        if (insertedUri != null) {
            return com.pushtorefresh.storio.contentresolver.operations.put.PutResult.newInsertResult(
                    insertedUri,
                    result3.affectedUri()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio.contentresolver.operations.put.PutResult.newUpdateResult(
                    result3.numberOfRowsUpdated(),
                    result3.affectedUri()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.operations.put.PutResult toV3PutResult(
            @NonNull final com.pushtorefresh.storio.contentresolver.operations.put.PutResult result1
    ) {
        final Uri insertedUri = result1.insertedUri();
        if (insertedUri != null) {
            return com.pushtorefresh.storio3.contentresolver.operations.put.PutResult.newInsertResult(
                    insertedUri,
                    result1.affectedUri()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio3.contentresolver.operations.put.PutResult.newUpdateResult(
                    result1.numberOfRowsUpdated(),
                    result1.affectedUri()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult toV1DeleteResult(
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult result3
    ) {
        return com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult.newInstance(
                result3.numberOfRowsDeleted(),
                result3.affectedUris()
        );
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult toV3DeleteResult(
            @NonNull final com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult result1
    ) {
        return com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult.newInstance(
                result1.numberOfRowsDeleted(),
                result1.affectedUris()
        );
    }
}
