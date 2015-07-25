package com.pushtorefresh.storio.internal;

import org.junit.Test;

import java.util.List;

import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;

public class RxChangesBusTest {

    @Test
    public void onNextShouldSendMessagesToObserver() {
        RxChangesBus<String> rxChangesBus = new RxChangesBus<String>();

        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();

        rxChangesBus
                .asObservable()
                .subscribe(testSubscriber);

        List<String> messages = asList("yo", ",", "wanna", "some", "messages?");

        for (String message : messages) {
            rxChangesBus.onNext(message);
        }

        testSubscriber.assertReceivedOnNext(messages);

        testSubscriber.assertNoErrors();
        testSubscriber.assertNoTerminalEvent();
    }
}
