package com.pushtorefresh.storio;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

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
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException expected) {
            assertThat(expected)
                    .hasMessage("numberOfPlaceholders must be >= 0, but was = -1")
                    .hasNoCause();
        }
    }

    @Test
    public void placeholders0() {
        assertThat(Queries.placeholders(0)).isEqualTo("");
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
