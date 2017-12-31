package com.pushtorefresh.storio3.contentresolver.interop2to3;

import android.database.Cursor;
import android.support.annotation.NonNull;

import static com.pushtorefresh.storio3.contentresolver.interop2to3.Queries2To3.toV2Query;
import static com.pushtorefresh.storio3.contentresolver.interop2to3.Queries2To3.toV3Query;
import static com.pushtorefresh.storio3.contentresolver.interop2to3.Results2To3.toV2DeleteResult;
import static com.pushtorefresh.storio3.contentresolver.interop2to3.Results2To3.toV2PutResult;
import static com.pushtorefresh.storio3.contentresolver.interop2to3.Results2To3.toV3DeleteResult;

public final class ContentResolverTypeMapping2To3 {

    private ContentResolverTypeMapping2To3() {
        throw new IllegalStateException("No instances please.");
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.contentresolver.operations.put.PutResolver<T> toV2PutResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.contentresolver.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio2.contentresolver.operations.put.PutResult performPut(
                    @NonNull com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
                    @NonNull T object
            ) {
                return toV2PutResult(resolver3.performPut(storIOContentResolver3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.contentresolver.operations.get.GetResolver<T> toV2GetResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOSQLite3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.contentresolver.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
                    @NonNull Cursor cursor
            ) {
                return resolver3.mapFromCursor(storIOSQLite3, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
                    @NonNull com.pushtorefresh.storio2.contentresolver.queries.Query query2
            ) {
                return resolver3.performGet(storIOSQLite3, toV3Query(query2));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResolver<T> toV2DeleteResolver(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T> resolver3
    ) {
        return new com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResult performDelete(
                    @NonNull com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
                    @NonNull T object
            ) {
                return toV2DeleteResult(resolver3.performDelete(storIOContentResolver3, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping<T> toV2ContentResolverTypeMapping(
            @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
            @NonNull final com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping<T> mapping3
    ) {
        return com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping.<T>builder()
                .putResolver(toV2PutResolver(storIOContentResolver3, mapping3.putResolver()))
                .getResolver(toV2GetResolver(storIOContentResolver3, mapping3.getResolver()))
                .deleteResolver(toV2DeleteResolver(storIOContentResolver3, mapping3.deleteResolver()))
                .build();
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T> toV3PutResolver(
            @NonNull final com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
            @NonNull final com.pushtorefresh.storio2.contentresolver.operations.put.PutResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.put.PutResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.contentresolver.operations.put.PutResult performPut(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull T object
            ) {
                return Results2To3.toV3PutResult(resolver2.performPut(storIOContentResolver2, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T> toV3GetResolver(
            @NonNull final com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
            @NonNull final com.pushtorefresh.storio2.contentresolver.operations.get.GetResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.get.GetResolver<T>() {
            @Override
            @NonNull
            public T mapFromCursor(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull Cursor cursor
            ) {
                return resolver2.mapFromCursor(storIOContentResolver2, cursor);
            }

            @Override
            @NonNull
            public Cursor performGet(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull com.pushtorefresh.storio3.contentresolver.queries.Query query3
            ) {
                return resolver2.performGet(storIOContentResolver2, toV2Query(query3));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T> toV3DeleteResolver(
            @NonNull final com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
            @NonNull final com.pushtorefresh.storio2.contentresolver.operations.delete.DeleteResolver<T> resolver2
    ) {
        return new com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResolver<T>() {
            @Override
            @NonNull
            public com.pushtorefresh.storio3.contentresolver.operations.delete.DeleteResult performDelete(
                    @NonNull final com.pushtorefresh.storio3.contentresolver.StorIOContentResolver storIOContentResolver3,
                    @NonNull T object
            ) {
                return toV3DeleteResult(resolver2.performDelete(storIOContentResolver2, object));
            }
        };
    }

    @NonNull
    public static <T> com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping<T> toV3ContentResolverTypeMapping(
            @NonNull final com.pushtorefresh.storio2.contentresolver.StorIOContentResolver storIOContentResolver2,
            @NonNull final com.pushtorefresh.storio2.contentresolver.ContentResolverTypeMapping<T> mapping2
    ) {
        return com.pushtorefresh.storio3.contentresolver.ContentResolverTypeMapping.<T>builder()
                .putResolver(toV3PutResolver(storIOContentResolver2, mapping2.putResolver()))
                .getResolver(toV3GetResolver(storIOContentResolver2, mapping2.getResolver()))
                .deleteResolver(toV3DeleteResolver(storIOContentResolver2, mapping2.deleteResolver()))
                .build();
    }
}
