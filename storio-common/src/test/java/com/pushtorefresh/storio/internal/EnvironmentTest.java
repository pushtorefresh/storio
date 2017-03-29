package com.pushtorefresh.storio.internal;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import java.lang.reflect.Field;

import static java.lang.reflect.Modifier.FINAL;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class EnvironmentTest {

    @Test
    public void constructorShouldBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(Environment.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please")
                .check();
    }

    @Test
    public void rxJavaShouldBeInClassPath() {
        assertThat(Environment.RX_JAVA_IS_IN_THE_CLASS_PATH).isTrue();
    }

    @Test
    public void shouldThrowExceptionIfRxJavaIsNotInTheClassPath() throws NoSuchFieldException, IllegalAccessException {
        Field field = Environment.class.getDeclaredField("RX_JAVA_IS_IN_THE_CLASS_PATH");

        field.setAccessible(true);

        // Removing FINAL modifier
        Field modifiersFieldOfTheField = Field.class.getDeclaredField("modifiers");
        modifiersFieldOfTheField.setAccessible(true);
        modifiersFieldOfTheField.setInt(field, field.getModifiers() & ~FINAL);

        final Object prevValue = field.get(null);

        field.set(null, false); // No Environment will think that RxJava is not in the ClassPath

        try {
            Environment.throwExceptionIfRxJavaIsNotAvailable("yolo");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("yolo requires RxJava in classpath," +
                            " please add it as compile dependency to the application");
        } finally {
            // Return previous value of the field
            field.set(null, prevValue);

            // Restoring FINAL modifier (for better tests performance)
            modifiersFieldOfTheField.setInt(field, field.getModifiers() & FINAL);
        }
    }

    @Test
    public void shouldNotThrowExceptionIfRxJavaIsInTheClassPath() {
        // Because RxJava should be in the ClassPath for tests
        Environment.throwExceptionIfRxJavaIsNotAvailable("no exceptions please");
    }
}
