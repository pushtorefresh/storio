package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;

public class PutResultTest {

    void checkCreateInsertResult(long insertedId, @NonNull Set<String> affectedTables, @NonNull PutResult insertResult) {
        assertThat(insertResult.wasInserted()).isTrue();
        assertThat(insertResult.wasUpdated()).isFalse();

        assertThat(insertResult.wasNotInserted()).isFalse();
        assertThat(insertResult.wasNotUpdated()).isTrue();

        //noinspection ConstantConditions
        assertThat((long) insertResult.insertedId()).isEqualTo(insertedId);
        assertThat(insertResult.affectedTables()).isEqualTo(affectedTables);

        assertThat(insertResult.numberOfRowsUpdated()).isNull();
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
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedTables must not be null");
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTables() {
        try {
            PutResult.newInsertResult(1, new HashSet<String>());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("affectedTables must contain at least one element");
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTable() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(1, (String) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessageStartingWith("affectedTable must not be null or empty, affectedTables = ");
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTable() {
        try {
            PutResult.newInsertResult(1, "");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessageStartingWith("affectedTable must not be null or empty, affectedTables = ");
        }
    }

    void checkCreateUpdateResult(int numberOfRowsUpdated, @NonNull Set<String> affectedTables, @NonNull PutResult updateResult) {
        assertThat(updateResult.wasUpdated()).isTrue();
        assertThat(updateResult.wasInserted()).isFalse();

        assertThat(updateResult.wasNotUpdated()).isFalse();
        assertThat(updateResult.wasNotInserted()).isTrue();

        //noinspection ConstantConditions
        assertThat((int) updateResult.numberOfRowsUpdated()).isEqualTo(numberOfRowsUpdated);
        assertThat(updateResult.affectedTables()).isEqualTo(affectedTables);

        assertThat(updateResult.insertedId()).isNull();
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
        assertThat(putResult.wasUpdated()).isFalse();
        assertThat(putResult.wasInserted()).isFalse();
        assertThat(putResult.numberOfRowsUpdated()).isEqualTo(Integer.valueOf(0));
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult(-1, "table");
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("Number of rows updated must be >= 0");
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTables() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, (Set<String>) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessage("affectedTables must not be null");
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTables() {
        try {
            PutResult.newUpdateResult(1, new HashSet<String>());
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException expected) {
            assertThat(expected).hasMessage("affectedTables must contain at least one element");
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTable() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, (String) null);
            failBecauseExceptionWasNotThrown(NullPointerException.class);
        } catch (NullPointerException expected) {
            assertThat(expected).hasMessageStartingWith("affectedTable must not be null or empty, affectedTables = ");
        }
    }


    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTable() {
        try {
            PutResult.newUpdateResult(1, "");
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (IllegalStateException expected) {
            assertThat(expected).hasMessageStartingWith("affectedTable must not be null or empty, affectedTables = ");
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
