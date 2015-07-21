package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

public class PutResultTest {

    @Test
    public void createInsertResult() {
        final Uri insertedUri = mock(Uri.class);
        final Uri affectedUri = mock(Uri.class);

        final PutResult insertResult = PutResult.newInsertResult(insertedUri, affectedUri);

        assertTrue(insertResult.wasInserted());
        assertFalse(insertResult.wasUpdated());

        assertFalse(insertResult.wasNotInserted());
        assertTrue(insertResult.wasNotUpdated());

        assertSame(insertedUri, insertResult.insertedUri());
        assertSame(affectedUri, insertResult.affectedUri());

        assertNull(insertResult.numberOfRowsUpdated());
    }

    @Test
    public void shouldNotCreateInsertResultWithNullInsertedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(null, mock(Uri.class));
            fail();
        } catch (NullPointerException expected) {
            assertEquals("insertedUri must not be null", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateInsertResultWithNullAffectedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newInsertResult(mock(Uri.class), null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("affectedUri must not be null", expected.getMessage());
        }
    }

    @Test
    public void createUpdateResult() {
        final int numberOfRowsUpdated = 10;
        final Uri affectedUri = mock(Uri.class);

        final PutResult updateResult = PutResult.newUpdateResult(numberOfRowsUpdated, affectedUri);

        assertTrue(updateResult.wasUpdated());
        assertFalse(updateResult.wasInserted());

        assertFalse(updateResult.wasNotUpdated());
        assertTrue(updateResult.wasNotInserted());

        //noinspection ConstantConditions
        assertEquals(numberOfRowsUpdated, (int) updateResult.numberOfRowsUpdated());
        assertSame(affectedUri, updateResult.affectedUri());

        assertNull(updateResult.insertedUri());
    }

    @Test
    public void shouldAllowCreatingUpdateResultWith0RowsUpdated() {
        PutResult putResult = PutResult.newUpdateResult(0, mock(Uri.class));
        assertTrue(putResult.wasUpdated());
        assertFalse(putResult.wasInserted());
        assertEquals(Integer.valueOf(0), putResult.numberOfRowsUpdated());
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult(-1, mock(Uri.class));
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Number of rows updated must be >= 0", expected.getMessage());
        }
    }

    @Test
    public void shouldCreateUpdateResultWithOneRowUpdated() {
        PutResult.newUpdateResult(1, mock(Uri.class)); // no exceptions should occur
    }

    @Test
    public void shouldNotCreateUpdateResultWithNullAffectedUri() {
        try {
            //noinspection ConstantConditions
            PutResult.newUpdateResult(1, null);
            fail();
        } catch (NullPointerException expected) {
            assertEquals("affectedUri must not be null", expected.getMessage());
        }
    }

}
