package com.pushtorefresh.storio.sqlite.operation.put;

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

public final class PreparedPutCollectionOfObjects<T> extends PreparedPut<PutResults<T>> {

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
     * Executes Put Operation immediately in current thread.
     * <p/>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null results of Put Operation.
     */
    @SuppressWarnings("unchecked")
    @WorkerThread
    @NonNull
    @Override
    public PutResults<T> executeAsBlocking() {
        final StorIOSQLite.Internal internal = storIOSQLite.internal();

        // Nullable
        final List<SimpleImmutableEntry<T, PutResolver<T>>> objectsAndPutResolvers;

        if (explicitPutResolver != null) {
            objectsAndPutResolvers = null;
        } else {
            objectsAndPutResolvers = new ArrayList<SimpleImmutableEntry<T, PutResolver<T>>>(objects.size());

            for (final T object : objects) {
                final SQLiteTypeMapping<T> typeMapping
                        = (SQLiteTypeMapping<T>) internal.typeMapping(object.getClass());

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
            internal.beginTransaction();
        }

        final Map<T, PutResult> results = new HashMap<T, PutResult>();
        boolean transactionSuccessful = false;

        try {
            if (explicitPutResolver != null) {
                for (final T object : objects) {
                    final PutResult putResult = explicitPutResolver.performPut(storIOSQLite, object);
                    results.put(object, putResult);

                    if (!useTransaction) {
                        internal.notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));
                    }
                }
            } else {
                for (final SimpleImmutableEntry<T, PutResolver<T>> objectAndPutResolver : objectsAndPutResolvers) {
                    final T object = objectAndPutResolver.getKey();
                    final PutResolver<T> putResolver = objectAndPutResolver.getValue();

                    final PutResult putResult = putResolver.performPut(storIOSQLite, object);

                    results.put(object, putResult);

                    if (!useTransaction) {
                        internal.notifyAboutChanges(Changes.newInstance(putResult.affectedTables()));
                    }
                }
            }

            if (useTransaction) {
                storIOSQLite.internal().setTransactionSuccessful();
                transactionSuccessful = true;
            }
        } finally {
            if (useTransaction) {
                storIOSQLite.internal().endTransaction();

                // if delete was in transaction and it was successful -> notify about changes
                if (transactionSuccessful) {
                    final Set<String> affectedTables = new HashSet<String>(1); // in most cases it will be 1 table

                    for (final T object : results.keySet()) {
                        affectedTables.addAll(results.get(object).affectedTables());
                    }

                    // IMPORTANT: Notifying about change should be done after end of transaction
                    // It'll reduce number of possible deadlock situations
                    storIOSQLite.internal().notifyAboutChanges(Changes.newInstance(affectedTables));
                }
            }
        }

        return PutResults.newInstance(results);
    }

    /**
     * Creates {@link Observable} which will perform Put Operation and send result to observer.
     * <p/>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * put only after subscribing to it. Also, it emits the result once.
     * <p/>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Put Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<PutResults<T>> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }

    /**
     * Builder for {@link PreparedPutCollectionOfObjects}
     *
     * @param <T> type of objects to put
     */
    public static final class Builder<T> {

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
         * <p/>
         * Can be set via {@link SQLiteTypeMapping}
         * If it's not set via {@link SQLiteTypeMapping} or explicitly -> exception will be thrown
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
         * <p/>
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
