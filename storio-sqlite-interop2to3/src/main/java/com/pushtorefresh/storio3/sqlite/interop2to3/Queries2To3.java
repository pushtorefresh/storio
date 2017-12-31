package com.pushtorefresh.storio3.sqlite.interop2to3;

import android.support.annotation.NonNull;

public final class Queries2To3 {

    private Queries2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static com.pushtorefresh.storio2.sqlite.queries.Query toV2Query(
            @NonNull final com.pushtorefresh.storio3.sqlite.queries.Query query3
    ) {
        return com.pushtorefresh.storio2.sqlite.queries.Query.builder()
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
            @NonNull final com.pushtorefresh.storio2.sqlite.queries.Query query2
    ) {
        return com.pushtorefresh.storio3.sqlite.queries.Query.builder()
                .table(query2.table())
                .columns(query2.columns().toArray(new String[]{}))
                .where(query2.where())
                .whereArgs(query2.whereArgs().toArray())
                .distinct(query2.distinct())
                .groupBy(query2.groupBy())
                .having(query2.having())
                .orderBy(query2.orderBy())
                .limit(query2.limit())
                .observesTags(query2.observesTags())
                .build();
    }

    @NonNull
    public static com.pushtorefresh.storio2.sqlite.queries.RawQuery toV2RawQuery(
            @NonNull final com.pushtorefresh.storio3.sqlite.queries.RawQuery rawQuery3
    ) {
        return com.pushtorefresh.storio2.sqlite.queries.RawQuery.builder()
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
            @NonNull final com.pushtorefresh.storio2.sqlite.queries.RawQuery rawQuery2
    ) {
        return com.pushtorefresh.storio3.sqlite.queries.RawQuery.builder()
                .query(rawQuery2.query())
                .args(rawQuery2.args().toArray())
                .affectsTables(rawQuery2.affectsTables())
                .affectsTags(rawQuery2.affectsTags())
                .observesTables(rawQuery2.observesTables())
                .observesTags(rawQuery2.observesTags())
                .build();
    }
}
