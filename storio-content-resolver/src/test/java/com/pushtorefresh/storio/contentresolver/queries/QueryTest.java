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
import static org.assertj.core.api.Assertions.assertThat;
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

        assertThat(query.columns()).isNotNull();
        assertThat(query.columns()).isEmpty();
    }

    @Test
    public void whereClauseShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.where()).isEqualTo("");
    }

    @Test
    public void whereArgsShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.whereArgs()).isNotNull();
        assertThat(query.whereArgs()).isEmpty();
    }

    @Test
    public void sortOrderShouldNotBeNull() {
        Query query = Query.builder()
                .uri(mock(Uri.class))
                .build();

        assertThat(query.sortOrder()).isEqualTo("");
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

        assertThat(query.uri()).isEqualTo(uri);
        assertThat(query.columns()).isEqualTo(asList(columns));
        assertThat(query.where()).isEqualTo(where);
        assertThat(query.whereArgs()).isEqualTo(asList(whereArgs));
        assertThat(query.sortOrder()).isEqualTo(sortOrder);
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
        ToStringChecker
                .forClass(Query.class)
                .check();
    }
}
