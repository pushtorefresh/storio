package com.pushtorefresh.storio.operation.group;

import com.pushtorefresh.storio.operation.PreparedOperation;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class GroupOperationResultsTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullResults() {
        GroupOperationResults.newInstance(null);
    }

    @Test
    public void results() {
        final Map<PreparedOperation<?>, Object> results = new HashMap<PreparedOperation<?>, Object>();

        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));

        final GroupOperationResults groupOperationResults = GroupOperationResults.newInstance(results);
        assertEquals(results, groupOperationResults.results());
    }
}
