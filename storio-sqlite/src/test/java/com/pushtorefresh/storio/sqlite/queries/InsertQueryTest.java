package com.pushtorefresh.storio.sqlite.queries;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class InsertQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        InsertQuery.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        InsertQuery.builder()
                .table("")
                .build();
    }

    @Test
    public void nullColumnHackShouldBeNullByDefault() {
        InsertQuery insertQuery = InsertQuery.builder()
                .table("test_table")
                .build();

        assertNull(insertQuery.nullColumnHack());
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";

        final InsertQuery insertQuery = InsertQuery.builder()
                .table(table)
                .nullColumnHack(nullColumnHack)
                .build();

        assertEquals(table, insertQuery.table());
        assertEquals(nullColumnHack, insertQuery.nullColumnHack());
    }
}
