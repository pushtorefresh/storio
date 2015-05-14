package com.pushtorefresh.storio.sqlite.impl;


import android.os.SystemClock;
import android.support.test.runner.AndroidJUnit4;

import com.pushtorefresh.storio.sqlite.Changes;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import rx.observers.TestObserver;
import rx.observers.TestSubscriber;

import static java.util.Collections.singletonList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
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
        final int numberOfThreads = 100;

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

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        for (int i = 0; i < numberOfThreads; i++) {
            final int finalI = i;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // all threads should start simultaneously
                        countDownLatch.await();

                        storIOSQLite
                                .internal()
                                .notifyAboutChanges(Changes.newInstance("test_table" + finalI));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            }).start();
        }

        countDownLatch.countDown();

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
}