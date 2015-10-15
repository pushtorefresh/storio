package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class InsertQueryTest {

    @Test
    public void shouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            InsertQuery.builder()
                    .table(null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void shouldNotAllowEmptyTable() {
        try {
            InsertQuery.builder()
                    .table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void nullColumnHackShouldBeNullByDefault() {
        InsertQuery insertQuery = InsertQuery.builder()
                .table("test_table")
                .build();

        assertThat(insertQuery.nullColumnHack()).isNull();
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            InsertQuery.builder()
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
            InsertQuery.builder()
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
        InsertQuery query = InsertQuery.builder()
                .table("old_table")
                .table("new_table")
                .build();

        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";

        final InsertQuery firstQuery = InsertQuery.builder()
                .table(table)
                .nullColumnHack(nullColumnHack)
                .build();

        final InsertQuery secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String nullColumnHack = "test_null_column_hack";

        final InsertQuery insertQuery = InsertQuery.builder()
                .table(table)
                .nullColumnHack(nullColumnHack)
                .build();

        assertThat(insertQuery.table()).isEqualTo(table);
        assertThat(insertQuery.nullColumnHack()).isEqualTo(nullColumnHack);
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(InsertQuery.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(InsertQuery.class)
                .check();
    }
}
