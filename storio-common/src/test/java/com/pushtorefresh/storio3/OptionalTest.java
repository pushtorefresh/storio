package com.pushtorefresh.storio3;

import com.pushtorefresh.storio3.test.ToStringChecker;

import org.assertj.core.api.ThrowableAssert;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Java6Assertions.assertThat;

public class OptionalTest {

    @Test
    public void emptyHasNullValue() {
        assertThat(Optional.empty().orNull()).isEqualTo(null);
    }

    @Test
    public void emptyReturnsTheSameInstance() {
        assertThat(Optional.empty()).isSameAs(Optional.empty());
    }

    @Test
    public void ofNullValueReturnsEmpty() {
        assertThat(Optional.of(null)).isSameAs(Optional.empty());
    }

    @Test
    public void ofNotNullValueReturnsEqualInstances() {
        assertThat(Optional.of("some value"))
                .isEqualTo(Optional.of("some value"));
    }

    @Test
    public void getReturnsValueIfPresent() {
        assertThat(Optional.of("some value").get()).isEqualTo("some value");
    }

    @Test
    public void getThrowsIfNull() {
        final Optional<Object> empty = Optional.empty();
        assertThatThrownBy(new ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() throws Throwable {
                empty.get();
            }
        }).isInstanceOf(NullPointerException.class)
                .hasMessage(null)
                .hasNoCause();
    }

    @Test
    public void isPresentReturnsTrueIfNotNull() {
        assertThat(Optional.of("some value").isPresent()).isTrue();
    }

    @Test
    public void isPresentReturnsFalseIfNull() {
        assertThat(Optional.empty().isPresent()).isFalse();
    }

    @Test
    public void orReturnsValueIfPresent() {
        assertThat(Optional.of("some value").or("default value")).
                isEqualTo("some value");
    }

    @Test
    public void orReturnsDefaultIfNotPresent() {
        assertThat(Optional.empty().or("default value")).
                isEqualTo("default value");
    }


    @Test
    public void orNullReturnsValueIfPresent() {
        assertThat(Optional.of("some value").orNull()).
                isEqualTo("some value");
    }

    @Test
    public void orNullReturnsNullIfNotPresent() {
        assertThat(Optional.empty().orNull()).isNull();
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Optional.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Optional.class, Optional.of("some value"), Optional.of("another value"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(Optional.class)
                .check();
    }
}
