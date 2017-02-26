package com.pushtorefresh.storio.sqlite.operations.delete;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.StorIOException;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.internal.RxJavaUtils;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Prepared Delete Operation for {@link StorIOSQLite}.
 *
 * @param <T> type of objects to delete.
 */
public class PreparedDeleteCollectionOfObjects<T> extends PreparedDelete<DeleteResults<T>> {

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
     * <p>
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
        try {
            final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

            // Nullable
            final List<SimpleImmutableEntry<T, DeleteResolver<T>>> objectsAndDeleteResolvers;

            if (explicitDeleteResolver != null) {
                objectsAndDeleteResolvers = null;
            } else {
                objectsAndDeleteResolvers
                        = new ArrayList<SimpleImmutableEntry<T, DeleteResolver<T>>>(objects.size());

                for (final T object : objects) {
                    final SQLiteTypeMapping<T> typeMapping
                            = (SQLiteTypeMapping<T>) lowLevel.typeMapping(object.getClass());

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
                lowLevel.beginTransaction();
            }

            final Map<T, DeleteResult> results = new HashMap<T, DeleteResult>(objects.size());
            boolean transactionSuccessful = false;

            try {
                if (explicitDeleteResolver != null) {
                    for (final T object : objects) {
                        final DeleteResult deleteResult = explicitDeleteResolver.performDelete(storIOSQLite, object);

                        results.put(object, deleteResult);

                        if (!useTransaction && deleteResult.numberOfRowsDeleted() > 0) {
                            final Changes changes = Changes.newInstance(
                                    deleteResult.affectedTables(),
                                    deleteResult.affectedTags()
                            );
                            lowLevel.notifyAboutChanges(changes);
                        }
                    }
                } else {
                    for (final SimpleImmutableEntry<T, DeleteResolver<T>> objectAndDeleteResolver : objectsAndDeleteResolvers) {
                        final T object = objectAndDeleteResolver.getKey();
                        final DeleteResolver<T> deleteResolver = objectAndDeleteResolver.getValue();

                        final DeleteResult deleteResult = deleteResolver.performDelete(storIOSQLite, object);

                        results.put(object, deleteResult);

                        if (!useTransaction && deleteResult.numberOfRowsDeleted() > 0) {
                            final Changes changes = Changes.newInstance(
                                    deleteResult.affectedTables(),
                                    deleteResult.affectedTags()
                            );
                            lowLevel.notifyAboutChanges(changes);
                        }
                    }
                }

                if (useTransaction) {
                    lowLevel.setTransactionSuccessful();
                    transactionSuccessful = true;
                }
            } finally {
                if (useTransaction) {
                    lowLevel.endTransaction();

                    // if delete was in transaction and it was successful -> notify about changes
                    if (transactionSuccessful) {
                        final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be one table
                        final Set<String> affectedTags = new HashSet<String>(1);

                        for (final T object : results.keySet()) {
                            final DeleteResult deleteResult = results.get(object);
                            if (deleteResult.numberOfRowsDeleted() > 0) {
                                affectedTables.addAll(results.get(object).affectedTables());
                                affectedTags.addAll(results.get(object).affectedTags());
                            }
                        }

                        // IMPORTANT: Notifying about change should be done after end of transaction
                        // It'll reduce number of possible deadlock situations
                        if (!affectedTables.isEmpty() || !affectedTags.isEmpty()) {
                            final Changes changes = Changes.newInstance(affectedTables, affectedTags);
                            lowLevel.notifyAboutChanges(changes);
                        }
                    }
                }
            }

            return DeleteResults.newInstance(results);

        } catch (Exception exception) {
            throw new StorIOException("Error has occurred during Delete operation. objects = " + objects, exception);
        }
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     * @deprecated (will be removed in 2.0) please use {@link #asRxObservable()}.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<DeleteResults<T>> createObservable() {
        return asRxObservable();
    }

    /**
     * Creates {@link Observable} which will perform Delete Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * delete only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Delete Operation.
     * and send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Observable<DeleteResults<T>> asRxObservable() {
        return RxJavaUtils.createObservable(storIOSQLite, this);
    }

    /**
     * Creates {@link Single} which will perform Delete Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Delete Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<DeleteResults<T>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    /**
     * Creates {@link Completable} which will perform Delete Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Delete Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
    }

    /**
     * Builder for {@link PreparedDeleteCollectionOfObjects}.
     *
     * @param <T> type of objects to delete.
     */
    public static class Builder<T> {

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
         * <p>
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
         * <p>
         * <p>
         * Can be set via {@link SQLiteTypeMapping},
         * If value is not set via {@link SQLiteTypeMapping}
         * or explicitly — exception will be thrown.
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
