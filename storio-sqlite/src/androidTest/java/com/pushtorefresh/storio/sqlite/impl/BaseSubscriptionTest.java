package com.pushtorefresh.storio.sqlite.impl;

import android.support.annotation.NonNull;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;

import rx.Subscription;

import static java.util.concurrent.TimeUnit.SECONDS;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public abstract class BaseSubscriptionTest extends BaseTest {

    public abstract class BaseEmissionChecker<T> {
        private final Queue<T> expected;
        private CountDownLatch lock;

        public BaseEmissionChecker(@NonNull Queue<T> expected) {
            this.expected = expected;
            lock = new CountDownLatch(expected.size());
        }

        public boolean syncWait() {
            try {
                lock.await(15, SECONDS);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            return lock.getCount() == 0;
        }

        protected void onNextObtained(T obtained) {
            T expectedItem = expected.remove();
            assertEquals(expectedItem, obtained);
            assertTrue(lock.getCount() > 0);
            lock.countDown();
        }

        public abstract Subscription subscribe();
    }
}
