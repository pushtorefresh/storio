package com.pushtorefresh.storio2.sqlite.operations;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio2.operations.PreparedOperation;
import com.pushtorefresh.storio2.operations.PreparedWriteOperation;
import com.pushtorefresh.storio2.sqlite.StorIOSQLite;

import io.reactivex.schedulers.TestScheduler;

import static io.reactivex.BackpressureStrategy.MISSING;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SchedulerChecker {

    @NonNull
    private final StorIOSQLite storIOSQLite;

    @NonNull
    private final TestScheduler scheduler;

    private SchedulerChecker(@NonNull StorIOSQLite storIOSQLite) {
        this.storIOSQLite = storIOSQLite;
        scheduler = new TestScheduler();
        when(storIOSQLite.defaultRxScheduler()).thenReturn(scheduler);
    }

    @NonNull
    public static SchedulerChecker create(@NonNull StorIOSQLite storIOSQLite) {
        return new SchedulerChecker(storIOSQLite);
    }

    public void checkAsFlowable(@NonNull PreparedOperation operation) {
        operation.asRxFlowable(MISSING).subscribe();
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

        verify(storIOSQLite).defaultRxScheduler();

        operationSpy.asRxFlowable(MISSING).subscribe();

        verify(operationSpy, never()).executeAsBlocking();
        scheduler.triggerActions();
        verify(operationSpy).executeAsBlocking();
    }
}
