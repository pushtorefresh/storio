package com.pushtorefresh.storio.sqlite.operations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.operations.PreparedWriteOperation;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;

import rx.schedulers.TestScheduler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static rx.schedulers.Schedulers.test;

public class SchedulerChecker {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    @NonNull
    private final TestScheduler scheduler;

    private SchedulerChecker(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
        scheduler = test();
        when(storIOSQLite.defaultScheduler()).thenReturn(scheduler);
    }

    public static SchedulerChecker create(@NonNull StorIOSQLite storIOSQLite) {
        return new SchedulerChecker(storIOSQLite);
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

        verify(storIOSQLite).defaultScheduler();

        operationSpy.asRxObservable().subscribe();

        verify(operationSpy, never()).executeAsBlocking();
        scheduler.triggerActions();
        verify(operationSpy).executeAsBlocking();
    }
}
