package com.pushtorefresh.storio3.sqlite.interop1to3;

import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio3.sqlite.interop1to3.Queries1To3.toV1Query;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Queries1To3.toV1RawQuery;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Queries1To3.toV3Query;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Queries1To3.toV3RawQuery;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Results1To3.toV1DeleteResult;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Results1To3.toV1PutResult;
import static com.pushtorefresh.storio3.sqlite.interop1to3.Results1To3.toV3DeleteResult;

public final class SQLiteTypeMapping1To3 {

    private SQLiteTypeMapping1To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.sqlite.operations.put.PutResolver<T> toV1PutResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite sqlite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.sqlite.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio.sqlite.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
                    @NonNull T object
            ) {
                return toV1PutResult(resolver3.performPut(sqlite3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.sqlite.operations.get.GetResolver<T> toV1GetResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.sqlite.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(@NonNull Cursor cursor) {
                return resolver3.mapFromCursor(storIOSQLite3, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
                    @NonNull com.pushtorefresh.storio.sqlite.queries.RawQuery rawQuery1
            ) {
                return resolver3.performGet(storIOSQLite3, toV3RawQuery(rawQuery1));
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
                    @NonNull com.pushtorefresh.storio.sqlite.queries.Query query1
            ) {
                return resolver3.performGet(storIOSQLite3, toV3Query(query1));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver<T> toV1DeleteResolver(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
                    @NonNull T object
            ) {
                return toV1DeleteResult(resolver3.performDelete(storIOSQLite3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.sqlite.SQLiteTypeMapping<T> toV1SQLiteTypeMapping(
            @NonNull final com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping<T> mapping3
    ) {
        return com.pushtorefresh.storio.sqlite.SQLiteTypeMapping.<T>builder()
                .putResolver(toV1PutResolver(storIOSQLite3, mapping3.putResolver()))
                .getResolver(toV1GetResolver(storIOSQLite3, mapping3.getResolver()))
                .deleteResolver(toV1DeleteResolver(storIOSQLite3, mapping3.deleteResolver()))
                .build();
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T> toV3PutResolver(
            @NonNull final com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
            @NonNull final com.pushtorefresh.storio.sqlite.operations.put.PutResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.sqlite.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull T object
            ) {
                return Results1To3.toV3PutResult(resolver1.performPut(storIOSQLite1, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T> toV3GetResolver(
            @NonNull final com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
            @NonNull final com.pushtorefresh.storio.sqlite.operations.get.GetResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull Cursor cursor
            ) {
                return resolver1.mapFromCursor(cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull com.pushtorefresh.storio3.sqlite.queries.RawQuery rawQuery3
            ) {
                return resolver1.performGet(storIOSQLite1, toV1RawQuery(rawQuery3));
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull com.pushtorefresh.storio3.sqlite.queries.Query query3
            ) {
                return resolver1.performGet(storIOSQLite1, toV1Query(query3));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T> toV3DeleteResolver(
            @NonNull final com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
            @NonNull final com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.sqlite.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio3.sqlite.StorIOSQLite storIOSQLite3,
                    @NonNull T object
            ) {
                return toV3DeleteResult(resolver1.performDelete(storIOSQLite1, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping<T> toV3SQLiteTypeMapping(
            @NonNull final com.pushtorefresh.storio.sqlite.StorIOSQLite storIOSQLite1,
            @NonNull final com.pushtorefresh.storio.sqlite.SQLiteTypeMapping<T> mapping1
    ) {
        return com.pushtorefresh.storio3.sqlite.SQLiteTypeMapping.<T>builder()
                .putResolver(toV3PutResolver(storIOSQLite1, mapping1.putResolver()))
                .getResolver(toV3GetResolver(storIOSQLite1, mapping1.getResolver()))
                .deleteResolver(toV3DeleteResolver(storIOSQLite1, mapping1.deleteResolver()))
                .build();
    }
}
