package com.pushtorefresh.storio.sqlite.queries;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UpdateQueryTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullTable() {
        UpdateQuery.builder()
                .table(null)
                .build();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldNotAllowEmptyTable() {
        UpdateQuery.builder()
                .table("")
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(updateQuery.where());
        assertEquals("", updateQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .table("test_table")
                .build();

        assertNotNull(updateQuery.whereArgs());
        assertTrue(updateQuery.whereArgs().isEmpty());
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

        assertEquals(table, updateQuery.table());
        assertEquals(where, updateQuery.where());
        assertEquals(asList(whereArgs), updateQuery.whereArgs());
    }

    @Test
    public void shouldThrowExceptionIfWhereArgsSpecifiedWithoutWhereClause() {
        try {
            UpdateQuery.builder()
                    .table("test_table")
                    .whereArgs("someArg") // Without WHERE clause!
                    .build();
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("You can not use whereArgs without where clause", expected.getMessage());
        }
    }

    @Test
    public void shouldAllowNullWhereArgsWithoutWhereClause() {
        //noinspection NullArgumentToVariableArgMethod
        UpdateQuery.builder()
                .table("test_table")
                .whereArgs(null)
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
