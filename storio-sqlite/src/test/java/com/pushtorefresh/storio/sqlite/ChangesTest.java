package com.pushtorefresh.storio.sqlite;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangesTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedTables() {
        Changes.newInstance((Set<String>) null); // Lol, specifying overload of newInstance
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedTable() {
        Changes.newInstance((String) null);
    }

    @Test
    public void emptyAffectedTableAllowed() {
        Changes.newInstance("");
    }

    @Test
    public void newInstanceOneAffectedTable() {
        final Changes changes = Changes.newInstance("test_table");
        assertThat(changes.affectedTables()).hasSize(1);
        assertThat(changes.affectedTables()).contains("test_table");
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
