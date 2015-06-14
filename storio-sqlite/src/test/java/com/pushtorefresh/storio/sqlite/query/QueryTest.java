package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class QueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        Query.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        Query.builder()
                .table("")
                .build();
    }

    @Test
    public void distinctShouldBeFalseByDefault() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertFalse(query.distinct());
    }

    @Test
    public void columnsShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.columns());
        assertTrue(query.columns().isEmpty());
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.where());
        assertEquals("", query.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.whereArgs());
        assertTrue(query.whereArgs().isEmpty());
    }

    @Test(expected = IllegalStateException.class)
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        Query.builder()
                .table("test_table")
                .whereArgs("someArg") // Without WHERE clause!
                .build();
    }

    @Test
    public void groupByShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.groupBy());
        assertEquals("", query.groupBy());
    }

    @Test
    public void havingShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.having());
        assertEquals("", query.having());
    }

    @Test
    public void orderByShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.orderBy());
        assertEquals("", query.orderBy());
    }

    @Test
    public void limitShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertNotNull(query.limit());
        assertEquals("", query.limit());
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final boolean distinct = true;
        final String[] columns = {"column1", "column2", "column3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String groupBy = "test_group_by";
        final String having = "test_having";
        final String orderBy = "test_order_by";
        final String limit = "test_limit";

        final Query query = Query.builder()
                .table(table)
                .distinct(distinct)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .groupBy(groupBy)
                .having(having)
                .orderBy(orderBy)
                .limit(limit)
                .build();

        assertEquals(table, query.table());
        assertEquals(distinct, query.distinct());
        assertEquals(Arrays.asList(columns), query.columns());
        assertEquals(where, query.where());
        assertEquals(Arrays.asList(whereArgs), query.whereArgs());
        assertEquals(groupBy, query.groupBy());
        assertEquals(having, query.having());
        assertEquals(orderBy, query.orderBy());
        assertEquals(limit, query.limit());
    }
}
