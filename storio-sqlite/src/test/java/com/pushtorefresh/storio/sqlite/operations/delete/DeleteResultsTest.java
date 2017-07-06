package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Java6Assertions.assertThat;

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
        assertThat(deleteResults.results()).isEqualTo(results);
    }

    @Test
    public void wasDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString", DeleteResult.newInstance(1, "test_table"));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertThat(deleteResults.wasDeleted("testString")).isTrue();
        assertThat(deleteResults.wasDeleted("should not be deleted")).isFalse();

        assertThat(deleteResults.wasNotDeleted("testString")).isFalse();
        assertThat(deleteResults.wasNotDeleted("should not be deleted")).isTrue();
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
