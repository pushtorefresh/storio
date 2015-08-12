package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.Arrays;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class QueryTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        //noinspection ConstantConditions
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

        assertThat(query.distinct()).isFalse();
    }

    @Test
    public void columnsShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.columns()).isNotNull();
        assertThat(query.columns()).isEmpty();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.whereArgs()).isEmpty();
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

        assertThat(query.groupBy()).isEqualTo("");
    }

    @Test
    public void havingShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.having()).isEqualTo("");
    }

    @Test
    public void orderByShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.orderBy()).isEqualTo("");
    }

    @Test
    public void limitShouldNotBeNull() {
        Query query = Query.builder()
                .table("test_table")
                .build();

        assertThat(query.limit()).isEqualTo("");
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

        assertThat(query.table()).isEqualTo(table);
        assertThat(query.distinct()).isEqualTo(distinct);
        assertThat(query.columns()).isEqualTo(Arrays.asList(columns));
        assertThat(query.where()).isEqualTo(where);
        assertThat(query.whereArgs()).isEqualTo(Arrays.asList(whereArgs));
        assertThat(query.groupBy()).isEqualTo(groupBy);
        assertThat(query.having()).isEqualTo(having);
        assertThat(query.orderBy()).isEqualTo(orderBy);
        assertThat(query.limit()).isEqualTo(limit);
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Query.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(Query.class)
                .check();
    }
}
