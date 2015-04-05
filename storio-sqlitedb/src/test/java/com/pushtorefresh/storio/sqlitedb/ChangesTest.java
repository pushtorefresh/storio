package com.pushtorefresh.storio.sqlitedb;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
        assertEquals(1, changes.affectedTables().size());
        assertTrue(changes.affectedTables().contains("test_table"));
    }

    @Test
    public void newInstanceMultipleAffectedTables() {
        final Set<String> affectedTables = new HashSet<>();
        affectedTables.add("test_table_1");
        affectedTables.add("test_table_2");
        affectedTables.add("test_table_3");

        final Changes changes = Changes.newInstance(affectedTables);
        assertEquals(affectedTables, changes.affectedTables());
    }
}
