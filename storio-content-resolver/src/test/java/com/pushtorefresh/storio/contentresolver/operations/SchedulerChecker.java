package com.pushtorefresh.storio.contentresolver.operations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.contentresolver.StorIOContentResolver;
import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.PreparedWriteOperation;

import org.mockito.Mockito;

import rx.schedulers.TestScheduler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static rx.schedulers.Schedulers.test;

public class SchedulerChecker {

    @NonNull
    private final StorIOContentResolver storIOContentResolver;

    @NonNull
    private final TestScheduler scheduler;

    private SchedulerChecker(@NonNull StorIOContentResolver storIOContentResolver) {
        this.storIOContentResolver = storIOContentResolver;
        scheduler = test();
        Mockito.when(storIOContentResolver.defaultScheduler()).thenReturn(scheduler);
    }

    @NonNull
    public static SchedulerChecker create(@NonNull StorIOContentResolver storIOContentResolver) {
        return new SchedulerChecker(storIOContentResolver);
    }

    public void checkAsObservable(@NonNull PreparedOperation operation) {
        operation.asRxObservable().subscribe();
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

        verify(storIOContentResolver).defaultScheduler();

        operationSpy.asRxObservable().subscribe();

        verify(operationSpy, never()).executeAsBlocking();
        scheduler.triggerActions();
        verify(operationSpy).executeAsBlocking();
    }
}