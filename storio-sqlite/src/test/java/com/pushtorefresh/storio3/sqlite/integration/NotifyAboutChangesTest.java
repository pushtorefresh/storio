package com.pushtorefresh.storio3.sqlite.integration;


import android.os.SystemClock;

import com.pushtorefresh.storio3.sqlite.BuildConfig;
import com.pushtorefresh.storio3.sqlite.Changes;
import com.pushtorefresh.storio3.sqlite.StorIOSQLite;
import com.pushtorefresh.storio3.test.ConcurrencyTesting;
import com.pushtorefresh.storio3.test.Repeat;
import com.pushtorefresh.storio3.test.RepeatRule;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import io.reactivex.subscribers.TestSubscriber;

import static io.reactivex.BackpressureStrategy.LATEST;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Java6Assertions.assertThat;

@RunWith(RobolectricTestRunner.class)
public class NotifyAboutChangesTest extends BaseTest {

    @Rule
    public RepeatRule repeat = new RepeatRule();

    @Test
    public void notifyAboutChange() {
        TestSubscriber<Changes> testObserver = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testObserver);

        storIOSQLite
                .lowLevel()
                .notifyAboutChanges(Changes.newInstance("test_table"));

        final long startTime = SystemClock.elapsedRealtime();

        while (testObserver.valueCount() == 0
                && SystemClock.elapsedRealtime() - startTime < 1000) {
            Thread.yield(); // let other threads work
        }

        testObserver.assertValues(Changes.newInstance("test_table"));
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
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);

        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // All threads should start "simultaneously".
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    storIOSQLite
                            .lowLevel()
                            .notifyAboutChanges(Changes.newInstance("test_table" + finalI));
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        final long startTime = SystemClock.elapsedRealtime();

        while (testSubscriber.valueCount() != tables.size()
                && (SystemClock.elapsedRealtime() - startTime) < 20000) {
            Thread.yield(); // let other threads work
        }

        testSubscriber.assertNoErrors();

        // notice, that order of received notification can be different
        // but in total, they should be equal
        testSubscriber.assertValueCount(expectedChanges.size());
        assertThat(expectedChanges.containsAll(testSubscriber.values())).isTrue();
    }

    @Test
    public void shouldReceiveOneNotificationInTransactionWithOneThread() throws InterruptedException {
        final String table = "test_table";
        final int numberOfChanges = 20;

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        storIOSQLite
                .lowLevel()
                .beginTransaction();

        for (int i = 0; i < numberOfChanges; i++) {
            storIOSQLite
                    .lowLevel()
                    .notifyAboutChanges(Changes.newInstance(table));
        }

        // While we in transaction, no changes should be sent.
        testSubscriber.assertValueCount(0);

        storIOSQLite
                .lowLevel()
                .endTransaction();

        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(Changes.newInstance(table));
    }

    @Test
    public void shouldReceiveOneNotificationInTransactionWithMultipleThreads() throws InterruptedException {
        final String table = "test_table";
        final int numberOfThreads = 100;

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        storIOSQLite
                .lowLevel()
                .beginTransaction();

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);
        final CountDownLatch allThreadsFinishedLock = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // All threads should start "simultaneously".
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    storIOSQLite
                            .lowLevel()
                            .notifyAboutChanges(Changes.newInstance(table));

                    allThreadsFinishedLock.countDown();
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        assertThat(allThreadsFinishedLock.await(20, SECONDS)).isTrue();

        // While we in transaction, no changes should be sent.
        testSubscriber.assertValueCount(0);

        storIOSQLite
                .lowLevel()
                .endTransaction();

        testSubscriber.assertNoErrors();
        testSubscriber.assertValues(Changes.newInstance(table));
    }

    @Test
    @Repeat(times = 20)
    public void shouldReceiveOneNotificationWithAllAffectedTablesInTransactionWithMultipleThreads() throws InterruptedException {
        final String table1 = "test_table1";
        final String table2 = "test_table2";

        final int numberOfThreads = ConcurrencyTesting.optimalTestThreadsCount();

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

        lowLevel.beginTransaction();

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);
        final CountDownLatch allThreadsFinishedLock = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // All threads should start "simultaneously".
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    lowLevel.notifyAboutChanges(Changes.newInstance(table1));
                    lowLevel.notifyAboutChanges(Changes.newInstance(table2));

                    allThreadsFinishedLock.countDown();
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        assertThat(allThreadsFinishedLock.await(25, SECONDS)).isTrue();

        // While we in transaction, no changes should be sent.
        testSubscriber.assertValueCount(0);

        lowLevel.endTransaction();

        testSubscriber.assertNoErrors();

        List<Changes> actualChanges = testSubscriber.values();
        assertThat(actualChanges).hasSize(1);
        assertThat(actualChanges.get(0).affectedTables()).containsOnly("test_table1", "test_table2");
    }

    @Test
    public void shouldNotReceiveNotificationIfNoChangesAfterTransactionEnd() throws InterruptedException {
        final int numberOfThreads = 100;

        final TestSubscriber<Changes> testSubscriber = new TestSubscriber<Changes>();

        storIOSQLite
                .observeChanges(LATEST)
                .subscribe(testSubscriber);

        final StorIOSQLite.LowLevel lowLevel = storIOSQLite.lowLevel();

        lowLevel.beginTransaction();

        final CountDownLatch startAllThreadsLock = new CountDownLatch(1);
        final CountDownLatch allThreadsFinishedLock = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // All threads should start "simultaneously".
                        startAllThreadsLock.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    allThreadsFinishedLock.countDown();
                }
            }).start();
        }

        // Ready!
        // Steady!
        startAllThreadsLock.countDown(); // Go!

        assertThat(allThreadsFinishedLock.await(20, SECONDS)).isTrue();

        // While we in transaction, no changes should be sent.
        testSubscriber.assertValueCount(0);

        lowLevel.endTransaction();

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoValues();
    }
}
