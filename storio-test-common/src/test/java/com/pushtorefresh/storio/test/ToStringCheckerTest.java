package com.pushtorefresh.storio.test;

import android.net.Uri;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class ToStringCheckerTest {

    static class ClassWithCorrectToString {

        int a;
        String b;

        @Override
        public String toString() {
            return "ClassWithCorrectToString{" +
                    "a=" + a +
                    ", b='" + b + '\'' +
                    '}';
        }
    }

    @Test
    public void shouldCheckSuccessfully() {
        ToStringChecker
                .forClass(ClassWithCorrectToString.class)
                .check();
    }

    static class ClassWithIncorrectToString {
        int a;
        String b;

        @Override
        public String toString() {
            return "ClassWithIncorrectToString{" +
                    "b='" + b + '\'' +
                    '}';
        }
    }

    @Test
    public void shouldThrowExceptionBecauseOneOfTheFieldsIsNotIncludedIntoToString() {
        try {
            ToStringChecker
                    .forClass(ClassWithIncorrectToString.class)
                    .check();

            fail();
        } catch (AssertionError expected) {
            assertEquals("toString() does not contain field = a, " +
                    "object = ClassWithIncorrectToString{b='some_string'}",
                    expected.getMessage()
            );
        }
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveInt() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(int.class);

        assertEquals(1, sample);
    }

    @Test
    public void shouldCreateSampleValueOfInteger() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Integer.class);

        assertEquals(1, sample);
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveLong() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(long.class);

        assertEquals(1L, sample);
    }

    @Test
    public void shouldCreateSampleValueOfLong() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Long.class);

        assertEquals(1L, sample);
    }

    @Test
    public void shouldCreateSampleValueOfString() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(String.class);

        assertEquals("some_string", sample);
    }

    @Test
    public void shouldCreateSampleValueOfList() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(List.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(asList("1", "2", "3"), sample);
    }

    @Test
    public void shouldCreateSampleValueOfMap() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Map.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(singletonMap("map_key", "map_value"), sample);
    }

    @Test
    public void shouldCreateSampleValueOfSet() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Set.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(singleton("set_item"), sample);
    }

    @Test
    public void shouldCreateSampleValueOfUri() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Uri.class);

        // We can not check equality of Uri instance here…
        assertTrue(sample instanceof Uri);
    }
}
