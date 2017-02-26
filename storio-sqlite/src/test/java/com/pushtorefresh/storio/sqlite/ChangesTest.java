package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.nullValue;

public class ChangesTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void nullAffectedTables() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify affected tables");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Changes.newInstance((Set<String>) null); // Lol, specifying overload of newInstance
    }

    @Test
    public void nullAffectedTags() {
        expectedException.expect(NullPointerException.class);
        expectedException.expectMessage("Please specify affected tags");
        expectedException.expectCause(nullValue(Throwable.class));

        //noinspection ConstantConditions
        Changes.newInstance(Collections.<String>emptySet(), null);
    }

    @Test
    public void nullAffectedTable() {
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
    public void nullAffectedTagAllowed() {
        Changes.newInstance("table", null);
    }

    @Test
    public void emptyAffectedTagAllowed() {
        Changes.newInstance("table", "");
    }

    @Test
    public void newInstanceOneAffectedTable() {
        final Changes changes = Changes.newInstance("test_table");
        assertThat(changes.affectedTables()).containsExactly("test_table");
    }

    @Test
    public void newInstanceOneAffectedTag() {
        final Changes changes = Changes.newInstance("table", "test_tag");
        assertThat(changes.affectedTags()).containsExactly("test_tag");
    }

    @Test
    public void newInstanceMultipleAffectedTables() {
        final Set<String> affectedTables = new HashSet<String>();
        affectedTables.add("test_table_1");
        affectedTables.add("test_table_2");
        affectedTables.add("test_table_3");

        final Changes changes = Changes.newInstance(affectedTables);
        assertThat(changes.affectedTables()).isEqualTo(affectedTables);
    }

    @Test
    public void newInstanceMultipleAffectedTags() {
        final Set<String> affectedTags = new HashSet<String>();
        affectedTags.add("test_tag_1");
        affectedTags.add("test_tag_2");
        affectedTags.add("test_tag_3");

        final Changes changes = Changes.newInstance(Collections.<String>emptySet(), affectedTags);
        assertThat(changes.affectedTags()).isEqualTo(affectedTags);
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
