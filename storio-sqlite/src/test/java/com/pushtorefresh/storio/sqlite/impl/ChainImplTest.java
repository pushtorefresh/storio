package com.pushtorefresh.storio.sqlite.impl;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.sqlite.Interceptor;
import com.pushtorefresh.storio.sqlite.Interceptor.Chain;

import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Fail.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ChainImplTest {

    @Test
    public void proceed_shouldThrowIfIteratorEmpty() {
        try {
            final List<Interceptor> empty = Collections.emptyList();
            final Chain chain = new ChainImpl(empty.listIterator());
            chain.proceed(mock(PreparedOperation.class));
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException e) {
            assertThat(e).hasMessage("proceed was called on empty iterator");
        }
    }
}