package com.pushtorefresh.storio3.contentresolver.interop2to3;

import android.support.annotation.NonNull;

public final class Queries2To3 {

    private Queries2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio2.contentresolver.queries.Query toV2Query(
            @NonNull final com.pushtorefresh.storio3.contentresolver.queries.Query query3
    ) {
        return com.pushtorefresh.storio2.contentresolver.queries.Query.builder()
                .uri(query3.uri())
                .columns(query3.columns().toArray(new String[]{}))
                .where(query3.where())
                .whereArgs(query3.whereArgs().toArray())
                .sortOrder(query3.sortOrder())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio3.contentresolver.queries.Query toV3Query(
            @NonNull final com.pushtorefresh.storio2.contentresolver.queries.Query query2
    ) {
        return com.pushtorefresh.storio3.contentresolver.queries.Query.builder()
                .uri(query2.uri())
                .columns(query2.columns().toArray(new String[]{}))
                .where(query2.where())
                .whereArgs(query2.whereArgs().toArray())
                .sortOrder(query2.sortOrder())
                .build();
    }
}
