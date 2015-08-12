package com.pushtorefresh.storio.sqlite.operations.delete;

import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import nl.jqno.equalsverifier.EqualsVerifier;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteResultTest {

    @Test(expected = NullPointerException.class)
    public void nullAffectedTable() {
        //noinspection ConstantConditions
        DeleteResult.newInstance(0, (String) null);
    }

    @Test(expected = NullPointerException.class)
    public void nullAffectedTables() {
        //noinspection ConstantConditions
        DeleteResult.newInstance(0, (Set<String>) null);
    }

    @Test
    public void numberOfRowsDeleted() {
        final DeleteResult deleteResult = DeleteResult.newInstance(3, "test_table");
        assertThat(deleteResult.numberOfRowsDeleted()).isEqualTo(3);
    }

    @Test
    public void oneAffectedTable() {
        final DeleteResult deleteResult = DeleteResult.newInstance(2, "test_table");
        assertThat(deleteResult.affectedTables()).isEqualTo(Collections.singleton("test_table"));
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
