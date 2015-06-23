package com.pushtorefresh.storio.operations.group;

import android.support.annotation.NonNull;
import android.support.annotation.WorkerThread;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.internal.OnSubscribeExecuteAsBlocking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Group Operation for StorIO implementations.
 * <p>
 * Allows group execution of any combination of {@link PreparedOperation}.
 * <p>
 * And yes, you can execute {@link PreparedGroupOperation}
 * as part of {@link PreparedGroupOperation}
 * since it implements {@link PreparedOperation} :).
 */
public class PreparedGroupOperation implements PreparedOperation<GroupOperationResults> {

    @NonNull
    private final List<PreparedOperation<?>> preparedOperations;

    protected PreparedGroupOperation(@NonNull List<PreparedOperation<?>> preparedOperations) {
        this.preparedOperations = preparedOperations;
    }

    /**
     * Executes Group Operation immediately in current thread.
     * <p>
     * Notice: This is blocking I/O operation that should not be executed on the Main Thread,
     * it can cause ANR (Activity Not Responding dialog), block the UI and drop animations frames.
     * So please, call this method on some background thread. See {@link WorkerThread}.
     *
     * @return non-null result of Group Operation.
     */
    @WorkerThread
    @NonNull
    @Override
    public GroupOperationResults executeAsBlocking() {
        final Map<PreparedOperation<?>, Object> results = new HashMap<PreparedOperation<?>, Object>();

        for (PreparedOperation<?> preparedOperation : preparedOperations) {
            final Object result = preparedOperation.executeAsBlocking();
            results.put(preparedOperation, result);
        }

        return GroupOperationResults.newInstance(results);
    }

    /**
     * Creates {@link Observable} which will perform Group Operation and send result to observer.
     * <p>
     * Returned {@link Observable} will be "Cold Observable", which means that it performs
     * Group Operation only after subscribing to it. Also, it emits the result once.
     * <p>
     * <dl>
     * <dt><b>Scheduler:</b></dt>
     * <dd>Operates on {@link Schedulers#io()}.</dd>
     * </dl>
     *
     * @return non-null {@link Observable} which will perform Group Operation.
     * and send result to observer.
     */
    @NonNull
    @Override
    public Observable<GroupOperationResults> createObservable() {
        throwExceptionIfRxJavaIsNotAvailable("createObservable()");

        return Observable
                .create(OnSubscribeExecuteAsBlocking.newInstance(this))
                .subscribeOn(Schedulers.io());
    }
}
