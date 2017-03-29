package com.pushtorefresh.storio.test;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class UtilsTest {

    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(Utils.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please!")
                .check();
    }

    @Test
    public void maxSdkVersionShouldBeAtLeast23() {
        assertThat(Utils.MAX_SDK_VERSION).isGreaterThanOrEqualTo(23);
    }

    @Test
    public void minSdkVersionShouldBe14() {
        assertThat(Utils.MIN_SDK_VERSION).isEqualTo(14);
    }
}
