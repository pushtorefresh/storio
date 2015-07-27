package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;
import com.pushtorefresh.storio.test.ToStringChecker;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class UpdateQueryTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        //noinspection ConstantConditions
        UpdateQuery.builder()
                .uri((Uri) null)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        //noinspection ConstantConditions
        UpdateQuery.builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(updateQuery.where());
        assertEquals("", updateQuery.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(updateQuery.whereArgs());
        assertTrue(updateQuery.whereArgs().isEmpty());
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};

        final UpdateQuery updateQuery = UpdateQuery.builder()
                .uri(uri)
                .where(where)
                .whereArgs(whereArgs)
                .build();

        assertEquals(uri, updateQuery.uri());
        assertEquals(where, updateQuery.where());
        assertEquals(asList(whereArgs), updateQuery.whereArgs());
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(UpdateQuery.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        ToStringChecker
                .forClass(UpdateQuery.class)
                .check();
    }
}
