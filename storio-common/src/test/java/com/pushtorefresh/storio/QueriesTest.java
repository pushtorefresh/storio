package com.pushtorefresh.storio;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class QueriesTest {

    @Test
    public void constructorShouldBePrivate() {
        PrivateConstructorChecker
                .forClass(Queries.class)
                .check();
    }

    @Test
    public void placeholdersMinus1() {
        try {
            Queries.placeholders(-1);
            fail("Should throw exception");
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("numberOfPlaceholders must be > 0, but was = -1");
        }
    }

    @Test
    public void placeholders0() {
        try {
            Queries.placeholders(0);
            fail("Should throw exception");
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("numberOfPlaceholders must be > 0, but was = 0");
        }
    }

    @Test
    public void placeholders1() {
        assertThat(Queries.placeholders(1)).isEqualTo("?");
    }

    @Test
    public void placeholders2() {
        assertThat(Queries.placeholders(2)).isEqualTo("?,?");
    }

    @Test
    public void placeholders3() {
        assertThat(Queries.placeholders(3)).isEqualTo("?,?,?");
    }
}
