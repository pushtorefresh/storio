package com.pushtorefresh.storio.contentresolver.operation.delete;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class DeleteResultsTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullResults() {
        DeleteResults.newInstance(null);
    }

    @Test
    public void results() {
        final Map<String, DeleteResult> results = new HashMap<>();
        results.put("testString", mock(DeleteResult.class));
        results.put("testString", mock(DeleteResult.class));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertEquals(results, deleteResults.results());
    }
}
