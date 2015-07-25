package com.pushtorefresh.storio;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class StorIOExceptionTest {

    @Test
    public void checkConstructorWithDetailMessage() {
        StorIOException storIOException = new StorIOException("test detail message");
        assertEquals("test detail message", storIOException.getMessage());
    }

    @Test
    public void checkConstructorWithDetailMessageAndThrowable() {
        Throwable testThrowable = new RuntimeException("yo");
        StorIOException storIOException = new StorIOException("test detail message", testThrowable);

        assertEquals("test detail message", storIOException.getMessage());
        assertSame(testThrowable, storIOException.getCause());
    }

    @Test
    public void checkConstructorWithThrowable() {
        Throwable testThrowable = new RuntimeException("yo");
        StorIOException storIOException = new StorIOException(testThrowable);

        assertSame(testThrowable, storIOException.getCause());
    }
}
