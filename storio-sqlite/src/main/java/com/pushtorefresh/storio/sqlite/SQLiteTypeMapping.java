package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.internal.TypeMapping;
import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.PutResolver;

import static com.pushtorefresh.storio.internal.Checks.checkNotNull;

/**
 * SQLite Type Mapping.
 *
 * @param <T> type to map.
 */
public class SQLiteTypeMapping<T> implements TypeMapping<T> {

    @NonNull
    private final PutResolver<T> putResolver;

    @NonNull
    private final GetResolver<T> getResolver;

    @NonNull
    private final DeleteResolver<T> deleteResolver;

    protected SQLiteTypeMapping(@NonNull PutResolver<T> putResolver,
                                @NonNull GetResolver<T> getResolver,
                                @NonNull DeleteResolver<T> deleteResolver) {
        this.putResolver = putResolver;
        this.getResolver = getResolver;
        this.deleteResolver = deleteResolver;
    }

    @NonNull
    public PutResolver<T> putResolver() {
        return putResolver;
    }

    @NonNull
    public GetResolver<T> getResolver() {
        return getResolver;
    }

    @NonNull
    public DeleteResolver<T> deleteResolver() {
        return deleteResolver;
    }

    /**
     * Creates new builder for {@link SQLiteTypeMapping}.
     *
     * @return non-null instance of {@link SQLiteTypeMapping.Builder}.
     */
    @NonNull
    public static <T> Builder<T> builder() {
        return new Builder<T>();
    }

    /**
     * Builder for {@link SQLiteTypeMapping}.
     */
    public static final class Builder<T> {

        /**
         * Please use {@link SQLiteTypeMapping#builder()} instead of this.
         */
        Builder() {
        }

        /**
         * Required: Specifies Resolver for Put Operation.
         *
         * @param putResolver non-null resolver for Put Operation.
         * @return builder.
         */
        @NonNull
        public PutResolverBuilder<T> putResolver(@NonNull PutResolver<T> putResolver) {
            checkNotNull(putResolver, "Please specify PutResolver");
            return new PutResolverBuilder<T>(putResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link SQLiteTypeMapping}.
     *
     * @param <T> type.
     */
    public static final class PutResolverBuilder<T> {

        @NonNull
        private final PutResolver<T> putResolver;

        PutResolverBuilder(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
        }

        /**
         * Required: Specifies Resolver for Get Operation.
         *
         * @param getResolver non-null resolver for Get Operation.
         * @return builder.
         */
        @NonNull
        public GetResolverBuilder<T> getResolver(@NonNull GetResolver<T> getResolver) {
            checkNotNull(getResolver, "Please specify GetResolver");
            return new GetResolverBuilder<T>(putResolver, getResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link SQLiteTypeMapping}.
     *
     * @param <T> type.
     */
    public static final class GetResolverBuilder<T> {

        @NonNull
        private final PutResolver<T> putResolver;

        @NonNull
        private final GetResolver<T> getResolver;

        GetResolverBuilder(@NonNull PutResolver<T> putResolver, @NonNull GetResolver<T> getResolver) {
            this.putResolver = putResolver;
            this.getResolver = getResolver;
        }

        /**
         * Required: Specifies Resolver for Delete Operation.
         *
         * @param deleteResolver non-null resolver for Delete Operation.
         * @return builder.
         */
        @NonNull
        public CompleteBuilder<T> deleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            checkNotNull(deleteResolver, "Please specify DeleteResolver");

            return new CompleteBuilder<T>(
                    putResolver,
                    getResolver,
                    deleteResolver
            );
        }
    }

    /**
     * Compile-time safe part of builder for {@link SQLiteTypeMapping}.
     *
     * @param <T> type.
     */
    public static final class CompleteBuilder<T> {

        @NonNull
        private final PutResolver<T> putResolver;

        @NonNull
        private final GetResolver<T> getResolver;

        @NonNull
        private final DeleteResolver<T> deleteResolver;

        CompleteBuilder(@NonNull PutResolver<T> putResolver,
                        @NonNull GetResolver<T> getResolver,
                        @NonNull DeleteResolver<T> deleteResolver) {
            this.putResolver = putResolver;
            this.getResolver = getResolver;
            this.deleteResolver = deleteResolver;
        }

        /**
         * Builds new immutable instance of {@link SQLiteTypeMapping}.
         *
         * @return new immutable instance of {@link SQLiteTypeMapping}.
         */
        @NonNull
        public SQLiteTypeMapping<T> build() {
            return new SQLiteTypeMapping<T>(
                    putResolver,
                    getResolver,
                    deleteResolver
            );
        }
    }

}
