package com.pushtorefresh.storio.contentresolver;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;
import com.pushtorefresh.storio.operation.MapFunc;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * ContentResolver Type default values for object mapping
 *
 * @param <T> type
 */
public class ContentResolverTypeDefaults<T> {

    @NonNull
    public final MapFunc<T, ContentValues> mapToContentValues;

    @NonNull
    public final MapFunc<Cursor, T> mapFromCursor;

    @NonNull
    public final PutResolver<T> putResolver;

    @NonNull
    public final MapFunc<T, DeleteQuery> mapToDeleteQuery;

    @Nullable
    public final GetResolver getResolver;

    @Nullable
    public final DeleteResolver deleteResolver;

    private ContentResolverTypeDefaults(@NonNull MapFunc<T, ContentValues> mapToContentValues, @NonNull MapFunc<Cursor, T> mapFromCursor, @NonNull PutResolver<T> putResolver, @NonNull MapFunc<T, DeleteQuery> mapToDeleteQuery, GetResolver getResolver, DeleteResolver deleteResolver) {
        this.mapToContentValues = mapToContentValues;
        this.mapFromCursor = mapFromCursor;
        this.putResolver = putResolver;
        this.mapToDeleteQuery = mapToDeleteQuery;
        this.getResolver = getResolver;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link ContentResolverTypeDefaults}
     */
    public static class Builder<T> {

        /**
         * Required: Specifies map function that will be used to map object of required type to {@link ContentValues}
         *
         * @param mapFunc non-null map function
         * @return builder
         */
        @NonNull
        public MapToContentValuesBuilder<T> mappingToContentValues(@NonNull MapFunc<T, ContentValues> mapFunc) {
            return new MapToContentValuesBuilder<T>(mapFunc);
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static class MapToContentValuesBuilder<T> {

        private final MapFunc<T, ContentValues> mapToContentValues;

        MapToContentValuesBuilder(MapFunc<T, ContentValues> mapToContentValues) {
            this.mapToContentValues = mapToContentValues;
        }

        /**
         * Required: Specifies map function that will be used to map {@link Cursor} to object of required type
         *
         * @param mapFunc non-null map function
         * @return builder
         */
        @NonNull
        public MapFromCursorBuilder<T> mappingFromCursor(@NonNull MapFunc<Cursor, T> mapFunc) {
            return new MapFromCursorBuilder<T>(mapToContentValues, mapFunc);
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static class MapFromCursorBuilder<T> {

        private final MapFunc<T, ContentValues> mapToContentValues;
        private final MapFunc<Cursor, T> mapFromCursor;

        MapFromCursorBuilder(MapFunc<T, ContentValues> mapToContentValues, MapFunc<Cursor, T> mapFromCursor) {
            this.mapToContentValues = mapToContentValues;
            this.mapFromCursor = mapFromCursor;
        }

        /**
         * Required: Specifies Resolver for Put Operation
         *
         * @param putResolver non-null resolver for Put Operation
         * @return builder
         */
        @NonNull
        public PutResolverBuilder<T> putResolver(@NonNull PutResolver<T> putResolver) {
            return new PutResolverBuilder<T>(
                    mapToContentValues,
                    mapFromCursor,
                    putResolver
            );
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static class PutResolverBuilder<T> {

        private final MapFunc<T, ContentValues> mapToContentValues;
        private final MapFunc<Cursor, T> mapFromCursor;
        private final PutResolver<T> putResolver;

        PutResolverBuilder(MapFunc<T, ContentValues> mapToContentValues, MapFunc<Cursor, T> mapFromCursor, PutResolver<T> putResolver) {
            this.mapToContentValues = mapToContentValues;
            this.mapFromCursor = mapFromCursor;
            this.putResolver = putResolver;
        }

        /**
         * Required: Specifies map function that will be used to map object or required type to {@link DeleteQuery}
         *
         * @param mapFunc non-null map function
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> mappingToDeleteQuery(@NonNull MapFunc<T, DeleteQuery> mapFunc) {
            return new CompleteBuilder<T>(
                    mapToContentValues,
                    mapFromCursor,
                    putResolver,
                    mapFunc
            );
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static class CompleteBuilder<T> {

        private final MapFunc<T, ContentValues> mapToContentValues;
        private final MapFunc<Cursor, T> mapFromCursor;
        private final PutResolver<T> putResolver;
        private final MapFunc<T, DeleteQuery> mapToDeleteQuery;

        private GetResolver getResolver;
        private DeleteResolver deleteResolver;

        CompleteBuilder(MapFunc<T, ContentValues> mapToContentValues, MapFunc<Cursor, T> mapFromCursor, PutResolver<T> putResolver, MapFunc<T, DeleteQuery> mapToDeleteQuery) {
            this.mapToContentValues = mapToContentValues;
            this.mapFromCursor = mapFromCursor;
            this.putResolver = putResolver;
            this.mapToDeleteQuery = mapToDeleteQuery;
        }

        /**
         * Optional: Specifies resolver for Get Operation
         *
         * @param getResolver resolver for Get Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> getResolver(@Nullable GetResolver getResolver) {
            this.getResolver = getResolver;
            return this;
        }

        /**
         * Optional: Specifies resolver for Delete Operation
         *
         * @param deleteResolver resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> deleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Builds new immutable instance of {@link ContentResolverTypeDefaults}
         *
         * @return new immutable instance of {@link ContentResolverTypeDefaults}
         */
        @NonNull
        public ContentResolverTypeDefaults<T> build() {
            checkNotNull(mapToContentValues, "Please specify mapping to ContentValues");
            checkNotNull(mapFromCursor, "Please specify mapping from Cursor");
            checkNotNull(putResolver, "Please specify PutResolver");
            checkNotNull(mapToDeleteQuery, "Please specify mapping to DeleteQuery");

            return new ContentResolverTypeDefaults<T>(
                    mapToContentValues,
                    mapFromCursor,
                    putResolver,
                    mapToDeleteQuery,
                    getResolver,
                    deleteResolver
            );
        }
    }
}
