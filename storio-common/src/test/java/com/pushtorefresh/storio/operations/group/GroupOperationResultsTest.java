package com.pushtorefresh.storio.operations.group;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

public class GroupOperationResultsTest {

    @Test
    public void nullResults() {
        try {
            //noinspection ConstantConditions
            GroupOperationResults.newInstance(null);
            fail();
        } catch (NullPointerException expected) {
        }
    }

    @Test
    public void passedResultsShouldBeEqualsToActual() {
        final Map<PreparedOperation<?>, Object> results = new HashMap<PreparedOperation<?>, Object>();

        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));

        final GroupOperationResults groupOperationResults = GroupOperationResults.newInstance(results);
        assertEquals(results, groupOperationResults.results());
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(GroupOperationResults.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(GroupOperationResults.class)
                .check();
    }
}
