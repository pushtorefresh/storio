package com.pushtorefresh.storio.sqlite.query;

import com.google.common.collect.HashMultiset;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class RawQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullQuery() {
        new RawQuery.Builder()
                .query(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyQuery() {
        new RawQuery.Builder()
                .query("")
                .build();
    }

    @Test
    public void build() {
        final String query = "test_query";
        final Object[] args = {"arg1", "arg2", "arg3"};
        final String[] observesTables = {"table_to_observe_1", "table_to_observe_2"};
        final String[] affectsTables = {"table_to_affect_1", "table_to_affect_2"};

        final RawQuery rawQuery = new RawQuery.Builder()
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
