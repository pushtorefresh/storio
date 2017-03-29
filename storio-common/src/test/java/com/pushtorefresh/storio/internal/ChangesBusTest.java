package com.pushtorefresh.storio.internal;

import org.junit.Test;

import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Fail.fail;

public class ChangesBusTest {

    @Test
    public void asObservableShouldNotReturnNullIfRxJavaInClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(true);
        assertThat(changesBus.asObservable()).isNotNull();
    }

    @Test
    public void asObservableShouldReturnNullIfRxJavaIsNotInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(false);
        assertThat(changesBus.asObservable()).isNull();
    }

    @Test
    public void onNextShouldNotThrowExceptionIfRxJavaIsNotInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(false);

        try {
            changesBus.onNext("don't crash me bro");
        } catch (Exception e) {
            fail("Yo, WTF dude?");
        }
    }

    @Test
    public void onNextShouldSendMessagesToObserverIfRxJavaIsInTheClassPath() {
        ChangesBus<String> changesBus = new ChangesBus<String>(true);

        TestSubscriber<String> testSubscriber = new TestSubscriber<String>();

        Observable<String> observable = changesBus.asObservable();
        assertThat(observable).isNotNull();

        //noinspection ConstantConditions
        observable.subscribe(testSubscriber);

        List<String> messages = asList("My", "life", "my", "rules", "please?");

        for (String message: messages) {
            changesBus.onNext(message);
        }

        testSubscriber.assertReceivedOnNext(messages);
        testSubscriber.assertNoTerminalEvent();
    }
}
