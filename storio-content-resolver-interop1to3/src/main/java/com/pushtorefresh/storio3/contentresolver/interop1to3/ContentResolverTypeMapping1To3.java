package com.pushtorefresh.storio3.contentresolver.interop1to3;

import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio3.contentresolver.interop1to3.Queries1To3.toV1Query;
import static com.pushtorefresh.storio3.contentresolver.interop1to3.Queries1To3.toV3Query;
import static com.pushtorefresh.storio3.contentresolver.interop1to3.Results1To3.toV1DeleteResult;
import static com.pushtorefresh.storio3.contentresolver.interop1to3.Results1To3.toV1PutResult;
import static com.pushtorefresh.storio3.contentresolver.interop1to3.Results1To3.toV3DeleteResult;

public final class ContentResolverTypeMapping1To3 {

    private ContentResolverTypeMapping1To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.contentresolver.operations.put.PutResolver<T> toV1PutResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.contentresolver.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio.contentresolver.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
                    @NonNull T object
            ) {
                return toV1PutResult(resolver3.performPut(storIOContentResolver3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.contentresolver.operations.get.GetResolver<T> toV1GetResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.contentresolver.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(@NonNull Cursor cursor) {
                return resolver3.mapFromCursor(storIOSQLite3, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
                    @NonNull com.pushtorefresh.storio.contentresolver.queries.Query query1
            ) {
                return resolver3.performGet(storIOSQLite3, toV3Query(query1));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver<T> toV1DeleteResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
                    @NonNull T object
            ) {
                return toV1DeleteResult(resolver3.performDelete(storIOContentResolver3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping<T> toV1ContentResolverTypeMapping(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping<T> mapping3
    ) {
        return com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping.<T>builder()
                .putResolver(toV1PutResolver(storIOContentResolver3, mapping3.putResolver()))
                .getResolver(toV1GetResolver(storIOContentResolver3, mapping3.getResolver()))
                .deleteResolver(toV1DeleteResolver(storIOContentResolver3, mapping3.deleteResolver()))
                .build();
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T> toV3PutResolver(
            @NonNull final com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
            @NonNull final com.pushtorefresh.storio.contentresolver.operations.put.PutResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.contentresolver.operations.put.PutResult performPut(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull T object
            ) {
                return Results1To3.toV3PutResult(resolver1.performPut(storIOContentResolver1, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T> toV3GetResolver(
            @NonNull final com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
            @NonNull final com.pushtorefresh.storio.contentresolver.operations.get.GetResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull Cursor cursor
            ) {
                return resolver1.mapFromCursor(cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull com.pushtorefresh.storio3.contentresolver.queries.Query query3
            ) {
                return resolver1.performGet(storIOContentResolver1, toV1Query(query3));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T> toV3DeleteResolver(
            @NonNull final com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
            @NonNull final com.pushtorefresh.storio.contentresolver.operations.delete.DeleteResolver<T> resolver1
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult performDelete(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull T object
            ) {
                return toV3DeleteResult(resolver1.performDelete(storIOContentResolver1, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping<T> toV3ContentResolverTypeMapping(
            @NonNull final com.pushtorefresh.storio.contentresolver.StorIOContentResolver storIOContentResolver1,
            @NonNull final com.pushtorefresh.storio.contentresolver.ContentResolverTypeMapping<T> mapping1
    ) {
        return com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping.<T>builder()
                .putResolver(toV3PutResolver(storIOContentResolver1, mapping1.putResolver()))
                .getResolver(toV3GetResolver(storIOContentResolver1, mapping1.getResolver()))
                .deleteResolver(toV3DeleteResolver(storIOContentResolver1, mapping1.deleteResolver()))
                .build();
    }
}
