package com.pushtorefresh.storio.test_without_rxjava;

import com.pushtorefresh.storio.internal.Environment;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class EnvironmentTest {

    @Test
    public void noRxJavaInClassPath() {
        assertThat(Environment.RX_JAVA_IS_IN_THE_CLASS_PATH).isFalse();
    }

    @Test(expected = ClassNotFoundException.class)
    public void rxJavaIsReallyNotInClassPath() throws ClassNotFoundException {
        Class.forName("rx.Observable");
    }
}
