package com.pushtorefresh.storio.contentresolver.operations.delete;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import nl.jqno.equalsverifier.EqualsVerifier;

import static com.pushtorefresh.storio.test.Tests.checkToString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class DeleteResultsTest {

    @SuppressWarnings("ConstantConditions")
    @Test(expected = NullPointerException.class)
    public void nullResults() {
        DeleteResults.newInstance(null);
    }

    @Test
    public void resultsShouldBeEqual() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, mock(Uri.class)));
        results.put("testString2", DeleteResult.newInstance(1, mock(Uri.class)));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertEquals(results, deleteResults.results());
    }

    @Test
    public void checkWasDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        results.put("testString2", DeleteResult.newInstance(1, Uri.parse("content://testUri")));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertTrue(deleteResults.wasDeleted("testString1"));
        assertTrue(deleteResults.wasDeleted("testString2"));
        assertFalse(deleteResults.wasDeleted("testString3"));
    }

    @Test
    public void checkWasNotDeleted() {
        final Map<String, DeleteResult> results = new HashMap<String, DeleteResult>();
        results.put("testString1", DeleteResult.newInstance(1, Uri.parse("content://testUri")));
        results.put("testString2", DeleteResult.newInstance(1, Uri.parse("content://testUri")));

        final DeleteResults<String> deleteResults = DeleteResults.newInstance(results);

        assertFalse(deleteResults.wasNotDeleted("testString1"));
        assertFalse(deleteResults.wasNotDeleted("testString2"));
        assertTrue(deleteResults.wasNotDeleted("testString3"));
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(DeleteResults.class)
                .allFieldsShouldBeUsed()
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        checkToString(DeleteResults.newInstance(new HashMap<Object, DeleteResult>() {
            {
                put("key1", DeleteResult.newInstance(1, Uri.parse("content://testUri1")));
                put("key2", DeleteResult.newInstance(1, Uri.parse("content://testUri2")));
            }
        }));
    }
}
