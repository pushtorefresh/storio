package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class InsertQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        InsertQuery.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        InsertQuery.builder()
                .table("")
                .build();
    }

    @Test
    public void nullColumnHackShouldBeNullByDefault() {
        InsertQuery insertQuery = InsertQuery.builder()
                .table("test_table")
                .build();

        assertThat(insertQuery.nullColumnHack()).isNull();
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
