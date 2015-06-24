package com.pushtorefresh.storio.sqlite.integration;


import android.os.SystemClock;

import com.pushtorefresh.storio.sqlite.BuildConfig;
import com.pushtorefresh.storio.sqlite.Changes;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import rx.observers.TestObserver;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class NotifyAboutChangesTest extends BaseTest {

    @Test
    public void notifyAboutChange() {
        TestObserver<Changes> testObserver = new TestObserver<Changes>();

        storIOSQLite
                .observeChangesInTable("test_table")
                .subscribe(testObserver);

        storIOSQLite
                .internal()
                .notifyAboutChanges(Changes.newInstance("test_table"));

        final long startTime = SystemClock.elapsedRealtime();

        while (testObserver.getOnNextEvents().size() == 0
                && SystemClock.elapsedRealtime() - startTime < 1000) {
            Thread.yield(); // let other threads work
        }

        testObserver.assertReceivedOnNext(singletonList(Changes.newInstance("test_table")));
    }

    @Test
    public void notifyAboutChangesConcurrently() {
        final int numberOfThreads = 100; // do you feel concurrency?

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        final Set<String> tables = new HashSet<String>();
        final List<Changes> expectedChanges = new ArrayList<Changes>();

        for (int i = 0; i < numberOfThreads; i++) {
            final String table = "test_table" + i;
            tables.add(table);
            expectedChanges.add(Changes.newInstance(table));
        }

        storIOSQLite
                .observeChangesInTables(tables)
                .subscribe(testSubscriber);

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);

        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // all threads should start simultaneously
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    storIOSQLite
                            .internal()
                            .notifyAboutChanges(Changes.newInstance("test_table" + finalI));
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        final long startTime = SystemClock.elapsedRealtime();

        while (testSubscriber.getOnNextEvents().size() != tables.size()
                && (SystemClock.elapsedRealtime() - startTime) < 20000) {
            Thread.yield(); // let other threads work
        }

        testSubscriber.assertNoErrors();

        // notice, that order of received notification can be different
        // but in total, they should be equal
        assertEquals(expectedChanges.size(), testSubscriber.getOnNextEvents().size());
        assertTrue(expectedChanges.containsAll(testSubscriber.getOnNextEvents()));
    }

    @Test
    public void shouldReceiveOneNotificationInTransactionWithOneThread() throws InterruptedException {
        final String table = "test_table";
        final int numberOfChanges = 20;

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChangesInTable(table)
                .subscribe(testSubscriber);

        storIOSQLite
                .internal()
                .beginTransaction();

        for (int i = 0; i < numberOfChanges; i++) {
            storIOSQLite
                    .internal()
                    .notifyAboutChanges(Changes.newInstance(table));
        }

        // While we in transaction, no changes should be sent
        assertEquals(0, testSubscriber.getOnNextEvents().size());

        storIOSQLite
                .internal()
                .endTransaction();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(Changes.newInstance(table)));
    }

    @Test
    public void shouldReceiveOneNotificationInTransactionWithMultipleThreads() throws InterruptedException {
        final String table = "test_table";
        final int numberOfThreads = 100;

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChangesInTable(table)
                .subscribe(testSubscriber);

        storIOSQLite
                .internal()
                .beginTransaction();

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);
        final CountDownLatch allThreadsFinishedLock = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // all threads should start simultaneously
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    storIOSQLite
                            .internal()
                            .notifyAboutChanges(Changes.newInstance(table));

                    allThreadsFinishedLock.countDown();
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        assertTrue(allThreadsFinishedLock.await(20, SECONDS));

        // While we in transaction, no changes should be sent
        assertEquals(0, testSubscriber.getOnNextEvents().size());

        storIOSQLite
                .internal()
                .endTransaction();

        testSubscriber.assertNoErrors();
        testSubscriber.assertReceivedOnNext(singletonList(Changes.newInstance(table)));
    }
}