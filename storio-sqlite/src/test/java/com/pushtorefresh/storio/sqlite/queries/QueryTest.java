package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

public class QueryTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldNotAllowNullTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Table name is null or empty"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Query.builder().table(null);
    }

    @Test
    public void shouldNotAllowEmptyTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(equalTo("Table name is null or empty"));
        expectedException.expectCause(nullValue(Throwable.class));

        Query.builder().table("");
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

    @Test
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        try {
            Query.builder()
                    .table("test_table")
                    .whereArgs("someArg")
                    .build(); // Without WHERE clause!
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasNoCause()
                    .hasMessage("You can not use whereArgs without where clause");
        }
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
    public void integerLimitShouldBePositive() {
        try {
            Query query = Query.builder()
                    .table("test_table")
                    .limit(-1)
                    .build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasNoCause()
                    .hasMessage("Parameter `limit` should be positive, but was = -1");
        }
    }

    @Test
    public void limitOffsetShouldNotBeNegative() {
        try {
            Query query = Query.builder()
                    .table("test_table")
                    .limit(-1, 10)
                    .build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasNoCause()
                    .hasMessage("Parameter `offset` should not be negative, but was = -1");
        }
    }

    @Test
    public void limitQuantityShouldBePositive() {
        try {
            Query query = Query.builder()
                    .table("test_table")
                    .limit(0, -1)
                    .build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasNoCause()
                    .hasMessage("Parameter `quantity` should be positive, but was = -1");
        }
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            Query.builder()
                    .table("test_table")
                    .table(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldNotAllowEmptyTable() {
        try {
            Query.builder()
                    .table("test_table")
                    .table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void completeBuilderShouldUpdateTable() {
        Query query = Query.builder()
                .table("old_table")
                .table("new_table")
                .build();

        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final boolean distinct = true;
        final String[] columns = {"column1", "column2", "column3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String groupBy = "test_group_by";
        final String having = "test_having";
        final String orderBy = "test_order_by";
        final String limit = "test_limit";
        final String tag = "test_tag";

        final Query firstQuery = Query.builder()
                .table(table)
                .distinct(distinct)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .groupBy(groupBy)
                .having(having)
                .orderBy(orderBy)
                .limit(limit)
                .observesTags(tag)
                .build();

        final Query secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = {"arg1", "arg2", "arg3"};

        final Query query = Query.builder()
                .table("test_table")
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(query.whereArgs()).isEqualTo(asList(whereArgs));
    }

    @Test
    public void shouldTakeListOfStringsAsWhereArgs() {
        final List<String> whereArgs = Arrays.asList("arg1", "arg2", "arg3");

        final Query query = Query.builder()
                .table("test_table")
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(query.whereArgs()).isEqualTo(whereArgs);
    }

    @Test
    public void observesTagsCollectionShouldRewrite() {
        Query query = Query.builder()
                .table("table")
                .observesTags(new HashSet<String>((singletonList("first_call_collection"))))
                .observesTags(new HashSet<String>((singletonList("second_call_collection"))))
                .build();

        assertThat(query.observesTags()).isEqualTo(singleton("second_call_collection"));
    }

    @Test
    public void observesTagsVarargShouldRewrite() {
        Query query = Query.builder()
                .table("table")
                .observesTags("first_call_vararg")
                .observesTags("second_call_vararg")
                .build();

        assertThat(query.observesTags()).isEqualTo(singleton("second_call_vararg"));
    }

    @Test
    public void observesTagsCollectionAllowsNull() {
        Query query = Query.builder()
                .table("table")
                .observesTags(new HashSet<String>((singletonList("first_call_collection"))))
                .observesTags(null)
                .build();

        assertThat(query.observesTags()).isEmpty();
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
        final Set<String> tags = singleton("test_tag");

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
                .observesTags(tags)
                .build();

        assertThat(query.table()).isEqualTo(table);
        assertThat(query.distinct()).isEqualTo(distinct);
        assertThat(query.columns()).isEqualTo(asList(columns));
        assertThat(query.where()).isEqualTo(where);
        assertThat(query.whereArgs()).isEqualTo(asList(whereArgs));
        assertThat(query.groupBy()).isEqualTo(groupBy);
        assertThat(query.having()).isEqualTo(having);
        assertThat(query.orderBy()).isEqualTo(orderBy);
        assertThat(query.limit()).isEqualTo(limit);
        assertThat(query.observesTags()).isEqualTo(tags);
    }

    @Test
    public void integerLimitWithNormalValue() {
        Query query = Query.builder()
                .table("test_table")
                .limit(10)
                .build();

        assertThat(query.limit()).isEqualTo("10");
    }

    @Test
    public void limitOffsetQuantityWithNormalValue() {
        Query query = Query.builder()
                .table("test_table")
                .limit(10, 20)
                .build();

        assertThat(query.limit()).isEqualTo("10, 20");
    }

    @Test
    public void shouldNotAllowNullTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("observesTag must not be null or empty, observesTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Query.builder()
                .table("table")
                .observesTags((String) null)
                .build();
    }

    @Test
    public void shouldNotAllowEmptyTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("observesTag must not be null or empty, observesTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Query.builder()
                .table("table")
                .observesTags("")
                .build();
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
