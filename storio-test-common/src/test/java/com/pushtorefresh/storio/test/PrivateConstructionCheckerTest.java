package com.pushtorefresh.storio.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class PrivateConstructionCheckerTest {

    static class ClassWithPrivateConstructor {
        private ClassWithPrivateConstructor() {
        }
    }

    static class ClassWithDefaultProtectedConstructor {
        ClassWithDefaultProtectedConstructor() {
        }
    }

    static class ClassWithProtectedConstructor {
        ClassWithProtectedConstructor() {
        }
    }

    static class ClassWithPublicConstructor {
        ClassWithPublicConstructor() {
        }
    }

    @Test
    public void shouldAssertThatConstructorIsPrivateAndDoesNotThrowExceptions() {
        PrivateConstructorChecker
                .forClass(ClassWithPrivateConstructor.class)
                .prepare()
                .check();
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasDefaultModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithDefaultProtectedConstructor.class)
                    .prepare()
                    .check();

            fail();
        } catch (RuntimeException expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasProtectedModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithProtectedConstructor.class)
                    .prepare()
                    .check();

            fail();
        } catch (RuntimeException expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasPublicModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithPublicConstructor.class)
                    .prepare()
                    .check();

            fail();
        } catch (RuntimeException expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }
}
