package com.pushtorefresh.storio.sqlite.query;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class QueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullTable() {
        new Query.Builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void emptyTable() {
        new Query.Builder()
                .table("")
                .build();
    }

    @Test
    public void build() {
        final String table = "test_table";
        final boolean distinct = true;
        final String[] columns = {"column1", "column2", "column3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String groupBy = "test_group_by";
        final String having = "test_having";
        final String orderBy = "test_order_by";
        final String limit = "test_limit";

        final Query query = new Query.Builder()
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

        assertEquals(table, query.table);
        assertEquals(distinct, query.distinct);
        assertEquals(Arrays.asList(columns), query.columns);
        assertEquals(where, query.where);
        assertEquals(Arrays.asList(whereArgs), query.whereArgs);
        assertEquals(groupBy, query.groupBy);
        assertEquals(having, query.having);
        assertEquals(orderBy, query.orderBy);
        assertEquals(limit, query.limit);
    }
}
