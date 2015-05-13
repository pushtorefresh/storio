package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class DeleteQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullTable() {
        new DeleteQuery.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyTable() {
        new DeleteQuery.Builder()
                .table("")
                .build();
    }

    @Test
    public void build() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery deleteQuery = new DeleteQuery.Builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(table, deleteQuery.table());
        assertEquals(where, deleteQuery.where());
        assertEquals(Arrays.asList(whereArgs), deleteQuery.whereArgs());
    }
}
