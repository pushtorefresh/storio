package com.pushtorefresh.storio3.contentresolver.interop1to3;

import android.support.annotation.NonNull;

public final class Queries1To3 {

    private Queries1To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio.contentresolver.queries.Query toV1Query(
            @NonNull final com.pushtorefresh.storio3.contentresolver.queries.Query query3
    ) {
        return com.pushtorefresh.storio.contentresolver.queries.Query.builder()
                .uri(query3.uri())
                .columns(query3.columns().toArray(new String[]{}))
                .where(query3.where())
                .whereArgs(query3.whereArgs().toArray())
                .sortOrder(query3.sortOrder())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.queries.Query toV3Query(
            @NonNull final com.pushtorefresh.storio.contentresolver.queries.Query query1
    ) {
        return com.pushtorefresh.storio3.contentresolver.queries.Query.builder()
                .uri(query1.uri())
                .columns(query1.columns().toArray(new String[]{}))
                .where(query1.where())
                .whereArgs(query1.whereArgs().toArray())
                .sortOrder(query1.sortOrder())
                .build();
    }
}
