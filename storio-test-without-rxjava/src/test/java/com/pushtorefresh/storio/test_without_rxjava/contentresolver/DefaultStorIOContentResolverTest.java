package com.pushtorefresh.storio.test_without_rxjava.contentresolver;

import android.content.ContentResolver;

import com.pushtorefresh.storio.contentresolver.impl.DefaultStorIOContentResolver;

import org.junit.Test;

import static org.mockito.Mockito.mock;

public class DefaultStorIOContentResolverTest {

    @Test
    public void instantiateWithoutRxJava() {
        // Should not fail
        new DefaultStorIOContentResolver.Builder()
                .contentResolver(mock(ContentResolver.class))
                .build();
    }
}
