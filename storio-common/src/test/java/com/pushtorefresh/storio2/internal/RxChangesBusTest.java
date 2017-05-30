package com.pushtorefresh.storio2.internal;

import org.junit.Test;

import java.util.List;

import io.reactivex.subscribers.TestSubscriber;

import static java.util.Arrays.asList;

public class RxChangesBusTest {

    @Test
    public void onNextShouldSendMessagesToObserver() {
        RxChangesBus<String> rxChangesBus = new RxChangesBus<String>();

        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();

        rxChangesBus
                .asFlowable()
                .subscribe(testSubscriber);

        List<String> messages = asList("yo", ",", "wanna", "some", "messages?");

        for (String message : messages) {
            rxChangesBus.onNext(message);
        }

        testSubscriber.assertValueSequence(messages);
        testSubscriber.assertNotTerminated();
    }
}
