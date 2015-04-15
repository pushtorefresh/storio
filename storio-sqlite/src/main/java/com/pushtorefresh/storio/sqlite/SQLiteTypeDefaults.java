package com.pushtorefresh.storio.sqlite;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.sqlite.operation.get.GetResolver;
import com.pushtorefresh.storio.sqlite.operation.put.PutResolver;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * SQLite Type default values for object mapping
 *
 * @param <T> type
 */
public class SQLiteTypeDefaults<T> {

    @NonNull
    public final PutResolver<T> putResolver;

    @NonNull
    public final GetResolver<T> getResolver;

    @NonNull
    public final DeleteResolver<T> deleteResolver;

    SQLiteTypeDefaults(@NonNull PutResolver<T> putResolver,
                       @NonNull GetResolver<T> getResolver,
                       @NonNull DeleteResolver<T> deleteResolver) {
        this.putResolver = putResolver;
        this.getResolver = getResolver;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link SQLiteTypeDefaults}
     */
    public static class Builder<T> {

        /**
         * Required: Specifies Resolver for Put Operation
         *
         * @param putResolver non-null resolver for Put Operation
         * @return builder
         */
        @NonNull
        public PutResolverBuilder<T> putResolver(@NonNull PutResolver<T> putResolver) {
            return new PutResolverBuilder<T>(putResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link SQLiteTypeDefaults}
     *
     * @param <T> type
     */
    public static class PutResolverBuilder<T> {

        private final PutResolver<T> putResolver;

        PutResolverBuilder(PutResolver<T> putResolver) {
            this.putResolver = putResolver;
        }

        /**
         * Required: Specifies Resolver for Get Operation
         *
         * @param getResolver non-null resolver for Get Operation
         * @return builder
         */
        @NonNull
        public GetResolverBuilder<T> getResolver(@NonNull GetResolver<T> getResolver) {
            return new GetResolverBuilder<T>(putResolver, getResolver);
        }
    }

    public static class GetResolverBuilder<T> {

        private final PutResolver<T> putResolver;
        private final GetResolver<T> getResolver;

        GetResolverBuilder(PutResolver<T> putResolver, GetResolver<T> getResolver) {
            this.putResolver = putResolver;
            this.getResolver = getResolver;
        }

        /**
         * Required: Specifies Resolver for Delete Operation
         *
         * @param deleteResolver non-null resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> deleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            return new CompleteBuilder<T>(
                    putResolver,
                    getResolver,
                    deleteResolver
            );
        }
    }

    public static class CompleteBuilder<T> {

        private final PutResolver<T> putResolver;
        private final GetResolver<T> getResolver;
        private final DeleteResolver<T> deleteResolver;

        CompleteBuilder(PutResolver<T> putResolver,
                        GetResolver<T> getResolver,
                        DeleteResolver<T> deleteResolver) {
            this.putResolver = putResolver;
            this.getResolver = getResolver;
            this.deleteResolver = deleteResolver;
        }

        /**
         * Builds new immutable instance of {@link SQLiteTypeDefaults}
         *
         * @return new immutable instance of {@link SQLiteTypeDefaults}
         */
        @NonNull
        public SQLiteTypeDefaults<T> build() {
            checkNotNull(putResolver, "Please specify PutResolver");
            checkNotNull(getResolver, "Please specify GetResolver");
            checkNotNull(deleteResolver, "Please specify DeleteResolver");

            return new SQLiteTypeDefaults<T>(
                    putResolver,
                    getResolver,
                    deleteResolver
            );
        }
    }

}
