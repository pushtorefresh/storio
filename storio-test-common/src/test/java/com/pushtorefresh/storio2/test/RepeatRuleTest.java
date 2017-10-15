package com.pushtorefresh.storio2.test;

import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RepeatRuleTest {

    private Statement test = mock(Statement.class);
    private Description description = mock(Description.class);
    private Repeat repeat = mock(Repeat.class);

    @Test
    public void repeats10Times() throws Throwable {
        when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        when(repeat.times()).thenReturn(10);

        new RepeatRule().apply(test, description).evaluate();

        verify(test, times(10)).evaluate();
    }

    @Test
    public void noAnnotation() throws Throwable {
        when(description.getAnnotation(Repeat.class)).thenReturn(null);

        new RepeatRule().apply(test, description).evaluate();

        verify(test).evaluate();
    }

    @Test
    public void lessThan1Time() throws Throwable {
        when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        when(repeat.times()).thenReturn(0);

        try {
            new RepeatRule().apply(test, description).evaluate();
            fail();
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Repeat times should be >= 1, times = 0");
        }
    }

    @Test
    public void lazyWithoutAnnotation() throws Throwable {
        when(description.getAnnotation(Repeat.class)).thenReturn(null);

        new RepeatRule().apply(test, description);

        verify(test, never()).evaluate();
    }

    @Test
    public void lazyWithAnnotation() throws Throwable {
        when(description.getAnnotation(Repeat.class)).thenReturn(repeat);
        when(repeat.times()).thenReturn(10);

        new RepeatRule().apply(test, description);

        verify(test, never()).evaluate();
    }
}
