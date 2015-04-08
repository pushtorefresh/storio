package com.pushtorefresh.storio.sqlitedb.operation.delete;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DeleteResultTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = IllegalStateException.class)
    public void nullAffectedTable() {
        DeleteResult.newInstance(0, null);
    }

    @Test
    public void numberOfRowsDeleted() {
        final DeleteResult deleteResult = DeleteResult.newInstance(3, "test_table");
        assertEquals(3, deleteResult.numberOfRowsDeleted());
    }

    @Test
    public void affectedTable() {
        final DeleteResult deleteResult = DeleteResult.newInstance(2, "test_table");
        assertEquals("test_table", deleteResult.affectedTable());
    }
}
