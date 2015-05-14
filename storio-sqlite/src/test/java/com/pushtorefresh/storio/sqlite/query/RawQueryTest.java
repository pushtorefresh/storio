package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

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

    public void build() {
        final String query = "test_query";
        final Object[] args = {"arg1", "arg2", "arg3"};
        final String[] affectedTables = {"table1", "table2", "table3"};

        final RawQuery rawQuery = new RawQuery.Builder()
                .query(query)
                .args(args)
                .affectedTables(affectedTables)
                .build();

        assertEquals(query, rawQuery.query());
        assertEquals(Arrays.asList(args), rawQuery.args());
        assertEquals(Arrays.asList(affectedTables), rawQuery.affectedTables());
    }
}
