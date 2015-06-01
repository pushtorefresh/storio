package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InsertQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        new InsertQuery.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        new InsertQuery.Builder()
                .table("")
                .build();
    }

    @Test
    public void nullColumnHackShouldBeNullByDefault() {
        InsertQuery insertQuery = new InsertQuery.Builder()
                .table("test_table")
                .build();

        assertNull(insertQuery.nullColumnHack());
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";

        final InsertQuery insertQuery = new InsertQuery.Builder()
                .table(table)
                .nullColumnHack(nullColumnHack)
                .build();

        assertEquals(table, insertQuery.table());
        assertEquals(nullColumnHack, insertQuery.nullColumnHack());
    }
}
