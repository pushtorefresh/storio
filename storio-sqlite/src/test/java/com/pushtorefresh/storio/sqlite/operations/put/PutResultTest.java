package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class PutResultTest {

    void checkCreateInsertResult(long insertedId, @NonNull Set<String> affectedTables, @NonNull PutResult insertResult) {
        assertTrue(insertResult.wasInserted());
        assertFalse(insertResult.wasUpdated());

        assertFalse(insertResult.wasNotInserted());
        assertTrue(insertResult.wasNotUpdated());

        //noinspection ConstantConditions
        assertEquals(insertedId, (long) insertResult.insertedId());
        assertEquals(affectedTables, insertResult.affectedTables());

        assertNull(insertResult.numberOfRowsUpdated());
    }

    @Test
    public void createInsertResultWithSeveralAffectedTables() {
        final int insertedId = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));

        checkCreateInsertResult(
                insertedId,
                affectedTables,
                PutResult.newInsertResult(insertedId, affectedTables)
        );
    }

    @Test
    public void createInsertResultWithOneAffectedTables() {
        final int insertedId = 10;
        final String affectedTable = "table";

        checkCreateInsertResult(
                insertedId,
                singleton(affectedTable),
                PutResult.newInsertResult(insertedId, affectedTable)
        );
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTables() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(1, (Set<String>) null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("affectedTables must not be null", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTables() {
        try {
            PutResult.newInsertResult(1, new HashSet<String>());
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("affectedTables must contain at least one element", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTable() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(1, (String) null);
            fail();
        } catch (NullPointerException expected) {
            assertTrue(expected.getMessage().contains("affectedTable must not be null or empty, affectedTables = "));
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTable() {
        try {
            PutResult.newInsertResult(1, "");
            fail();
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("affectedTable must not be null or empty, affectedTables = "));
        }
    }

    void checkCreateUpdateResult(int numberOfRowsUpdated, @NonNull Set<String> affectedTables, @NonNull PutResult updateResult) {
        assertTrue(updateResult.wasUpdated());
        assertFalse(updateResult.wasInserted());

        assertFalse(updateResult.wasNotUpdated());
        assertTrue(updateResult.wasNotInserted());

        //noinspection ConstantConditions
        assertEquals(numberOfRowsUpdated, (int) updateResult.numberOfRowsUpdated());
        assertEquals(affectedTables, updateResult.affectedTables());

        assertNull(updateResult.insertedId());
    }

    @Test
    public void createUpdateResultWithSeveralAffectedTables() {
        final int numberOfRowsUpdated = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                affectedTables,
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTables)
        );
    }

    @Test
    public void createUpdateResultWithOneAffectedTable() {
        final int numberOfRowsUpdated = 10;
        final String affectedTable = "table";

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                singleton(affectedTable),
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTable)
        );
    }

    @Test
    public void shouldAllowCreatingUpdateResultWith0RowsUpdated() {
        PutResult putResult = PutResult.newUpdateResult(0, "table");
        assertTrue(putResult.wasUpdated());
        assertFalse(putResult.wasInserted());
        assertEquals(Integer.valueOf(0), putResult.numberOfRowsUpdated());
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult(-1, "table");
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("Number of rows updated must be >= 0", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTables() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, (Set<String>) null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("affectedTables must not be null", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTables() {
        try {
            PutResult.newUpdateResult(1, new HashSet<String>());
            fail();
        } catch (IllegalArgumentException expected) {
            assertEquals("affectedTables must contain at least one element", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTable() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, (String) null);
            fail();
        } catch (NullPointerException expected) {
            assertTrue(expected.getMessage().contains("affectedTable must not be null or empty, affectedTables = "));
        }
    }


    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTable() {
        try {
            PutResult.newUpdateResult(1, "");
            fail();
        } catch (IllegalStateException expected) {
            assertTrue(expected.getMessage().contains("affectedTable must not be null or empty, affectedTables = "));
        }
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(PutResult.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(PutResult.class)
                .check();
    }
}
