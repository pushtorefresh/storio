package com.pushtorefresh.storio3.sqlite.interop2to3;

import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio3.sqlite.interop2to3.Queries2To3.toV2Query;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Queries2To3.toV2RawQuery;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Queries2To3.toV3Query;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Queries2To3.toV3RawQuery;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Results2To3.toV2DeleteResult;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Results2To3.toV2PutResult;
import static com.pushtorefresh.storio3.sqlite.interop2to3.Results2To3.toV3DeleteResult;

public final class SQLiteTypeMapping2To3 {

    private SQLiteTypeMapping2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.sqlite.operations.put.PutResolver<T> toV2PutResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite sqlite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.sqlite.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio2.sqlite.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
                    @NonNull T object
            ) {
                return toV2PutResult(resolver3.performPut(sqlite3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.sqlite.operations.get.GetResolver<T> toV2GetResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.sqlite.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
                    @NonNull Cursor cursor
            ) {
                return resolver3.mapFromCursor(storIOSQLite3, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
                    @NonNull com.pushtorefresh.storio2.sqlite.queries.RawQuery rawQuery2
            ) {
                return resolver3.performGet(storIOSQLite3, toV3RawQuery(rawQuery2));
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
                    @NonNull com.pushtorefresh.storio2.sqlite.queries.Query query2
            ) {
                return resolver3.performGet(storIOSQLite3, toV3Query(query2));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver<T> toV2DeleteResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
                    @NonNull T object
            ) {
                return toV2DeleteResult(resolver3.performDelete(storIOSQLite3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping<T> toV2SQLiteTypeMapping(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite sqlite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping<T> mapping3
    ) {
        return com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping.<T>builder()
                .putResolver(toV2PutResolver(sqlite3, mapping3.putResolver()))
                .getResolver(toV2GetResolver(sqlite3, mapping3.getResolver()))
                .deleteResolver(toV2DeleteResolver(sqlite3, mapping3.deleteResolver()))
                .build();
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T> toV3PutResolver(
            @NonNull final com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
            @NonNull final com.pushtorefresh.storio2.sqlite.operations.put.PutResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.sqlite.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull T object
            ) {
                return Results2To3.toV3PutResult(resolver2.performPut(storIOSQLite2, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T> toV3GetResolver(
            @NonNull final com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
            @NonNull final com.pushtorefresh.storio2.sqlite.operations.get.GetResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull Cursor cursor
            ) {
                return resolver2.mapFromCursor(storIOSQLite2, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull com.pushtorefresh.storio3.sqlite.queries.RawQuery rawQuery3
            ) {
                return resolver2.performGet(storIOSQLite2, toV2RawQuery(rawQuery3));
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull com.pushtorefresh.storio3.sqlite.queries.Query query3
            ) {
                return resolver2.performGet(storIOSQLite2, toV2Query(query3));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T> toV3DeleteResolver(
            @NonNull final com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
            @NonNull final com.pushtorefresh.storio2.sqlite.operations.delete.DeleteResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull T object
            ) {
                return toV3DeleteResult(resolver2.performDelete(storIOSQLite2, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping<T> toV3SQLiteTypeMapping(
            @NonNull final com.pushtorefresh.storio2.sqlite.StorIOSQLite storIOSQLite2,
            @NonNull final com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping<T> mapping2
    ) {
        return com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping.<T>builder()
                .putResolver(toV3PutResolver(storIOSQLite2, mapping2.putResolver()))
                .getResolver(toV3GetResolver(storIOSQLite2, mapping2.getResolver()))
                .deleteResolver(toV3DeleteResolver(storIOSQLite2, mapping2.deleteResolver()))
                .build();
    }
}
