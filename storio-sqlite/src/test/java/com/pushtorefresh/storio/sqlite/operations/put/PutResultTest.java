package com.pushtorefresh.storio.sqlite.operations.put;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

public class PutResultTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private void checkCreateInsertResult(
            long insertedId,
            @NonNull Set<String> affectedTables,
            @Nullable Set<?> affectedTags,
            @NonNull PutResult insertResult
    ) {
        assertThat(insertResult.wasInserted()).isTrue();
        assertThat(insertResult.wasUpdated()).isFalse();

        assertThat(insertResult.wasNotInserted()).isFalse();
        assertThat(insertResult.wasNotUpdated()).isTrue();

        //noinspection ConstantConditions
        assertThat((long) insertResult.insertedId()).isEqualTo(insertedId);
        assertThat(insertResult.affectedTables()).isEqualTo(affectedTables);
        assertThat(insertResult.affectedTags()).isEqualTo(affectedTags);

        assertThat(insertResult.numberOfRowsUpdated()).isNull();
    }

    @Test
    public void createInsertResultWithSeveralAffectedTables() {
        final int insertedId = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));

        checkCreateInsertResult(
                insertedId,
                affectedTables,
                Collections.<String>emptySet(),
                PutResult.newInsertResult(insertedId, affectedTables)
        );
    }

    @Test
    public void createInsertResultWithSeveralAffectedTags() {
        final int insertedId = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));
        final Set<String> affectedTags = new HashSet<String>(asList("tag1", "tag2", "tag3"));

        checkCreateInsertResult(
                insertedId,
                affectedTables,
                affectedTags,
                PutResult.newInsertResult(insertedId, affectedTables, affectedTags)
        );
    }

    @Test
    public void createInsertResultWithOneAffectedTables() {
        final int insertedId = 10;
        final String affectedTable = "table";

        checkCreateInsertResult(
                insertedId,
                singleton(affectedTable),
                Collections.<String>emptySet(),
                PutResult.newInsertResult(insertedId, affectedTable)
        );
    }

    @Test
    public void createInsertResultWithOneAffectedTag() {
        final int insertedId = 10;
        final String affectedTable = "table";
        final String affectedTag = "tag";

        checkCreateInsertResult(
                insertedId,
                singleton(affectedTable),
                singleton(affectedTag),
                PutResult.newInsertResult(insertedId, affectedTable, affectedTag)
        );
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("affectedTables must not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newInsertResult(1, (Set<String>) null);
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTables() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("affectedTables must contain at least one element"));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newInsertResult(1, new HashSet<String>());
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify affected table"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newInsertResult(1, (String) null);
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTable must not be null or empty, affectedTables = "));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newInsertResult(1, "");
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newInsertResult(1, "table", (String) null);
    }

    @Test
    public void shouldNotCreateInsertResultWithEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newInsertResult(1, "table", "");
    }

    private void checkCreateUpdateResult(
            int numberOfRowsUpdated,
            @NonNull Set<String> affectedTables,
            @Nullable Set<String> affectedTags,
            @NonNull PutResult updateResult
    ) {
        assertThat(updateResult.wasUpdated()).isTrue();
        assertThat(updateResult.wasInserted()).isFalse();

        assertThat(updateResult.wasNotUpdated()).isFalse();
        assertThat(updateResult.wasNotInserted()).isTrue();

        //noinspection ConstantConditions
        assertThat((int) updateResult.numberOfRowsUpdated()).isEqualTo(numberOfRowsUpdated);
        assertThat(updateResult.affectedTables()).isEqualTo(affectedTables);
        assertThat(updateResult.affectedTags()).isEqualTo(affectedTags);

        assertThat(updateResult.insertedId()).isNull();
    }

    @Test
    public void createUpdateResultWithSeveralAffectedTables() {
        final int numberOfRowsUpdated = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                affectedTables,
                Collections.<String>emptySet(),
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTables)
        );
    }


    @Test
    public void createUpdateResultWithSeveralAffectedTags() {
        final int numberOfRowsUpdated = 10;
        final Set<String> affectedTables = new HashSet<String>(asList("table1", "table2", "table3"));
        final Set<String> affectedTags = new HashSet<String>(asList("tag1", "tag2", "tag3"));

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                affectedTables,
                affectedTags,
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTables, affectedTags)
        );
    }

    @Test
    public void createUpdateResultWithOneAffectedTable() {
        final int numberOfRowsUpdated = 10;
        final String affectedTable = "table";

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                singleton(affectedTable),
                Collections.<String>emptySet(),
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTable)
        );
    }

    @Test
    public void createUpdateResultWithOneAffectedTag() {
        final int numberOfRowsUpdated = 10;
        final String affectedTable = "table";
        final String affectedTag = "tag";

        checkCreateUpdateResult(
                numberOfRowsUpdated,
                singleton(affectedTable),
                singleton(affectedTag),
                PutResult.newUpdateResult(numberOfRowsUpdated, affectedTable, affectedTag)
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
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("Number of rows updated must be >= 0, but was: -1"));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newUpdateResult(-1, "table");
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("affectedTables must not be null"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newUpdateResult(1, (Set<String>) null);
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTables() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalTo("affectedTables must contain at least one element"));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newUpdateResult(1, new HashSet<String>());
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify affected table"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newUpdateResult(1, (String) null);
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTable must not be null or empty, affectedTables = "));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newUpdateResult(1, "");
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        PutResult.newUpdateResult(1, "tag", (String) null);
    }

    @Test
    public void shouldNotCreateUpdateResultWithEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        PutResult.newUpdateResult(1, "table", "");
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
