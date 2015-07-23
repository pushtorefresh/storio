package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class DeleteResultsTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullResults() {
        DeleteResults.newInstance(null);
    }

    @Test
    public void results() {
        final Map<Object, DeleteResult> results = new HashMap<Object, DeleteResult>();
        final DeleteResults<Object> deleteResults = DeleteResults.newInstance(results);
        assertEquals(results, deleteResults.results());
    }

    @Test
    public void wasDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString", DeleteResult.newInstance(1, "test_table"));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertTrue(deleteResults.wasDeleted("testString"));
        assertFalse(deleteResults.wasDeleted("should not be deleted"));

        assertFalse(deleteResults.wasNotDeleted("testString"));
        assertTrue(deleteResults.wasNotDeleted("should not be deleted"));
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(DeleteResults.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(DeleteResults.class)
                .check();
    }
}
