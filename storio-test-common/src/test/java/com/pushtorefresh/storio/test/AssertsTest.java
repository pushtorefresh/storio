package com.pushtorefresh.storio.test;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;

public class AssertsTest {

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
}
