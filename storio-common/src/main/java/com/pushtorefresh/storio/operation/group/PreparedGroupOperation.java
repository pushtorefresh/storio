package com.pushtorefresh.storio.operation.group;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operation.PreparedOperation;
import com.pushtorefresh.storio.operation.internal.OnSubscribeExecuteAsBlocking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.Observable;
import rx.schedulers.Schedulers;

import static com.pushtorefresh.storio.internal.Environment.throwExceptionIfRxJavaIsNotAvailable;

/**
 * Prepared Group Operation for StorIO implementations.
 * <p/>
 * Allows group execution of any combination of {@link PreparedOperation}.
 * <p/>
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
     *
     * @return non-null result of Group Operation.
     */
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
     *
     * <dl>
     *  <dt><b>Scheduler:</b></dt>
     *  <dd>Operates on {@link Schedulers#io()}.</dd>
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
