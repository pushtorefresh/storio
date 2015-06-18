package com.pushtorefresh.storio.sqlite.operation.delete;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of objects to delete.
 */
public final class PreparedDeleteCollectionOfObjects<T> extends PreparedDelete<DeleteResults<T>> {

    @NonNull
    private final Collection<T> objects;

    @Nullable
    private final DeleteResolver<T> explicitDeleteResolver;

    private final boolean useTransaction;

    PreparedDeleteCollectionOfObjects(@NonNull StorIOSQLite storIOSQLite,
                                      @NonNull Collection<T> objects,
                                      @Nullable DeleteResolver<T> explicitDeleteResolver,
                                      boolean useTransaction) {
        super(storIOSQLite);
        this.objects = objects;
        this.explicitDeleteResolver = explicitDeleteResolver;
        this.useTransaction = useTransaction;
    }

    /**
     * Executes Delete Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Delete Operation.
     */
    @SuppressWarnings("unchecked")
    @WorkerThread
    @NonNull
    @Override
    public DeleteResults<T> executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();

        // Nullable
        final List<SimpleImmutableEntry<T, DeleteResolver<T>>> objectsAndDeleteResolvers;

        if (explicitDeleteResolver != null) {
            objectsAndDeleteResolvers = null;
        } else {
            objectsAndDeleteResolvers
                    = new ArrayList<SimpleImmutableEntry<T, DeleteResolver<T>>>(objects.size());

            for (final T object : objects) {
                final SQLiteTypeMapping<T> typeMapping
                        = (SQLiteTypeMapping<T>) internal.typeMapping(object.getClass());

                if (typeMapping == null) {
                    throw new IllegalStateException("One of the objects from the collection does not have type mapping: " +
                            "object = " + object + ", object.class = " + object.getClass() + "," +
                            "db was not affected by this operation, please add type mapping for this type");
                }

                objectsAndDeleteResolvers.add(new SimpleImmutableEntry<T, DeleteResolver<T>>(
                        object,
                        typeMapping.deleteResolver()
                ));
            }
        }

        if (useTransaction) {
            internal.beginTransaction();
        }

        final Map<T, DeleteResult> results = new HashMap<T, DeleteResult>(objects.size());
        boolean transactionSuccessful = false;

        try {
            if (explicitDeleteResolver != null) {
                for (final T object : objects) {
                    final DeleteResult deleteResult = explicitDeleteResolver.performDelete(storIOSQLite, object);

                    results.put(object, deleteResult);

                    if (!useTransaction) {
                        internal.notifyAboutChanges(Changes.newInstance(deleteResult.affectedTables()));
                    }
                }
            } else {
                for (final SimpleImmutableEntry<T, DeleteResolver<T>> objectAndDeleteResolver : objectsAndDeleteResolvers) {
                    final T object = objectAndDeleteResolver.getKey();
                    final DeleteResolver<T> deleteResolver = objectAndDeleteResolver.getValue();

                    final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);

                    results.put(object, deleteResult);

                    if (!useTransaction) {
                        internal.notifyAboutChanges(Changes.newInstance(deleteResult.affectedTables()));
                    }
                }
            }

            if (useTransaction) {
                internal.setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (useTransaction) {
                internal.endTransaction();

                // if delete was in transaction and it was successful -> notify about changes
                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be one table

                    for (final T object : results.keySet()) {
                        affectedTables.addAll(results.get(object).affectedTables());
                    }

                    // IMPORTANT: Notifying about change should be done after end of transaction
                    // It'll reduce number of possible deadlock situations
                    internal.notifyAboutChanges(Changes.newInstance(affectedTables));
                }
            }
        }

        return DeleteResults.newInstance(results);
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p/>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<DeleteResults<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedDeleteCollectionOfObjects}.
     *
     * @param <T> type of objects to delete.
     */
    public static final class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Collection<T> objects;

        private DeleteResolver<T> deleteResolver;

        private boolean useTransaction = true;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Collection<T> objects) {
            this.storIOSQLite = storIOSQLite;
            this.objects = objects;
        }

        /**
         * Optional: Defines that Delete Operation will use transaction or not.
         * <p/>
         * By default, transaction will be used.
         *
         * @param useTransaction {@code true} to use transaction, {@code false} to not.
         * @return builder.
         */
        @NonNull
        public Builder<T> useTransaction(boolean useTransaction) {
            this.useTransaction = useTransaction;
            return this;
        }

        /**
         * Optional: Specifies {@link DeleteResolver} for Delete Operation.
         * <p/>
         * <p/>
         * Can be set via {@link SQLiteTypeMapping},
         * If value is not set via {@link SQLiteTypeMapping}
         * or explicitly -> exception will be thrown.
         *
         * @param deleteResolver {@link DeleteResolver} for Delete Operation.
         * @return builder.
         */
        @NonNull
        public Builder<T> withDeleteResolver(@Nullable DeleteResolver<T> deleteResolver) {
            this.deleteResolver = deleteResolver;
            return this;
        }

        /**
         * Prepares Delete Operation.
         *
         * @return {@link PreparedDeleteCollectionOfObjects}.
         */
        @NonNull
        public PreparedDeleteCollectionOfObjects<T> prepare() {
            return new PreparedDeleteCollectionOfObjects<T>(
                    storIOSQLite,
                    objects,
                    deleteResolver,
                    useTransaction
            );
        }
    }
}
