package com.pushtorefresh.storio.sqlite.queries;

import com.google.common.collect.HashMultiset;

import org.junit.Test;

import static java.util.Arrays.asList;
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
}
