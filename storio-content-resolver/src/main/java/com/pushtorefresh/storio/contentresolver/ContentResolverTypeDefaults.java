package com.pushtorefresh.storio.contentresolver;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.operation.delete.DeleteResolver;
import com.pushtorefresh.storio.contentresolver.operation.get.GetResolver;
import com.pushtorefresh.storio.contentresolver.operation.put.PutResolver;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * ContentResolver Type default values for object mapping
 *
 * @param <T> type
 */
public final class ContentResolverTypeDefaults<T> {

    @NonNull
    public final PutResolver<T> putResolver;

    @NonNull
    public final GetResolver<T> getResolver;

    @NonNull
    public final DeleteResolver<T> deleteResolver;

    private ContentResolverTypeDefaults(@NonNull PutResolver<T> putResolver,
                                        @NonNull GetResolver<T> getResolver,
                                        @NonNull DeleteResolver<T> deleteResolver) {
        this.putResolver = putResolver;
        this.getResolver = getResolver;
        this.deleteResolver = deleteResolver;
    }

    /**
     * Builder for {@link ContentResolverTypeDefaults}
     */
    public static final class Builder<T> {

        /**
         * Required: Specifies Resolver for Put Operation
         *
         * @param putResolver non-null resolver for Put Operation
         * @return builder
         */
        @NonNull
        public PutResolverBuilder<T> putResolver(@NonNull PutResolver<T> putResolver) {
            checkNotNull(putResolver, "Please specify PutResolver");
            return new PutResolverBuilder<T>(putResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static final class PutResolverBuilder<T> {

        @NonNull
        private final PutResolver<T> putResolver;

        PutResolverBuilder(@NonNull PutResolver<T> putResolver) {
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
            checkNotNull(getResolver, "Please specify GetResolver");
            return new GetResolverBuilder<T>(putResolver, getResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
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
         * Required: Specifies Resolver for Delete Operation
         *
         * @param deleteResolver non-null resolver for Put Operation
         * @return builder
         */
        @NonNull
        public CompleteBuilder<T> deleteResolver(@NonNull DeleteResolver<T> deleteResolver) {
            checkNotNull(deleteResolver, "Please specify DeleteResolver");
            return new CompleteBuilder<T>(putResolver, getResolver, deleteResolver);
        }
    }

    /**
     * Compile-time safe part of builder for {@link ContentResolverTypeDefaults}
     *
     * @param <T> type
     */
    public static class CompleteBuilder<T> {

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
         * Builds new immutable instance of {@link ContentResolverTypeDefaults}
         *
         * @return new immutable instance of {@link ContentResolverTypeDefaults}
         */
        @NonNull
        public ContentResolverTypeDefaults<T> build() {
            return new ContentResolverTypeDefaults<T>(
                    putResolver,
                    getResolver,
                    deleteResolver
            );
        }
    }
}
