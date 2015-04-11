package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InsertQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullTable() {
        new InsertQuery.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyTable() {
        new InsertQuery.Builder()
                .table("")
                .build();
    }

    @Test
    public void build() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";

        final InsertQuery insertQuery = new InsertQuery.Builder()
                .table(table)
                .nullColumnHack(nullColumnHack)
                .build();

        assertEquals(table, insertQuery.table);
        assertEquals(nullColumnHack, insertQuery.nullColumnHack);
    }
}
