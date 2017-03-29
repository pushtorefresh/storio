package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;

public class ChangesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullAffectedTablesFails() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify affected tables");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Changes.newInstance((Set<String>) null); // Lol, specifying overload of newInstance
    }

    @Test
    public void nullAffectedTableFails() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify affected table");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Changes.newInstance((String) null);
    }

    @Test
    public void emptyAffectedTableAllowed() {
        Changes.newInstance("");
    }

    @Test
    public void nullAffectedTagsCollectionAllowed() {
        Changes changes = Changes.newInstance(Collections.<String>emptySet(), (Collection<String>) null);
        assertThat(changes.affectedTags()).isEmpty();
    }

    @Test
    public void nullAffectedTagsVarArgAllowed() {
        Changes changes = Changes.newInstance("table");
        assertThat(changes.affectedTags()).isEmpty();
    }

    @Test
    public void newInstance_oneTableWithoutTag() {
        final Changes changes = Changes.newInstance("test_table");
        assertThat(changes.affectedTables()).containsExactly("test_table");
        assertThat(changes.affectedTags()).isEmpty();
    }

    @Test
    public void newInstance_oneTableOneTag() {
        final Changes changes = Changes.newInstance("table", "test_tag");
        assertThat(changes.affectedTables()).containsExactly("table");
        assertThat(changes.affectedTags()).containsExactly("test_tag");
    }

    @Test
    public void newInstance_oneTableTagsCollection() {
        final Changes changes = Changes.newInstance("table", singletonList("test_tag"));
        assertThat(changes.affectedTables()).containsExactly("table");
        assertThat(changes.affectedTags()).containsExactly("test_tag");
    }

    @Test
    public void newInstance_multipleTablesWithoutTag() {
        final Set<String> affectedTables = new HashSet<String>();
        affectedTables.add("test_table_1");
        affectedTables.add("test_table_2");
        affectedTables.add("test_table_3");

        final Changes changes = Changes.newInstance(affectedTables);
        assertThat(changes.affectedTables()).isEqualTo(affectedTables);
        assertThat(changes.affectedTags()).isEmpty();
    }

    @Test
    public void newInstance_multipleTablesOneTag() {
        final Changes changes = Changes.newInstance(singleton("test_table_1"), "test_tag_1");
        assertThat(changes.affectedTables()).containsExactly("test_table_1");
        assertThat(changes.affectedTags()).containsExactly("test_tag_1");
    }

    @Test
    public void newInstance_multipleTablesTagsCollection() {
        final Set<String> affectedTags = new HashSet<String>();
        affectedTags.add("test_tag_1");
        affectedTags.add("test_tag_2");
        affectedTags.add("test_tag_3");

        final Changes changes = Changes.newInstance(singleton("test_table_1"), affectedTags);
        assertThat(changes.affectedTables()).containsExactly("test_table_1");
        assertThat(changes.affectedTags()).isEqualTo(affectedTags);
    }

    @Test
    public void shouldNotAllowNullAffectedTag() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        Changes.newInstance("table", (String) null);
    }

    @Test
    public void shouldNotAllowEmptyAffectedTag() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage(startsWith("affectedTag must not be null or empty, affectedTags = "));
        expectedException.expectCause(nullValue(Throwable.class));

        Changes.newInstance("table", "");
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Changes.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(Changes.class)
                .check();
    }
}
