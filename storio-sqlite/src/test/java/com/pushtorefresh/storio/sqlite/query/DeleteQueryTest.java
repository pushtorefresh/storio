package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DeleteQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        new DeleteQuery.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        new DeleteQuery.Builder()
                .table("")
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        DeleteQuery deleteQuery = new DeleteQuery.Builder()
                .table("test_table")
                .build();

        assertNotNull(deleteQuery.where());
        assertEquals("", deleteQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        DeleteQuery deleteQuery = new DeleteQuery.Builder()
                .table("test_table")
                .build();

        assertNotNull(deleteQuery.whereArgs());
        assertTrue(deleteQuery.whereArgs().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        new DeleteQuery.Builder()
                .table("test_table")
                .whereArgs("someArg") // Without WHERE clause!
                .build();
    }

    @Test
    public void buildWithNormalValues() {
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
