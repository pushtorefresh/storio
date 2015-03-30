package com.pushtorefresh.storio.util;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.fail;

public class ChecksTest {

    @Test
    public void checkNotNullPositive() {
        Checks.checkNotNull(new Object(), "No exceptions please");
    }

    @Test(expected = NullPointerException.class)
    public void checkNotNullNegative() {
        Checks.checkNotNull(null, "Throw me!");
    }

    @Test
    public void checkNotNullExceptionMessage() {
        try {
            Checks.checkNotNull(null, "expected message");
            fail("NullPointerException should be thrown");
        } catch (NullPointerException e) {
            assertEquals("expected message", e.getMessage());
        }
    }
}
