package com.pushtorefresh.storio.sqlite.operations.delete;

import org.junit.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class DeleteResultTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedTable() {
        DeleteResult.newInstance(0, (String) null);
    }

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullAffectedTables() {
        DeleteResult.newInstance(0, (Set<String>) null);
    }

    @Test
    public void numberOfRowsDeleted() {
        final DeleteResult deleteResult = DeleteResult.newInstance(3, "test_table");
        assertEquals(3, deleteResult.numberOfRowsDeleted());
    }

    @Test
    public void oneAffectedTable() {
        final DeleteResult deleteResult = DeleteResult.newInstance(2, "test_table");
        assertEquals(Collections.singleton("test_table"), deleteResult.affectedTables());
    }

    @Test
    public void multipleAffectedTables() {
        final Set<String> affectedTables = new HashSet<String>();
        affectedTables.add("table1");
        affectedTables.add("table2");

        final DeleteResult deleteResult = DeleteResult.newInstance(2, affectedTables);

        assertEquals(affectedTables, deleteResult.affectedTables());
    }
}
