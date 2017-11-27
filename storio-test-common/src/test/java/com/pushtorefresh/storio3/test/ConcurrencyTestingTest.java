package com.pushtorefresh.storio3.test;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class ConcurrencyTestingTest {

    @Test
    public void optimalTestThreadsCountIsAtLeast4() {
        assertThat(ConcurrencyTesting.optimalTestThreadsCount()).isGreaterThanOrEqualTo(4);
    }

    @Test
    public void constructorMustBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(ConcurrencyTesting.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please.")
                .check();
    }
}
