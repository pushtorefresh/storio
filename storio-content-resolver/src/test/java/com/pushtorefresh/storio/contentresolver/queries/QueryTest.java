package com.pushtorefresh.storio.contentresolver.queries;

import android.net.Uri;

import com.pushtorefresh.storio.contentresolver.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import nl.jqno.equalsverifier.EqualsVerifier;

import static com.pushtorefresh.storio.test.Tests.checkToString;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class) // Required for correct Uri impl
@Config(constants = BuildConfig.class, sdk = 21)
public class QueryTest {

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriObject() {
        //noinspection ConstantConditions
        Query.builder()
                .uri((Uri) null)
                .build();
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotAllowNullUriString() {
        //noinspection ConstantConditions
        Query.builder()
                .uri((String) null)
                .build();
    }

    @Test
    public void columnsShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.columns());
        assertTrue(query.columns().isEmpty());
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.where());
        assertEquals("", query.where());
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.whereArgs());
        assertTrue(query.whereArgs().isEmpty());
    }

    @Test
    public void sortOrderShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertNotNull(query.sortOrder());
        assertEquals("", query.sortOrder());
    }

    @Test
    public void buildWithNormalValues() {
        final Uri uri = mock(Uri.class);
        final String[] columns = {"1", "2", "3"};
        final String where = "test_where";
        final Object[] whereArgs = {"arg1", "arg2", "arg3"};
        final String sortOrder = "test_order";

        final Query query = Query.builder()
                .uri(uri)
                .columns(columns)
                .where(where)
                .whereArgs(whereArgs)
                .sortOrder(sortOrder)
                .build();

        assertEquals(uri, query.uri());
        assertEquals(asList(columns), query.columns());
        assertEquals(where, query.where());
        assertEquals(asList(whereArgs), query.whereArgs());
        assertEquals(sortOrder, query.sortOrder());
    }

    @Test
    public void verifyEqualsAndHashCodeImplementation() {
        EqualsVerifier
                .forClass(Query.class)
                .allFieldsShouldBeUsed()
                .withPrefabValues(Uri.class, Uri.parse("content://1"), Uri.parse("content://2"))
                .verify();
    }

    @Test
    public void checkToStringImplementation() {
        checkToString(Query.builder()
                        .uri("content://test")
                        .where("some_column = ? AND another_column = ?")
                        .whereArgs("1", "2")
                        .columns("some_column", "third_column")
                        .sortOrder("ASC")
                        .build()
        );
    }
}
