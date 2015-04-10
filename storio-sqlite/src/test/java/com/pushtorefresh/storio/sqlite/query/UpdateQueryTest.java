package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class UpdateQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullTable() {
        new UpdateQuery.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyTable() {
        new UpdateQuery.Builder()
                .table("")
                .build();
    }

    @Test
    public void build() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = new UpdateQuery.Builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(table, updateQuery.table);
        assertEquals(where, updateQuery.where);
        assertEquals(Arrays.asList(whereArgs), updateQuery.whereArgs);
    }
}
