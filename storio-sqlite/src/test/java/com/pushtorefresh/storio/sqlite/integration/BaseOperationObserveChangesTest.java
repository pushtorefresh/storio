package com.pushtorefresh.storio.sqlite.integration;


import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.Changes;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.junit.Before;

import rx.Scheduler;
import rx.observers.TestSubscriber;
import rx.schedulers.Schedulers;

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

    public <T> void verifyChangesReceived(
            @NonNull PreparedOperation<T> operation,
            @NonNull Changes changes,
            @NonNull T value
    ) {
        TestSubscriber<T> testSubscriber = new TestSubscriber<T>();

        operation
                .asRxObservable()
                .subscribe(testSubscriber);

        testSubscriber.assertValues(value);

        storIOSQLite.lowLevel().notifyAboutChanges(changes);

        testSubscriber.assertValues(value, value);
    }

    @Override
    @NonNull
    protected Scheduler defaultScheduler() {
        return Schedulers.immediate();
    }
}
