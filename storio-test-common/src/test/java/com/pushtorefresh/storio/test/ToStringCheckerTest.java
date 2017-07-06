package com.pushtorefresh.storio.test;

import android.net.Uri;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonMap;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

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

            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage("toString() does not contain field = a, " +
                            "object = ClassWithIncorrectToString{b='some_string'}");
        }
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveInt() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(int.class);

        assertThat(sample).isEqualTo(1);
    }

    @Test
    public void shouldCreateSampleValueOfInteger() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Integer.class);

        assertThat(sample).isEqualTo(1);
    }

    @Test
    public void shouldCreateSampleValueOfPrimitiveLong() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(long.class);

        assertThat(sample).isEqualTo(1L);
    }

    @Test
    public void shouldCreateSampleValueOfLong() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Long.class);

        assertThat(sample).isEqualTo(1L);
    }

    @Test
    public void shouldCreateSampleValueOfString() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(String.class);

        assertThat(sample).isEqualTo("some_string");
    }

    @Test
    public void shouldCreateSampleValueOfList() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(List.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(asList("1", "2", "3"));
    }

    @Test
    public void shouldCreateSampleValueOfMap() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Map.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(singletonMap("map_key", "map_value"));
    }

    @Test
    public void shouldCreateSampleValueOfSet() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Set.class);

        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertThat(sample).isEqualTo(singleton("set_item"));
    }

    @Test
    public void shouldCreateSampleValueOfUri() {
        Object sample = new ToStringChecker<Object>(Object.class)
                .createSampleValueOfType(Uri.class);

        // We can not check equality of Uri instance here…
        assertThat(sample).isInstanceOf(Uri.class);
    }
}
