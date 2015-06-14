package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UpdateQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        UpdateQuery.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        UpdateQuery.builder()
                .table("")
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(updateQuery.where());
        assertEquals("", updateQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(updateQuery.whereArgs());
        assertTrue(updateQuery.whereArgs().isEmpty());
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(table, updateQuery.table());
        assertEquals(where, updateQuery.where());
        assertEquals(asList(whereArgs), updateQuery.whereArgs());
    }
}
