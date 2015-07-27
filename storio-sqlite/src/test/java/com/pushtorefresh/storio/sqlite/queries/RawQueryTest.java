package com.pushtorefresh.storio.sqlite.queries;

import com.google.common.collect.HashMultiset;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class RawQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullQuery() {
        RawQuery.builder()
                .query(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyQuery() {
        RawQuery.builder()
                .query("")
                .build();
    }

    @Test
    public void argsShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder()
                .query("lalala I know SQL")
                .build();

        assertNotNull(rawQuery.args());
        assertTrue(rawQuery.args().isEmpty());
    }

    @Test
    public void observesTablesShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder()
                .query("lalala I know SQL")
                .build();

        assertNotNull(rawQuery.observesTables());
        assertTrue(rawQuery.observesTables().isEmpty());
    }

    @Test
    public void affectsTablesShouldNotBeNull() {
        RawQuery rawQuery = RawQuery.builder()
                .query("lalala I know SQL")
                .build();

        assertNotNull(rawQuery.affectsTables());
        assertTrue(rawQuery.affectsTables().isEmpty());
    }

    @Test
    public void shouldRewriteAffectsTablesOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder()
                .query("test_query")
                .affectsTables("first_call")
                .affectsTables("second_call")
                .build();

        assertEquals(singleton("second_call"), rawQuery.affectsTables());
    }

    @Test
    public void shouldRewriteObservesTablesOnSecondCall() {
        RawQuery rawQuery = RawQuery.builder()
                .query("test_query")
                .observesTables("first_call")
                .observesTables("second_call")
                .build();

        assertEquals(singleton("second_call"), rawQuery.observesTables());
    }

    @Test
    public void buildWithNormalValues() {
        final String query = "test_query";
        final Object[] args = {"arg1", "arg2", "arg3"};
        final String[] observesTables = {"table_to_observe_1", "table_to_observe_2"};
        final String[] affectsTables = {"table_to_affect_1", "table_to_affect_2"};

        final RawQuery rawQuery = RawQuery.builder()
                .query(query)
                .args(args)
                .observesTables(observesTables)
                .affectsTables(affectsTables)
                .build();

        assertEquals(query, rawQuery.query());
        assertEquals(asList(args), rawQuery.args());
        assertEquals(HashMultiset.create(asList(observesTables)), HashMultiset.create(rawQuery.observesTables()));
        assertEquals(HashMultiset.create(asList(affectsTables)), HashMultiset.create(rawQuery.affectsTables()));
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(RawQuery.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(RawQuery.class)
                .check();
    }
}
