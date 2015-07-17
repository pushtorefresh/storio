package com.pushtorefresh.storio.operations.group;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.operations.PreparedOperation;
import com.pushtorefresh.storio.test.Tests;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;
import rx.Observable;

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
    public void verifyEqualsAndHashCode() {
        EqualsVerifier
                .forClass(GroupOperationResults.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToString() {
        GroupOperationResults groupOperationResults = GroupOperationResults.newInstance(new HashMap<PreparedOperation<?>, Object>() {
            {
                put(new PreparedOperation<Object>() {
                    @NonNull
                    @Override
                    public Object executeAsBlocking() {
                        //noinspection ConstantConditions
                        return null;
                    }

                    @NonNull
                    @Override
                    public Observable<Object> createObservable() {
                        //noinspection ConstantConditions
                        return null;
                    }

                    @Override
                    public String toString() {
                        return "some operation";
                    }
                }, "some value");
            }
        });

        Tests.checkToString(groupOperationResults);
    }
}
