package com.pushtorefresh.storio.internal;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import static com.pushtorefresh.storio.internal.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.internal.Checks.checkNotNull;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class ChecksTest {

    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(Checks.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please.")
                .check();
    }

    @Test
    public void checkNotNullPositive() {
        checkNotNull(new Object(), "No exceptions please");
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNullNegative() {
        checkNotNull(null, "Throw me!");
    }

    @Test
    public void checkNotNullExceptionMessage() {
        try {
            checkNotNull(null, "expected message");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("expected message");
        }
    }

    @Test
    public void checkNotEmptyPositive() {
        checkNotEmpty("Not empty string", "No exceptions please");
    }

    @Test(expected = NullPointerException.class)
    public void checkNotEmptyNullNegative() {
        checkNotEmpty(null, "Throw me!");
    }

    @Test(expected = IllegalStateException.class)
    public void checkNotEmptyEmptyNegative() {
        checkNotEmpty("", "Throw me!");
    }

    @Test
    public void checkNotEmptyExceptionMessage() {
        try {
            checkNotEmpty(null, "expected message");
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException e) {
            assertThat(e).hasMessage("expected message");
        }
    }

    @Test
    public void checkNotEmptyEmptyExceptionMessage() {
        try {
            checkNotEmpty("", "expected message");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("expected message");
        }
    }
}
