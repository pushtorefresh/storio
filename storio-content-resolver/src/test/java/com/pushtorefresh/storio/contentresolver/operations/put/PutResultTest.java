package com.pushtorefresh.storio.contentresolver.operations.put;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static com.pushtorefresh.storio.test.Tests.checkToString;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
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
    public void shouldNotCreateUpdateResultWith0RowsUpdated() {
        try {
            PutResult.newUpdateResult(0, mock(Uri.class));
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Number of rows updated must be > 0", expected.getMessage());
        }
    }

    @Test
    public void shouldNotCreateUpdateResultWithNegativeNumberOfRowsUpdated() {
        try {
            PutResult.newUpdateResult(-1, mock(Uri.class));
            fail();
        } catch (IllegalStateException expected) {
            assertEquals("Number of rows updated must be > 0", expected.getMessage());
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

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(PutResult.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        checkToString(PutResult.newInsertResult(
                        Uri.parse("content://testInsertedUri"),
                        Uri.parse("content://testAffectedUri"))
        );
    }
}
