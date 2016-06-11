package com.pushtorefresh.storio.contentresolver.operations.internal;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

public class RxJavaUtilsTest {

    @Test
    public void constructorShouldBePrivate() {
        PrivateConstructorChecker
                .forClass(RxJavaUtils.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please.")
                .check();
    }
}
