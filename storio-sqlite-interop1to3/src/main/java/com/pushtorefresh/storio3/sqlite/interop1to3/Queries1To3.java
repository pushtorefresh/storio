package com.pushtorefresh.storio3.sqlite.interop1to3;

import android.support.annotation.NonNull;

public final class Queries1To3 {

    private Queries1To3() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static com.pushtorefresh.storio.sqlite.queries.Query toV1Query(
            @NonNull final com.pushtorefresh.storio3.sqlite.queries.Query query3
    ) {
        return com.pushtorefresh.storio.sqlite.queries.Query.builder()
                .table(query3.table())
                .columns(query3.columns().toArray(new String[]{}))
                .where(query3.where())
                .whereArgs(query3.whereArgs().toArray())
                .distinct(query3.distinct())
                .groupBy(query3.groupBy())
                .having(query3.having())
                .orderBy(query3.orderBy())
                .limit(query3.limit())
                .observesTags(query3.observesTags())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.queries.Query toV3Query(
            @NonNull final com.pushtorefresh.storio.sqlite.queries.Query query1
    ) {
        return com.pushtorefresh.storio3.sqlite.queries.Query.builder()
                .table(query1.table())
                .columns(query1.columns().toArray(new String[]{}))
                .where(query1.where())
                .whereArgs(query1.whereArgs().toArray())
                .distinct(query1.distinct())
                .groupBy(query1.groupBy())
                .having(query1.having())
                .orderBy(query1.orderBy())
                .limit(query1.limit())
                .observesTags(query1.observesTags())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio.sqlite.queries.RawQuery toV1RawQuery(
            @NonNull final com.pushtorefresh.storio3.sqlite.queries.RawQuery rawQuery3
    ) {
        return com.pushtorefresh.storio.sqlite.queries.RawQuery.builder()
                .query(rawQuery3.query())
                .args(rawQuery3.args().toArray())
                .affectsTables(rawQuery3.affectsTables())
                .affectsTags(rawQuery3.affectsTags())
                .observesTables(rawQuery3.observesTables())
                .observesTags(rawQuery3.observesTags())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio3.sqlite.queries.RawQuery toV3RawQuery(
            @NonNull final com.pushtorefresh.storio.sqlite.queries.RawQuery rawQuery1
    ) {
        return com.pushtorefresh.storio3.sqlite.queries.RawQuery.builder()
                .query(rawQuery1.query())
                .args(rawQuery1.args().toArray())
                .affectsTables(rawQuery1.affectsTables())
                .affectsTags(rawQuery1.affectsTags())
                .observesTables(rawQuery1.observesTables())
                .observesTags(rawQuery1.observesTags())
                .build();
    }
}
