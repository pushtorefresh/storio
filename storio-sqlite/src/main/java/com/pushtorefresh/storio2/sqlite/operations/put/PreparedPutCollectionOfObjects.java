package com.pushtorefresh.storio2.sqlite.operations.put;

import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio2.StorIOException;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.sqlite.Changes;
import com.pushtorefresh.storio2.sqlite.Interceptor;
import com.pushtorefresh.storio2.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;
import com.pushtorefresh.storio2.sqlite.operations.internal.RxJavaUtils;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class PreparedPutCollectionOfObjects<T> extends PreparedPut<PutResults<T>, Collection<T>> {

    @NonNull
    private final Collection<T> objects;

    private final boolean useTransaction;

    @Nullable
    private final PutResolver<T> explicitPutResolver;

    PreparedPutCollectionOfObjects(@NonNull StorIOSQLite storIOSQLite,
                                   @NonNull Collection<T> objects,
                                   @Nullable PutResolver<T> explicitPutResolver,
                                   boolean useTransaction) {
        super(storIOSQLite);
        this.objects = objects;
        this.useTransaction = useTransaction;
        this.explicitPutResolver = explicitPutResolver;
    }

    /**
     * Creates {@link Flowable} which will perform Put Operation and send result to observer.
     * <p>
     * Returned {@link Flowable} will be "Cold Flowable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Flowable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Flowable<PutResults<T>> asRxFlowable(@NonNull BackpressureStrategy backpressureStrategy) {
        return RxJavaUtils.createFlowable(storIOSQLite, this, backpressureStrategy);
    }

    /**
     * Creates {@link Single} which will perform Put Operation lazily when somebody subscribes to it and send result to observer.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Single} which will perform Put Operation.
     * And send result to observer.
     */
    @NonNull
    @CheckResult
    @Override
    public Single<PutResults<T>> asRxSingle() {
        return RxJavaUtils.createSingle(storIOSQLite, this);
    }

    /**
     * Creates {@link Completable} which will perform Put Operation lazily when somebody subscribes to it.
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link StorIOSQLite#defaultRxScheduler()} if not {@code null}.</dd>
     * </dl>
     *
     * @return non-null {@link Completable} which will perform Put Operation.
     */
    @NonNull
    @CheckResult
    @Override
    public Completable asRxCompletable() {
        return RxJavaUtils.createCompletable(storIOSQLite, this);
    }

    @NonNull
    @Override
    protected Interceptor getRealCallInterceptor() {
        return new RealCallInterceptor();
    }

    @NonNull
    @Override
    public Collection<T> getData() {
        return objects;
    }

    private class RealCallInterceptor implements Interceptor {
        @NonNull
        @Override
        public <Result, Data> Result intercept(@NonNull PreparedOperation<Result, Data> operation, @NonNull Chain chain) {
            try {
                final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

                // Nullable
                final List<SimpleImmutableEntry<T, PutResolver<T>>> objectsAndPutResolvers;

                if (explicitPutResolver != null) {
                    objectsAndPutResolvers = null;
                } else {
                    objectsAndPutResolvers = new ArrayList<SimpleImmutableEntry<T, PutResolver<T>>>(objects.size());

                    for (final T object : objects) {
                        //noinspection unchecked
                        final SQLiteTypeMapping<T> typeMapping
                                = (SQLiteTypeMapping<T>) lowLevel.typeMapping(object.getClass());

                        if (typeMapping == null) {
                            throw new IllegalStateException("One of the objects from the collection does not have type mapping: " +
                                    "object = " + object + ", object.class = " + object.getClass() + "," +
                                    "db was not affected by this operation, please add type mapping for this type");
                        }

                        objectsAndPutResolvers.add(new SimpleImmutableEntry<T, PutResolver<T>>(
                                object,
                                typeMapping.putResolver()
                        ));
                    }
                }

                if (useTransaction) {
                    lowLevel.beginTransaction();
                }

                final Map<T, PutResult> results = new HashMap<T, PutResult>(objects.size());
                boolean transactionSuccessful = false;

                try {
                    if (explicitPutResolver != null) {
                        for (final T object : objects) {
                            final PutResult putResult = explicitPutResolver.performPut(storIOSQLite, object);
                            results.put(object, putResult);

                            if (!useTransaction && (putResult.wasInserted() || putResult.wasUpdated())) {
                                final Changes changes = Changes.newInstance(
                                        putResult.affectedTables(),
                                        putResult.affectedTags()
                                );
                                lowLevel.notifyAboutChanges(changes);
                            }
                        }
                    } else {
                        for (final SimpleImmutableEntry<T, PutResolver<T>> objectAndPutResolver : objectsAndPutResolvers) {
                            final T object = objectAndPutResolver.getKey();
                            final PutResolver<T> putResolver = objectAndPutResolver.getValue();

                            final PutResult putResult = putResolver.performPut(storIOSQLite, object);

                            results.put(object, putResult);

                            if (!useTransaction && (putResult.wasInserted() || putResult.wasUpdated())) {
                                final Changes changes = Changes.newInstance(
                                        putResult.affectedTables(),
                                        putResult.affectedTags()
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

                        // if put was in transaction and it was successful -> notify about changes
                        if (transactionSuccessful) {
                            final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table
                            final Set<String> affectedTags = new HashSet<String>(1);

                            for (final T object : results.keySet()) {
                                final PutResult putResult = results.get(object);
                                if (putResult.wasInserted() || putResult.wasUpdated()) {
                                    affectedTables.addAll(putResult.affectedTables());
                                    affectedTags.addAll(putResult.affectedTags());
                                }
                            }

                            // IMPORTANT: Notifying about change should be done after end of transaction
                            // It'll reduce number of possible deadlock situations
                            if (!affectedTables.isEmpty() || !affectedTags.isEmpty()) {
                                lowLevel.notifyAboutChanges(Changes.newInstance(affectedTables, affectedTags));
                            }
                        }
                    }
                }

                //noinspection unchecked
                return (Result) PutResults.newInstance(results);

            } catch (Exception exception) {
                throw new StorIOException("Error has occurred during Put operation. objects = " + objects, exception);
            }
        }
    }

    /**
     * Builder for {@link PreparedPutCollectionOfObjects}
     *
     * @param <T> type of objects to put
     */
    public static class Builder<T> {

        @NonNull
        private final StorIOSQLite storIOSQLite;

        @NonNull
        private final Collection<T> objects;

        private PutResolver<T> putResolver;

        private boolean useTransaction = true;

        Builder(@NonNull StorIOSQLite storIOSQLite, @NonNull Collection<T> objects) {
            this.storIOSQLite = storIOSQLite;
            this.objects = objects;
        }

        /**
         * Optional: Specifies {@link PutResolver} for Put Operation
         * which allows you to customize behavior of Put Operation
         * <p>
         * Can be set via {@link SQLiteTypeMapping}
         * If it's not set via {@link SQLiteTypeMapping} or explicitly — exception will be thrown
         *
         * @param putResolver put resolver
         * @return builder
         * @see DefaultPutResolver
         */
        @NonNull
        public Builder<T> withPutResolver(@NonNull PutResolver<T> putResolver) {
            this.putResolver = putResolver;
            return this;
        }

        /**
         * Optional: Defines that Put Operation will use transaction if it is supported by implementation of {@link StorIOSQLite}
         * <p>
         * By default, transaction will be used
         *
         * @return builder
         */
        @NonNull
        public Builder<T> useTransaction(boolean useTransaction) {
            this.useTransaction = useTransaction;
            return this;
        }

        /**
         * Prepares Put Operation
         *
         * @return {@link PreparedPutCollectionOfObjects} instance
         */
        @NonNull
        public PreparedPutCollectionOfObjects<T> prepare() {
            return new PreparedPutCollectionOfObjects<T>(
                    storIOSQLite,
                    objects,
                    putResolver,
                    useTransaction
            );
        }
    }
}
