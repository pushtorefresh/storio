package com.pushtorefresh.storio.util;

import org.junit.Test;

import static com.pushtorefresh.storio.util.Checks.checkNotEmpty;
import static com.pushtorefresh.storio.util.Checks.checkNotNull;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class ChecksTest {

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
            fail("NullPointerException should be thrown");
        } catch (NullPointerException e) {
            assertEquals("expected message", e.getMessage());
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
            fail("NullPointerException should be thrown");
        } catch (NullPointerException e) {
            assertEquals("expected message", e.getMessage());
        }
    }

    @Test
    public void checkNotEmptyEmptyExceptionMessage() {
        try {
            checkNotEmpty("", "expected message");
            fail("IllegalStateException should be thrown");
        } catch (IllegalStateException e) {
            assertEquals("expected message", e.getMessage());
        }
    }
}
