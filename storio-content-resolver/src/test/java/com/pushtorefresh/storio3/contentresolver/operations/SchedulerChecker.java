package com.pushtorefresh.storio3.contentresolver.operations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio3.operations.PreparedCompletableOperation;
import com.pushtorefresh.storio3.operations.PreparedOperation;

import org.mockito.Mockito;

import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.TestScheduler;
import io.reactivex.subscribers.TestSubscriber;

import static org.mockito.Mockito.verify;

public class SchedulerChecker {

    @NonNull
    private final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final TestScheduler scheduler;

    private SchedulerChecker(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
        scheduler = new TestScheduler();
        Mockito.when(storIOContentResolver.defaultRxScheduler()).thenReturn(scheduler);
    }

    @NonNull
    public static SchedulerChecker create(@NonNull StorIOContentResolver storIOContentResolver) {
        return new SchedulerChecker(storIOContentResolver);
    }

    public void checkAsFlowable(@NonNull PreparedOperation operation) {
        operation.asRxFlowable(BackpressureStrategy.MISSING).subscribe();
        check(operation);
    }

    public void checkAsSingle(@NonNull PreparedOperation operation) {
        operation.asRxSingle().subscribe();
        check(operation);
    }

    public void checkAsCompletable(@NonNull PreparedCompletableOperation operation) {
        operation.asRxCompletable().subscribe();
        check(operation);
    }

    private void check(@NonNull PreparedOperation operation) {
        verify(storIOContentResolver).defaultRxScheduler();

        TestSubscriber subscriber = new TestSubscriber();
        //noinspection unchecked
        operation.asRxFlowable(BackpressureStrategy.MISSING).subscribe(subscriber);

        subscriber.assertNoValues();

        scheduler.triggerActions();

        subscriber.assertValueCount(1);
    }
}
