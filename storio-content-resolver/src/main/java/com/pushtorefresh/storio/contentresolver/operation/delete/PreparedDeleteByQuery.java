package com.pushtorefresh.storio.contentresolver.operation.delete;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.contentresolver.query.DeleteQuery;

import rx.Observable;
import rx.Subscriber;

import static com.pushtorefresh.storio.util.Checks.checkNotNull;

/**
 * Prepared Delete Operation by {@link com.pushtorefresh.storio.contentresolver.query.DeleteQuery}
 * for {@link com.pushtorefresh.storio.contentresolver.StorIOContentResolver}
 */
public class PreparedDeleteByQuery extends PreparedDelete<DeleteResult> {

    @NonNull
    private final DeleteQuery deleteQuery;

    PreparedDeleteByQuery(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteResolver deleteResolver, @NonNull DeleteQuery deleteQuery) {
        super(storIOContentResolver, deleteResolver);
        this.deleteQuery = deleteQuery;
    }

    /**
     * Executes Delete Operation immediately in current thread
     *
     * @return non-null result of Delete Operation
     */
    @NonNull
    @Override
    public DeleteResult executeAsBlocking() {
        return deleteResolver.performDelete(storIOContentResolver, deleteQuery);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer
     *
     * @return non-null {@link Observable} which will perform Delete Operation and send result to observer
     */
    @NonNull
    @Override
    public Observable<DeleteResult> createObservable() {
        return Observable.create(new Observable.OnSubscribe<DeleteResult>() {
            @Override
            public void call(Subscriber<? super DeleteResult> subscriber) {
                final DeleteResult deleteResult = executeAsBlocking();

                if (!subscriber.isUnsubscribed()) {
                    subscriber.onNext(deleteResult);
                    subscriber.onCompleted();
                }
            }
        });
    }

    /**
     * Builder for {@link PreparedDeleteByQuery}
     */
    public static class Builder {

        @NonNull
        private final StorIOContentResolver storIOContentResolver;

        @NonNull
        private final DeleteQuery deleteQuery;

        private DeleteResolver deleteResolver;

        /**
         * Creates builder for {@link PreparedDeleteByQuery}
         *
         * @param storIOContentResolver non-null instance of {@link StorIOContentResolver}
         * @param deleteQuery           non-null instance of {@link DeleteQuery}
         */
        public Builder(@NonNull StorIOContentResolver storIOContentResolver, @NonNull DeleteQuery deleteQuery) {
            checkNotNull(storIOContentResolver, "Please specify StorIOContentResolver");
            checkNotNull(deleteQuery, "Please specify delete query");

            this.storIOContentResolver = storIOContentResolver;
            this.deleteQuery = deleteQuery;
        }

        /**
         * Optional: Specifies resolver for Delete Operation
         * Allows you to customise behavior of Delete Operation
         * <p>
         * Default value is instance of {@link DefaultDeleteResolver}
         *
         * @param deleteResolver resolver for Delete Operation
         * @return builder
         */
        @NonNull
        public Builder withDeleteResolver(@NonNull DeleteResolver deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Builds instance of {@link PreparedDeleteByQuery}
         *
         * @return instance of {@link PreparedDeleteByQuery}
         */
        @NonNull
        public PreparedDeleteByQuery prepare() {
            if (deleteResolver == null) {
                deleteResolver = DefaultDeleteResolver.INSTANCE;
            }

            return new PreparedDeleteByQuery(
                    storIOContentResolver,
                    deleteResolver,
                    deleteQuery
            );
        }
    }
}
