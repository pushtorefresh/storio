package com.pushtorefresh.storio.test_without_rxjava;

import com.pushtorefresh.storio.operation.group.PreparedGroupOperation;

import org.junit.Test;

public class PreparedGroupOperationTest {

    @SuppressWarnings("ConstantConditions")
    @Test
    public void instantiateWithoutRxJava() {
        new PreparedGroupOperation.Builder()
                .addOperation(null)
                .prepare();
    }
}
