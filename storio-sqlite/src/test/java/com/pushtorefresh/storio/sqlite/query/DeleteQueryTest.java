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
        DeleteQuery.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        DeleteQuery.builder()
                .table("")
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(deleteQuery.where());
        assertEquals("", deleteQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        DeleteQuery deleteQuery = DeleteQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(deleteQuery.whereArgs());
        assertTrue(deleteQuery.whereArgs().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        DeleteQuery.builder()
                .table("test_table")
                .whereArgs("someArg") // Without WHERE clause!
                .build();
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final DeleteQuery deleteQuery = DeleteQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(table, deleteQuery.table());
        assertEquals(where, deleteQuery.where());
        assertEquals(Arrays.asList(whereArgs), deleteQuery.whereArgs());
    }
}
