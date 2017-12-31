package com.pushtorefresh.storio3.contentresolver.interop2to3;

import android.net.Uri;
import android.support.annotation.NonNull;

public final class Results2To3 {

    private Results2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio2.contentresolver.operations.put.PutResult toV2PutResult(
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.put.PutResult result3
    ) {
        final Uri insertedUri = result3.insertedUri();
        if (insertedUri != null) {
            return com.pushtorefresh.storio2.contentresolver.operations.put.PutResult.newInsertResult(
                    insertedUri,
                    result3.affectedUri()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio2.contentresolver.operations.put.PutResult.newUpdateResult(
                    result3.numberOfRowsUpdated(),
                    result3.affectedUri()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.operations.put.PutResult toV3PutResult(
            @NonNull final com.pushtorefresh.storio2.contentresolver.operations.put.PutResult result2
    ) {
        final Uri insertedUri = result2.insertedUri();
        if (insertedUri != null) {
            return com.pushtorefresh.storio3.contentresolver.operations.put.PutResult.newInsertResult(
                    insertedUri,
                    result2.affectedUri()
            );
        } else {
            //noinspection ConstantConditions   Must be update result here.
            return com.pushtorefresh.storio3.contentresolver.operations.put.PutResult.newUpdateResult(
                    result2.numberOfRowsUpdated(),
                    result2.affectedUri()
            );
        }
    }

    @NonNull
    public static com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult toV2DeleteResult(
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult result3
    ) {
        return com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult.newInstance(
                result3.numberOfRowsDeleted(),
                result3.affectedUris()
        );
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult toV3DeleteResult(
            @NonNull final com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult result2
    ) {
        return com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult.newInstance(
                result2.numberOfRowsDeleted(),
                result2.affectedUris()
        );
    }
}
