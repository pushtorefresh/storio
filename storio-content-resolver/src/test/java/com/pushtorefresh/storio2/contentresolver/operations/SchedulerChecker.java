package com.pushtorefresh.storio2.contentresolver.operations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.PreparedWriteOperation;

import org.mockito.Mockito;

import io.reactivex.BackpressureStrategy;
import io.reactivex.schedulers.TestScheduler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
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

    public void checkAsCompletable(@NonNull PreparedWriteOperation operation) {
        operation.asRxCompletable().subscribe();
        check(operation);
    }

    private void check(@NonNull PreparedOperation operation) {
        final PreparedOperation operationSpy = spy(operation);

        verify(storIOContentResolver).defaultRxScheduler();

        operationSpy.asRxFlowable(BackpressureStrategy.MISSING).subscribe();

        verify(operationSpy, never()).executeAsBlocking();
        scheduler.triggerActions();
        verify(operationSpy).executeAsBlocking();
    }
}
