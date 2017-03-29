package com.pushtorefresh.storio.test;

import com.pushtorefresh.private_constructor_checker.PrivateConstructorChecker;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class AssertsTest {

    @Test
    public void constructorMustBePrivateAndThrowException() {
        PrivateConstructorChecker
                .forClass(Asserts.class)
                .expectedTypeOfException(IllegalStateException.class)
                .expectedExceptionMessage("No instances please.")
                .check();
    }

    @Test
    public void shouldAssertThatListIsImmutable() {
        // Should not throw exception
        Asserts.assertThatListIsImmutable(Collections.unmodifiableList(new ArrayList<Object>()));
    }

    @Test
    public void shouldAssetThatEmptyListIsImmutable() {
        // Should not throw exception
        Asserts.assertThatListIsImmutable(Collections.EMPTY_LIST);
    }

    @Test(expected = AssertionError.class)
    public void shouldNotAssertThatListIsImmutable() {
        Asserts.assertThatListIsImmutable(new ArrayList<Object>());
    }

    @Test
    public void shouldCheckThatListIsImmutableByCallingAdd() {
        //noinspection unchecked
        List<Object> list = mock(List.class);

        try {
            Asserts.assertThatListIsImmutable(list);
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage("List is not immutable: list = " + list);
        }

        verify(list).add(any());
        verifyNoMoreInteractions(list);
    }

    @Test
    public void shouldCheckThatListIsImmutableByCallingRemove() {
        //noinspection unchecked
        List<Object> list = mock(List.class);

        when(list.add(any()))
                .thenThrow(new UnsupportedOperationException("add() not supported"));

        try {
            Asserts.assertThatListIsImmutable(list);
            failBecauseExceptionWasNotThrown(AssertionError.class);
        } catch (AssertionError expected) {
            assertThat(expected).hasMessage("List is not immutable: list = " + list);
        }

        verify(list).add(any());
        verify(list).remove(0);
        verifyNoMoreInteractions(list);
    }
}
