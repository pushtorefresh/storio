package com.pushtorefresh.storio.operations.group;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;

public class GroupOperationResultsTest {

    @Test
    public void nullResults() {
        try {
            //noinspection ConstantConditions
            GroupOperationResults.newInstance(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Please specify results of Group Operation")
                    .hasNoCause();
        }
    }

    @Test
    public void passedResultsShouldBeEqualsToActual() {
        final Map<PreparedOperation<?>, Object> results = new HashMap<PreparedOperation<?>, Object>();

        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));
        results.put(mock(PreparedOperation.class), mock(Object.class));

        final GroupOperationResults groupOperationResults = GroupOperationResults.newInstance(results);
        assertThat(groupOperationResults.results()).isEqualTo(results);
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
