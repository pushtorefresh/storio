package com.pushtorefresh.storio;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;

public class StorIOExceptionTest {

    @Test
    public void checkConstructorWithDetailMessage() {
        StorIOException storIOException = new StorIOException("test detail message");
        assertThat(storIOException)
                .hasMessage("test detail message")
                .hasNoCause();
    }

    @Test
    public void checkConstructorWithDetailMessageAndThrowable() {
        Throwable testThrowable = new RuntimeException("yo");
        StorIOException storIOException = new StorIOException("test detail message", testThrowable);

        assertThat(storIOException).hasMessage("test detail message");
        assertThat(storIOException.getCause()).isSameAs(testThrowable);
    }

    @Test
    public void checkConstructorWithThrowable() {
        Throwable testThrowable = new RuntimeException("yo");
        StorIOException storIOException = new StorIOException(testThrowable);

        assertThat(storIOException.getCause()).isSameAs(testThrowable);
    }
}
