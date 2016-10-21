package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class UpdateQueryTest {

    @Test
    public void shouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            UpdateQuery.builder()
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
            UpdateQuery.builder()
                    .table("");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected)
                    .hasMessage("Table name is null or empty")
                    .hasNoCause();
        }
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertThat(updateQuery.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertThat(updateQuery.whereArgs()).isNotNull();
        assertThat(updateQuery.whereArgs()).isEmpty();
    }

    @Test
    public void completeBuilderShouldNotAllowNullTable() {
        try {
            //noinspection ConstantConditions
            UpdateQuery.builder()
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
            UpdateQuery.builder()
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
        UpdateQuery query = UpdateQuery.builder()
                .table("old_table")
                .table("new_table")
                .build();

        assertThat(query.table()).isEqualTo("new_table");
    }

    @Test
    public void createdThroughToBuilderQueryShouldBeEqual() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery firstQuery = UpdateQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        final UpdateQuery secondQuery = firstQuery.toBuilder().build();

        assertThat(secondQuery).isEqualTo(firstQuery);
    }

    @Test
    public void shouldTakeStringArrayAsWhereArgs() {
        final String[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .where("test_where")
                .whereArgs(whereArgs)
                .build();

        assertThat(updateQuery.whereArgs()).isEqualTo(asList(whereArgs));
    }

    @Test
    public void buildWithNormalValues() {
        final String table = "test_table";
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .table(table)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertThat(updateQuery.table()).isEqualTo(table);
        assertThat(updateQuery.where()).isEqualTo(where);
        assertThat(updateQuery.whereArgs()).isEqualTo(asList(whereArgs));
    }

    @Test
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        try {
            UpdateQuery.builder()
                    .table("test_table")
                    .whereArgs("someArg") // Without WHERE clause!
                    .build();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessage("You can not use whereArgs without where clause");
        }
    }

    @Test
    public void shouldAllowNullWhereArgsWithoutWhereClause() {
        //noinspection NullArgumentToVariableArgMethod
        UpdateQuery.builder()
                .table("test_table")
                .whereArgs((Object[]) null)
                .build();

        // We don't expect any exceptions here
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(UpdateQuery.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(UpdateQuery.class)
                .check();
    }
}
