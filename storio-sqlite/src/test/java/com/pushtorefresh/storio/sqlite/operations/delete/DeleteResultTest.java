package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Collections.singleton;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

public class DeleteResultTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify affected tables"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        DeleteResult.newInstance(0, (Set<String>) null);
    }

    @Test
    public void nullAffectedTable() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(equalTo("Please specify affected table"));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        DeleteResult.newInstance(0, (String) null);
    }

    @Test
    public void emptyAffectedTable() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTable must not be null or empty, affectedTables = "));
        expectedException.expectCause(nullValue(Throwable.class));

        DeleteResult.newInstance(0, "");
    }

    @Test
    public void nullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        DeleteResult.newInstance(0, "table", (String) null);
    }

    @Test
    public void emptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        DeleteResult.newInstance(0, "table", "");
    }

    @Test
    public void numberOfRowsDeleted() {
        final DeleteResult deleteResult = DeleteResult.newInstance(3, "test_table");
        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(3);
    }

    @Test
    public void oneAffectedTable() {
        final DeleteResult deleteResult = DeleteResult.newInstance(2, "test_table");
        assertThat(deleteResult.affectedTables()).isEqualTo(singleton("test_table"));
    }

    @Test
    public void multipleAffectedTables() {
        final Set<String> affectedTables = new HashSet<String>();
        affectedTables.add("table1");
        affectedTables.add("table2");

        final DeleteResult deleteResult = DeleteResult.newInstance(2, affectedTables);

        assertThat(deleteResult.affectedTables()).isEqualTo(affectedTables);
    }

    @Test
    public void affectedTagVarArg() {
        final DeleteResult deleteResult = DeleteResult.newInstance(2, "test_table", "test_tag");
        assertThat(deleteResult.affectedTags()).isEqualTo(singleton("test_tag"));
    }

    @Test
    public void affectedTagsCollection() {
        final Set<String> affectedTags = new HashSet<String>();
        affectedTags.add("tag1");
        affectedTags.add("tag2");

        final DeleteResult deleteResult = DeleteResult.newInstance(2, singleton("table1"), affectedTags);

        assertThat(deleteResult.affectedTags()).isEqualTo(affectedTags);
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(DeleteResult.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(DeleteResult.class)
                .check();
    }
}
