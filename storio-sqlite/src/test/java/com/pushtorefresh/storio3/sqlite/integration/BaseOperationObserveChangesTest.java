package com.pushtorefresh.storio3.sqlite.integration;


import android.support.annotation.NonNull;

import com.pushtorefresh.storio3.operations.PreparedOperation;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.queries.Query;
import com.pushtorefresh.storio3.sqlite.queries.RawQuery;

import org.junit.Before;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.MISSING;

public abstract class BaseOperationObserveChangesTest extends BaseTest {

    @NonNull
    public Query query;

    @NonNull
    public RawQuery rawQuery;

    @NonNull
    public Changes tableChanges;

    @NonNull
    public Changes tagChanges;

    @Before
    public void beforeEachTest() {
        query = Query.builder()
                .table(UserTableMeta.TABLE)
                .observesTags(UserTableMeta.NOTIFICATION_TAG)
                .build();

        rawQuery = RawQuery.builder()
                .query("select * from " + UserTableMeta.TABLE)
                .observesTables(UserTableMeta.TABLE)
                .observesTags(UserTableMeta.NOTIFICATION_TAG)
                .build();

        tableChanges = Changes.newInstance(UserTableMeta.TABLE);

        tagChanges = Changes.newInstance("yet_another_table", UserTableMeta.NOTIFICATION_TAG);
    }

    public <Result, WrappedResult, Data> void verifyChangesReceived(
            @NonNull PreparedOperation<Result, WrappedResult, Data> operation,
            @NonNull Changes changes,
            @NonNull WrappedResult value
    ) {
        TestSubscriber<WrappedResult> testSubscriber = new TestSubscriber<WrappedResult>();

        operation
                .asRxFlowable(MISSING)
                .subscribe(testSubscriber);

        testSubscriber.assertValues(value);

        storIOSQLite.lowLevel().notifyAboutChanges(changes);

        testSubscriber.assertValues(value, value);
    }

    @Override
    @NonNull
    protected Scheduler defaultRxScheduler() {
        return Schedulers.trampoline();
    }
}
