package com.pushtorefresh.storio.test;

import android.support.annotation.NonNull;

import org.junit.Test;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.exceptions.OnErrorNotImplementedException;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class AbstractEmissionCheckerTest {

    @SuppressWarnings("unchecked")
    @Test
    public void verifySubscribeBehavior() {
        final AtomicBoolean onSubscribeWasCalled = new AtomicBoolean(false);

        final Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                onSubscribeWasCalled.set(true);
                subscriber.onNext("test_value");
                subscriber.onCompleted();
            }
        });

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(new LinkedList<String>()) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return observable
                        .subscribe();
            }
        };

        // Should not subscribe before manual call to subscribe
        assertFalse(onSubscribeWasCalled.get());

        Subscription subscription = emissionChecker.subscribe();

        // Should subscribe to observable
        assertTrue(onSubscribeWasCalled.get());

        subscription.unsubscribe();
    }

    @Test
    public void shouldAssertThatNextExpectedValueReceived() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("test_value");

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return Observable
                        .just("test_value")
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        Subscription subscription = emissionChecker.subscribe();

        // Should not throw exception
        emissionChecker.awaitNextExpectedValue();

        subscription.unsubscribe();
    }

    @Test
    public void shouldNotAssertThatNextExpectedValueReceivedInCaseOfAnotherValue() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return Observable
                        .just("another_value")
                        .subscribeOn(Schedulers.computation())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        Subscription subscription = emissionChecker.subscribe();

        try {
            emissionChecker.awaitNextExpectedValue();
            fail();
        } catch (AssertionError expected) {
            // it's okay
        } finally {
            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldNotAssertThatNextExpectedValueReceivedBecauseOfTimeout() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @Override
            protected long timeoutMillis() {
                return 1000;
            }

            @NonNull
            @Override
            public Subscription subscribe() {
                return Observable
                        .just("expected_value")
                        .delay(2, SECONDS) // ha!
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        Subscription subscription = emissionChecker.subscribe();

        try {
            emissionChecker.awaitNextExpectedValue();
            fail();
        } catch (AssertionError expected) {
            // it's okay
        } finally {
            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldAssertThatNoExpectedValuesLeft() {
        Queue<String> expectedValues = new LinkedList<String>();

        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");

        final PublishSubject<String> publishSubject = PublishSubject.create();

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return publishSubject
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        Subscription subscription = emissionChecker.subscribe();

        publishSubject.onNext("1");

        // "1"
        emissionChecker.awaitNextExpectedValue();

        publishSubject.onNext("2");

        // "2"
        emissionChecker.awaitNextExpectedValue();

        publishSubject.onNext("3");

        // "3"
        emissionChecker.awaitNextExpectedValue();

        // Should not throw exception
        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    public void shouldNotAssertThatNoExpectedValuesLeft() {
        Queue<String> expectedValues = new LinkedList<String>();
        expectedValues.add("expected_value");

        AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return Observable
                        .just("expected_value")
                        .subscribe(); // Don't pass value to emission checker
            }
        };

        Subscription subscription = emissionChecker.subscribe();

        try {
            emissionChecker.assertThatNoExpectedValuesLeft();
            fail();
        } catch (AssertionError expected) {
            // it's okay, we didn't call emissionChecker.awaitNextExpectedValue()
        } finally {
            subscription.unsubscribe();
        }
    }

    @Test
    public void shouldStoreItemsInQueueAndThenAwaitNextExpectedValues() {
        final Queue<String> expectedValues = new LinkedList<String>();

        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");

        final PublishSubject<String> publishSubject = PublishSubject.create();

        final AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return publishSubject
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        // Notice: We emit several values before awaiting any of them

        publishSubject.onNext("1");
        publishSubject.onNext("2");
        publishSubject.onNext("3");

        // Now we should successfully await all these items one by one
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        subscription.unsubscribe();
    }

    @Test
    public void shouldThrowExcepionBecauseObservableEmittedUnexpectedItemAfterExpectedSequence() {
        final Queue<String> expectedValues = new LinkedList<String>();

        expectedValues.add("1");
        expectedValues.add("2");
        expectedValues.add("3");

        final PublishSubject<String> publishSubject = PublishSubject.create();

        final AbstractEmissionChecker<String> emissionChecker = new AbstractEmissionChecker<String>(expectedValues) {
            @NonNull
            @Override
            public Subscription subscribe() {
                return publishSubject
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String s) {
                                onNextObtained(s);
                            }
                        });
            }
        };

        final Subscription subscription = emissionChecker.subscribe();

        publishSubject.onNext("1");
        publishSubject.onNext("2");
        publishSubject.onNext("3");

        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();
        emissionChecker.awaitNextExpectedValue();

        emissionChecker.assertThatNoExpectedValuesLeft();

        try {
            publishSubject.onNext("4");
            fail();
        } catch (OnErrorNotImplementedException expected) {
            assertTrue(expected.getCause().getMessage().startsWith("Received emission, but no more emissions were expected: obtained "));
        } finally {
            subscription.unsubscribe();
        }
    }
}
