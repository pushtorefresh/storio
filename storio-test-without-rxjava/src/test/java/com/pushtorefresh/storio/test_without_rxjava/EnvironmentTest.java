package com.pushtorefresh.storio.test_without_rxjava;

import com.pushtorefresh.storio.internal.Environment;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public class EnvironmentTest {

    @Test
    public void noRxJavaInClassPath() {
        assertFalse(Environment.RX_JAVA_IS_IN_THE_CLASS_PATH);
    }

    @Test(expected = ClassNotFoundException.class)
    public void rxJavaIsReallyNotInClassPath() throws ClassNotFoundException {
        Class.forName("rx.Observable");
    }
}
