package com.pushtorefresh.storio.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PrivateConstructionCheckerTest {

    @Test
    public void builderShouldPreventSettingExceptionMessageWithoutExceptionType() {
        try {
            PrivateConstructorChecker
                    .forClass(Object.class)
                    .expectedExceptionMessage("test message")
                    .check();

            fail();
        } catch (IllegalStateException expected) {
            assertEquals(
                    "You can not set expected exception message without expected exception type",
                    expected.getMessage()
            );
        }
    }

    static class ClassWithoutDefaultConstructor {
        private ClassWithoutDefaultConstructor(String someParam) {
        }
    }

    @Test
    public void shouldThrowExceptionIfClassDoesNotHaveDeclaredConstructors() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithoutDefaultConstructor.class)
                    .check();

            fail();
        } catch (RuntimeException expected) {
            assertEquals(
                    "Can not get default declared constructor for class = " + ClassWithoutDefaultConstructor.class,
                    expected.getMessage()
            );

            assertTrue(expected.getCause() instanceof NoSuchMethodException);
        }
    }

    static class ClassWithPrivateConstructor {
        private ClassWithPrivateConstructor() {
        }
    }

    @Test
    public void shouldAssertThatConstructorIsPrivateAndDoesNotThrowExceptions() {
        PrivateConstructorChecker
                .forClass(ClassWithPrivateConstructor.class)
                .check();
    }

    static class ClassWithDefaultProtectedConstructor {
        ClassWithDefaultProtectedConstructor() {
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasDefaultModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithDefaultProtectedConstructor.class)
                    .check();

            fail();
        } catch (AssertionError expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }

    static class ClassWithProtectedConstructor {
        ClassWithProtectedConstructor() {
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasProtectedModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithProtectedConstructor.class)
                    .check();

            fail();
        } catch (AssertionError expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }

    static class ClassWithPublicConstructor {
        ClassWithPublicConstructor() {
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorHasPublicModifier() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithPublicConstructor.class)
                    .check();

            fail();
        } catch (AssertionError expected) {
            assertEquals("Constructor must be private", expected.getMessage());
        }
    }

    static class ClassWithConstructorThatThrowsException {
        private ClassWithConstructorThatThrowsException() {
            throw new IllegalStateException("test exception");
        }
    }

    @Test
    public void shouldCheckThatConstructorThrowsExceptionWithoutCheckingMessage() {
        PrivateConstructorChecker
                .forClass(ClassWithConstructorThatThrowsException.class)
                .expectedTypeOfException(IllegalStateException.class)
                .check();
    }

    @Test
    public void shouldCheckThatConstructorThrowsExceptionWithExpectedMessage() {
        PrivateConstructorChecker
                .forClass(ClassWithConstructorThatThrowsException.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("test exception")
                .check();
    }

    @Test
    public void shouldThrowExceptionBecauseTypeOfExpectedExceptionDoesNotMatch() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithConstructorThatThrowsException.class)
                    .expectedTypeOfException(IllegalArgumentException.class) // Incorrect type
                    .check();

            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Expected exception of type = class java.lang.IllegalArgumentException, " +
                            "but was exception of type = java.lang.reflect.InvocationTargetException",
                    expected.getMessage()
            );
        }
    }

    @Test
    public void shouldThrowExceptionBecauseExpectedMessageDoesNotMatch() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithConstructorThatThrowsException.class)
                    .expectedTypeOfException(IllegalStateException.class) // Correct type
                    .expectedExceptionMessage("lol, not something that you've expected?") // Incorrect message
                    .check();

            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Expected exception message = 'lol, not something that you've expected?', " +
                            "but was = 'test exception'",
                    expected.getMessage()
            );
        }
    }

    @Test
    public void shouldThrowExceptionBecauseConstructorThrownUnexpectedException() {
        try {
            PrivateConstructorChecker
                    .forClass(ClassWithConstructorThatThrowsException.class)
                    .check(); // We don't expect exception, but it will be thrown

            fail();
        } catch (IllegalStateException expected) {
            assertEquals("No exception was expected", expected.getMessage());
        }
    }
}
